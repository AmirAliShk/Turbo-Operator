package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogChangeDriverQueueBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ChangeDriverQueueDialog {

    public static final String TAG = ChangeDriverQueueDialog.class.getSimpleName();
    DialogChangeDriverQueueBinding binding;
    Dialog dialog;
    String driverCode;
    String position;

    public void show(String driverCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogChangeDriverQueueBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);
        this.driverCode = driverCode;

        binding.btnSubmit.setOnClickListener(view -> {
            position = binding.txtNumber.getText().toString();
            if (position.isEmpty()) {
                MyApplication.Toast("لطفا اولویت راننده را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }
            setQueue(driverCode, position);
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private void setQueue(String driverCode, String position) {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.DRIVER_STATION_POSITION)
                .ignore422Error(true)
                .addParam("driverCode", driverCode)
                .addParam("position", position)
                .listener(onChangeQueue)
                .put();
    }

    private RequestHelper.Callback onChangeQueue = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        new GeneralDialog()
                                .title("تایید شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", () -> dismiss())
                                .show();
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                } catch (Exception e) {
                    LoadingDialog.dismissCancelableDialog();
                    MyApplication.Toast("خطا در ثبت اولویت", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onChangeQueue method");
                }
            });
//                {"success": true,
//                    "message": "عملیات با موفقیت انجام شد",
//                    "data": {
//                "status": true}
//               }
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            LoadingDialog.dismissCancelableDialog();
            MyApplication.Toast("خطا در ثبت اولویت", Toast.LENGTH_SHORT);
            super.onFailure(reCall, e);
        }
    };

    private void dismiss() {
        MyApplication.handler.postDelayed(KeyBoardHelper::hideKeyboard, 30);
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }

        dialog = null;
    }
}