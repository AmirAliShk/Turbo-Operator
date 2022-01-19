package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class EditFinancialDialog {
    View blrView;
    private static final String TAG = EditFinancialDialog.class.getSimpleName();

    static Dialog dialog;

    public void show(String driverId, String carCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_edit_financial);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        EditText edtDescription = dialog.findViewById(R.id.edtDescription);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        blrView = dialog.findViewById(R.id.blrView);
        edtDescription.requestFocus();
        blrView.setOnClickListener(view -> dismiss());

        btnSubmit.setOnClickListener(v -> {
            String description = edtDescription.getText().toString();

            EditFinancial(driverId, description, carCode);
            dismiss();
        });
        MyApplication.handler.postDelayed(() -> KeyBoardHelper.showKeyboard(MyApplication.context), 200);

        dialog.show();
    }

    private void EditFinancial(String driverId, String description, String carCode) {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.DRIVER_EDIT_FINANCIAL)
                .addParam("carCode", carCode)
                .addParam("driverCode", driverId)
                .addParam("comment", description)
                .listener(EditFinancialCallBack)
                .post();
    }

    RequestHelper.Callback EditFinancialCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        new GeneralDialog()
                                .title("تایید شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .secondButton("باشه", null)
                                .show();
                    }
                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, EditFinancialCallBack");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }

}