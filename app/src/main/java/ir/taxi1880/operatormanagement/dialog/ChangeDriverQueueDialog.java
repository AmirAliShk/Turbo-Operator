package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class ChangeDriverQueueDialog {
    Dialog dialog;
    Unbinder unbinder;
    String driverCode;
    String position;

    @OnClick(R.id.imgClose)
    void onClosePress() {
        dismiss();
    }

    @BindView(R.id.imgClose)
    ImageView imgClose;

    @BindView(R.id.txtNumber)
    TextView txtNumber;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        position = txtNumber.getText().toString();
        if (position.isEmpty()) {
            MyApplication.Toast("لطفا اولویت راننده را وارد کنید.", Toast.LENGTH_SHORT);
            return;
        }
        setQueue(driverCode, position);
    }

    public void show(String driverCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_change_driver_queue);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        unbinder = ButterKnife.bind(this, dialog);
        dialog.setCancelable(false);
        this.driverCode = driverCode;
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
                    Boolean success = object.getBoolean("success");
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
        MyApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardHelper.hideKeyboard();
            }
        }, 30);
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.getMessage();
        }

        dialog = null;
    }

}
