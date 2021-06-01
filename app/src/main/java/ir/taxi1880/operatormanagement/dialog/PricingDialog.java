package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
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

import static ir.taxi1880.operatormanagement.fragment.ComplaintDetailFragment.complaintDetailsModel;

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

    @BindView(R.id.rbByStation)
    RadioButton rbByStation;

    @BindView(R.id.rbByTime)
    RadioButton rbByTime;

    @BindView(R.id.llPrice)
    LinearLayout llPrice;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (rbByStation.isChecked()) {
            if (edtStationOrigin.getText().toString().equals("") || edtStationDestination.getText().toString().equals("")) {
                MyApplication.Toast("لطفا ایستگاه را وارد نمایید.", Toast.LENGTH_SHORT);
            }
        }

        if (rbByTime.isChecked()) {
            if (edtTime.getText().toString().equals("")) {
                MyApplication.Toast("لطفا زمان را به دقیقه وارد کنید.", Toast.LENGTH_SHORT);
            }
        }

        if (!rbByTime.isChecked() && !rbByStation.isChecked()) {
            MyApplication.Toast("لطفا یکی از حالت‌ها را انتخاب کنید.", Toast.LENGTH_SHORT);
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

        edtStationDestination.setEnabled(false);
        edtStationOrigin.setEnabled(false);
        edtTime.setEnabled(false);

        llPrice.setVisibility(View.GONE);

//        edtStationDestination.setFocusable(false);
//        edtStationOrigin.setFocusable(false);
//        edtTime.setFocusable(false);

        rbByStation.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbByStation.isChecked()) {
                rbByTime.setChecked(false);
                edtStationDestination.setEnabled(true);
                edtStationOrigin.setEnabled(true);
                edtTime.setEnabled(false);
                edtStationDestination.setFocusable(true);
                edtStationOrigin.setFocusable(true);
            }
        });

        rbByTime.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbByTime.isChecked()) {
                rbByStation.setChecked(false);
                edtStationDestination.setEnabled(false);
                edtStationOrigin.setEnabled(false);
                edtTime.setEnabled(true);
                edtTime.setFocusable(true);
            }
        });

        dialog.show();
    }

    private void getPricing() {
        String StationOrigin;
        String StationDestination;
        String time;
        if (rbByTime.isChecked()) {
            StationOrigin = "0";
            StationDestination = "0";
        } else {
            StationOrigin = edtStationOrigin.getText().toString();
            StationDestination = edtStationDestination.getText().toString();
        }
        if (rbByStation.isChecked()) {
            time = "0";
        } else {
            time = edtTime.getText().toString();
        }
        RequestHelper.builder(EndPoints.COMPLAINT_GET_PRICE + complaintDetailsModel.getCityCode() + "/" + complaintDetailsModel.getCarClass() + "/" + StationOrigin + "/" + StationDestination + "/" + time)
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
            MyApplication.handler.post(() -> {
                if (vfLoader != null) {
                    vfLoader.setDisplayedChild(0);
                }
            });
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
