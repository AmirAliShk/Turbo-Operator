package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class LostDialog {

  private static final String TAG = LostDialog.class.getSimpleName();

  private Spinner spType;
  int type;

  static Dialog dialog;

  public void show(String serviceId, String name, String phone, String carCode) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_lost);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(false);

    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
    EditText edtComment = dialog.findViewById(R.id.edtComment);
    EditText edtAddress = dialog.findViewById(R.id.edtAddress);
    spType = dialog.findViewById(R.id.spType);

    initSpinner();

    imgClose.setOnClickListener(view -> dismiss());

    btnSubmit.setOnClickListener(view -> {
      KeyBoardHelper.hideKeyboard();

      String address = edtAddress.getText().toString();
      String comment = edtComment.getText().toString();

      if (address.isEmpty()) {
        MyApplication.Toast("لطفا آدرس را وارد کنید", Toast.LENGTH_SHORT);
        return;
      }

      setLostObject(serviceId, carCode, phone, name, address, comment);
      dismiss();

    });

    dialog.show();
  }

  private void setLostObject(String serviceId, String carCode, String passengerPhone, String passengerName, String address, String description) {
    LoadingDialog.makeCancelableLoader();
    RequestHelper.builder(EndPoints.INSERT_LOST_OBJECT)
            .addParam("carCode", carCode)
            .addParam("serviceId", serviceId)
            .addParam("objectType", type)
            .addParam("passengerPhone", passengerPhone)
            .addParam("passengerName", passengerName)
            .addParam("adrs", address)
            .addParam("description", description)
            .addParam("userId", MyApplication.prefManager.getUserCode())
            .listener(onSetLostObject)
            .post();
  }

  RequestHelper.Callback onSetLostObject = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          Log.i("TripDetailsFragment", "run: " + args[0].toString());
          JSONObject object = new JSONObject(args[0].toString());
          boolean success = object.getBoolean("success");
          String message = object.getString("message");
          JSONObject dataObj = object.getJSONObject("data");
          boolean status = dataObj.getBoolean("status");

          if (status) {
            new GeneralDialog()
                    .title("تایید شد")
                    .message(message)
                    .cancelable(false)
                    .firstButton("باشه",null)
                    .show();
          }else {
            new GeneralDialog()
                    .title("خطا")
                    .message(message)
                    .cancelable(false)
                    .firstButton("باشه", null)
                    .show();
          }
          LoadingDialog.dismissCancelableDialog();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
        LoadingDialog.dismissCancelableDialog();
      });
    }
  };

  private void initSpinner() {
    ArrayList<TypeServiceModel> typeServiceModels = new ArrayList<>();
    ArrayList<String> serviceList = new ArrayList<String>();
    try {
      JSONArray serviceArr = new JSONArray(MyApplication.prefManager.getObjectsType());
      for (int i = 0; i < serviceArr.length(); i++) {
        JSONObject serviceObj = serviceArr.getJSONObject(i);
        TypeServiceModel typeServiceModel = new TypeServiceModel();
        typeServiceModel.setName(serviceObj.getString("KTypeSharh"));
        typeServiceModel.setId(serviceObj.getInt("KTypeId"));
        typeServiceModels.add(typeServiceModel);
        serviceList.add(serviceObj.getString("KTypeSharh"));
      }
      if (spType == null)
        return;

      spType.setEnabled(true);

      spType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

      spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          type = typeServiceModels.get(position).getId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
      AvaCrashReporter.send(e, "LostDialog class, dismiss method");
    }
    dialog = null;
  }

}
