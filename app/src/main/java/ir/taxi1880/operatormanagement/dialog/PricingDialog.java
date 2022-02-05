package ir.taxi1880.operatormanagement.dialog;

import static ir.taxi1880.operatormanagement.fragment.ComplaintDetailFragment.complaintDetailsModel;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogPricingBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PricingDialog {

    public static final String TAG = PricingDialog.class.getSimpleName();
    DialogPricingBinding binding;
    Dialog dialog;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogPricingBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        dialog.setCancelable(true);

        binding.edtStationOrigin.setEnabled(false);
        binding.edtStationDestination.setEnabled(false);
        binding.edtTime.setEnabled(false);

        binding.llPrice.setVisibility(View.GONE);

        MyApplication.handler.postDelayed(() -> binding.rbByStation.setChecked(true), 100);

        binding.rbByStation.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbByStation.isChecked()) {
                binding.rbByTime.setChecked(false);
                binding.edtStationDestination.setEnabled(true);
                binding.edtStationOrigin.setEnabled(true);
                binding.edtTime.setEnabled(false);
                binding.edtTime.setText("");
            }
        });

        binding.rbByTime.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbByTime.isChecked()) {
                binding.rbByStation.setChecked(false);
                binding.edtTime.setEnabled(true);
                binding.edtStationDestination.setText("");
                binding.edtStationOrigin.setText("");
                binding.edtStationDestination.setEnabled(false);
                binding.edtStationOrigin.setEnabled(false);
            }
        });

        binding.btnSubmit.setOnClickListener(view -> {
            if (binding.rbByStation.isChecked()) {
                if (binding.edtStationOrigin.getText().toString().equals("") || binding.edtStationDestination.getText().toString().equals("")) {
                    MyApplication.Toast("لطفا ایستگاه را وارد نمایید.", Toast.LENGTH_SHORT);
                }
            }

            if (binding.rbByTime.isChecked()) {
                if (binding.edtTime.getText().toString().equals("")) {
                    MyApplication.Toast("لطفا زمان را به دقیقه وارد کنید.", Toast.LENGTH_SHORT);
                }
            }

            if (!binding.rbByTime.isChecked() && !binding.rbByStation.isChecked()) {
                MyApplication.Toast("لطفا یکی از حالت‌ها را انتخاب کنید.", Toast.LENGTH_SHORT);
            }
//            if (binding.vfLoader != null)
            binding.vfLoader.setDisplayedChild(1);
            getPricing();
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private void getPricing() {
        String StationOrigin;
        String StationDestination;
        String time;
        if (binding.rbByTime.isChecked()) {
            StationOrigin = "0";
            StationDestination = "0";
        } else {
            StationOrigin = binding.edtStationOrigin.getText().toString();
            StationDestination = binding.edtStationDestination.getText().toString();
        }
        if (binding.rbByStation.isChecked()) {
            time = "0";
        } else {
            time = binding.edtTime.getText().toString();
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
//                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);

//{"success":true,"message":"عملیات با موفقیت انجام شد","data":{"duration":20.25,"distance":8542,"stopTime":0,"tripPrice":21000,"finalPrice":21000,"discount":0,"distancePrice":11958.8,"stopTimePrice":2025,"incomePrice":7000}}
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        binding.txtPrice.setText(dataObj.getInt("tripPrice") + "");
                        binding.llPrice.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, pricingCallBack method");
//                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);
                        binding.llPrice.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
            MyApplication.handler.post(() -> {
//                if (binding.vfLoader != null)
                    binding.vfLoader.setDisplayedChild(0);
                    binding.llPrice.setVisibility(View.GONE);
            });
        }
    };

    private void dismiss() {
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