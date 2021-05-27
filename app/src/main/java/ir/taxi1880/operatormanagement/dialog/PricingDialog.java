package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ViewFlipper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PricingDialog {

    Unbinder unbinder;
    Dialog dialog;

    @BindView(R.id.edtStationOrigin)
    EditText edtStationOrigin;

    @BindView(R.id.edtStationDestination)
    EditText edtStationDestination;

    @BindView(R.id.edtTime)
    EditText edtTime;

    @BindView(R.id.vfLoader)
    ViewFlipper vfLoader;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (edtStationOrigin.getText().toString().equals("") || edtStationDestination.getText().toString().equals("")){

        }
            if (vfLoader != null) {
                vfLoader.setDisplayedChild(1);
            }
        getPricing();
    }

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_pricing);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        dialog.show();
    }

    private void getPricing() {
        RequestHelper.builder(EndPoints.COMPLAINT_GET_PRICE + edtStationOrigin.getText() + "/" + edtStationDestination.getText() + "/" + edtTime.getText())
                .listener(pricingCallBack)
                .get();
    }

    RequestHelper.Callback pricingCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (vfLoader != null) {
                        vfLoader.setDisplayedChild(0);
                    }
//                    {"success":true,"message":"عملیات با موفقیت انجام شد","data":"ok"}

                } catch (Exception e) {
                    e.printStackTrace();
                    if (vfLoader != null) {
                        vfLoader.setDisplayedChild(0);
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
            if (vfLoader != null) {
                vfLoader.setDisplayedChild(0);
            }
        }
    };

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "ReserveDialog class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}
