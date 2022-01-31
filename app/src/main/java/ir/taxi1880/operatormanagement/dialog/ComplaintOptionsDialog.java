package ir.taxi1880.operatormanagement.dialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.downloader.PRDownloader;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapterK;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogComplaintOptionsBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ComplaintOptionsDialog {

    public static final String TAG = ComplaintOptionsDialog.class.getSimpleName();
    DialogComplaintOptionsBinding binding;
    Dialog dialog;
    String customerTell;
    String customerMobile;
    int taxiCode;

    public void show(String customerTell, String customerMobile, int taxiCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogComplaintOptionsBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        this.customerTell = customerTell;
        this.customerMobile = customerMobile;
        this.taxiCode = taxiCode;

        binding.llPricing.setOnClickListener(view -> {
            dismiss();
            new PricingDialog()
                    .show();
        });

        binding.llCustomerComplaintsHistory.setOnClickListener(view -> {
            dismiss();
            new ComplaintsHistoryDialog()
                    .show("customer", taxiCode, customerTell, customerMobile);
        });

        binding.llDriverComplaintsHistory.setOnClickListener(view -> {
            dismiss();
            new ComplaintsHistoryDialog()
                    .show("driver", taxiCode, customerTell, customerMobile);
        });

        binding.llCustomerCalls.setOnClickListener(view -> {
            dismiss();
            new RecentCallsDialog()
                    .show(customerTell, customerMobile, 0, true, (b) -> {
                        if (b) {
                            MyApplication.handler.postDelayed(() -> {
                                PRDownloader.cancelAll();
                                PRDownloader.shutDown();
                                RecentCallsAdapterK.Companion.pauseVoice();
//                                pauseVoice();
                            }, 500);
                        }
                    });
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        binding.llComplaintOptions.setOnClickListener(view -> {
            return;
        });

        binding.blrView.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}