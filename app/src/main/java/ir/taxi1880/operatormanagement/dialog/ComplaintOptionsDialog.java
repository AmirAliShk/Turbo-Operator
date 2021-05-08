package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.downloader.PRDownloader;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

public class ComplaintOptionsDialog {
    Unbinder unbinder;
    static Dialog dialog;
    String customerTell;
    String customerMobile;
    int taxiCode;

    @OnClick(R.id.blrView)
    void onBlur() {
        dismiss();
    }

    @OnClick(R.id.llComplaintOptions)
    void onPendingMistakesOptions() {
        return;
    }

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    @OnClick(R.id.llCustomerCalls)
    void onPressGuestCalls() {
        dismiss();
        new RecentCallsDialog()
                .show(customerTell, customerMobile, 0, true, (b) -> {
                    if (b) {
                        MyApplication.handler.postDelayed(() -> {
                            PRDownloader.cancelAll();
                            PRDownloader.shutDown();
                            pauseVoice();
                        }, 500);
                    }
                });
    }

    @OnClick(R.id.llDriverComplaintsHistory)
    void onDriverComplaintsHistory() {
        dismiss();
        new ComplaintsHistoryDialog()
                .show("driver", taxiCode, customerTell, customerMobile);
    }

    @OnClick(R.id.llCustomerComplaintsHistory)
    void onCustomerComplaintsHistory() {
        dismiss();
       new ComplaintsHistoryDialog()
                .show("customer", taxiCode, customerTell, customerMobile);
    }

    public void show(String customerTell, String customerMobile, int taxiCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_complaint_options);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
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

        dialog.show();
    }

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
