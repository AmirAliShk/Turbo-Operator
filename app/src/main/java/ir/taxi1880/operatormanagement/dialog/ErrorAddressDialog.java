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

public class ErrorAddressDialog {

  private static final String TAG = ErrorAddressDialog.class.getSimpleName();

  static Dialog dialog;

  public void show(String passengerAddress) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_edit_address);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(false);

    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
    EditText edtAddress = dialog.findViewById(R.id.edtAddress);

    edtAddress.setText(passengerAddress);

    imgClose.setOnClickListener(view -> dismiss());

    btnSubmit.setOnClickListener(view -> {
      KeyBoardHelper.hideKeyboard();
      String address = edtAddress.getText().toString();

      if (address.isEmpty()) {
        edtAddress.setError("آدرس نباید خالی باشد");
        return;
      }

      editAddress();
    });

    dialog.show();
  }

  private void editAddress() {
    LoadingDialog.makeCancelableLoader();
    RequestHelper.builder(EndPoints.EDIT_ADDRESS)
            .addParam("serviceId",0)
            .addParam("adrs",0)
            .listener(onEditAddress)
            .post();
  }

  RequestHelper.Callback onEditAddress = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          JSONObject object = new JSONObject(args[0].toString());
          boolean success = object.getBoolean("success");
          String message = object.getString("message");
          JSONObject dataObj = object.getJSONObject("data");
          boolean status = dataObj.getBoolean("status");

          if (status) {
            dismiss();
            new GeneralDialog()
                    .title("تایید شد")
                    .message(message)
                    .cancelable(false)
                    .firstButton("باشه", null)
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

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      AvaCrashReporter.send(e, "ErrorAddressDialog class, dismiss method");
    }
    dialog = null;
  }

}
