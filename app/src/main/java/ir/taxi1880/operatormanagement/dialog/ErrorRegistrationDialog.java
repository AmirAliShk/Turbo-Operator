package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ErrorRegistrationDialog {

  private static final String TAG = ErrorRegistrationDialog.class.getSimpleName();

  static Dialog dialog;

  public void show(String ServiceId, String phone, String address, String customerName, String voipId) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_error_registration);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);

    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
    EditText edtErrorText = dialog.findViewById(R.id.edtErrorText);

    imgClose.setOnClickListener(view -> dismiss());

    btnSubmit.setOnClickListener(view -> {
      KeyBoardHelper.hideKeyboard();
      String description = edtErrorText.getText().toString();

      if (description.isEmpty()) {
        edtErrorText.setError("متن خطا را وارد کنید");
        return;
      }

      setMistake(ServiceId, phone, address, customerName, voipId, description);
      dismiss();
    });

    dialog.show();
  }

  private void setMistake(String ServiceId, String phone, String address, String customerName, String voipId, String desc) {

    RequestHelper.builder(EndPoints.INSERT_MISTAKE)
            .addParam("serviceId", ServiceId)
            .addParam("userId", MyApplication.prefManager.getUserCode())
            .addParam("phone", phone)
            .addParam("adrs", address)
            .addParam("customerName", customerName)
            .addParam("voipId", voipId)
            .addParam("description", desc)
            .listener(onSetMistake)
            .post();
  }

  RequestHelper.Callback onSetMistake = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());
            JSONObject object=new JSONObject(args[0].toString());
            boolean success=object.getBoolean("success");
            String message=object.getString("message");
            JSONObject dataObj = object.getJSONObject("data");
            boolean status=dataObj.getBoolean("status");

            //TODO test after update server

            if (status){
              new GeneralDialog()
                      .title("تایید شد")
                      .message("عملیات با موفقیت انجام شد")
                      .cancelable(false)
                      .firstButton("باشه", null)
                      .show();
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {

      });
    }
  };

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      AvaCrashReporter.send(e, "ErrorRegistrationDialog class, dismiss method");
    }
    dialog = null;
  }

}
