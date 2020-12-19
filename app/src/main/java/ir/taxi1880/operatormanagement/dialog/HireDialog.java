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
import ir.taxi1880.operatormanagement.model.HireTypeModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class HireDialog {

  private static final String TAG = HireDialog.class.getSimpleName();

  public interface Listener {
    void onClose(boolean b);
  }

  Listener listener;

  private Spinner spHireType;
  private int hireType;

  static Dialog dialog;

  public void show(Listener listener, String mobile, String name, int cityCode) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_hire);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    getHireType();

    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
    EditText edtComment = dialog.findViewById(R.id.edtComment);
    spHireType = dialog.findViewById(R.id.spHireType);

    imgClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new GeneralDialog()
                .title("استخدامی")
                .message("آیا از ثبت درخواست اطمینان دارید؟")
                .firstButton("بله", () ->
                        setHire(MyApplication.prefManager.getUserCode(), name, mobile, edtComment.getText().toString(), hireType, cityCode))
                .secondButton("خیر", null)
                .show();
      }
    });

    dialog.show();
  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
      AvaCrashReporter.send(e,"HireDialog class, dismiss method");
    }
    dialog = null;
  }

  private void setHire(int userId, String name, String phoneNumber, String comment, int hireType, int cityCode) {

    RequestHelper.builder(EndPoints.HIRE)
            .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
            .addHeader("id_token", MyApplication.prefManager.getIdToken())
            .addParam("userId", userId)
            .addParam("phoneNumber", phoneNumber)
            .addParam("cityCode", cityCode)
            .addParam("name", name)
            .addParam("comment", comment)
            .addParam("type", hireType)
            .listener(setHire)
            .post();

  }

  RequestHelper.Callback setHire = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject data = obj.getJSONObject("data");
            boolean status = data.getBoolean("status");

            if (success) {
              new GeneralDialog()
                      .title("ثبت شد")
                      .message("درخواست استخدامی شما با موفقیت ثبت شد")
                      .firstButton("باشه", new Runnable() {
                        @Override
                        public void run() {
                          dismiss();
                          listener.onClose(true);
                        }
                      })
                      .show();
            }

          } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e,"HireDialog class, setHire onResponse method");
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) { }

  };

  private void getHireType() {
    RequestHelper.builder(EndPoints.HIRETYPES)
            .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
            .addHeader("id_token", MyApplication.prefManager.getIdToken())
            .listener(getHireType)
            .get();
  }

  RequestHelper.Callback getHireType = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          ArrayList<HireTypeModel> hireTypeModels = new ArrayList<>();
          ArrayList<String> hireTypes = new ArrayList<String>();
          try {
            Log.i(TAG, "run: " + args[0].toString());
            JSONObject hireObj = new JSONObject(args[0].toString());
            boolean success = hireObj.getBoolean("success");
            String message = hireObj.getString("message");

            JSONArray data = hireObj.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
              JSONObject obj = data.getJSONObject(i);
              HireTypeModel hireTypeModel = new HireTypeModel();
              hireTypeModel.setName(obj.getString("name"));
              hireTypeModel.setId(obj.getInt("id"));
              hireTypeModels.add(hireTypeModel);
              hireTypes.add(obj.getString("name"));
            }
            if (spHireType != null) {
              spHireType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner_right, hireTypes));
              spHireType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  hireType = hireTypeModels.get(position).getId();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
              });
            }
          } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e,"HireDialog class, getHireType onResponse method");
          }

        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) { }

  };

}
