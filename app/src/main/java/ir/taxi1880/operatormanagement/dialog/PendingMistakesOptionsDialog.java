package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.downloader.PRDownloader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.PassengerTripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

public class PendingMistakesOptionsDialog {
    Unbinder unbinder;
    static Dialog dialog;
    String tell;
    String mobile;


    @OnClick(R.id.blrView)
    void onBlur() {
        dismiss();
    }

    @OnClick(R.id.llPendingMistakesOptions)
    void onPendingMistakesOptions() {
        return;
    }

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    @OnClick(R.id.llGuestCalls)
    void onPressGuestCalls() {
        dismiss();
        new RecentCallsDialog()
                .show(tell, mobile, 0, true, (b) -> {
                    if (b) {
                        MyApplication.handler.postDelayed(() -> {
                            PRDownloader.cancelAll();
                            PRDownloader.shutDown();
                            pauseVoice();
                        }, 500);
                    }
                });
    }

    @OnClick(R.id.llSearchService)
    void onPressSearchService() {
        dismiss();

        Bundle bundle = new Bundle();
        bundle.putString("tellNumber", tell);
        FragmentHelper.toFragment(MyApplication.currentActivity, new PassengerTripSupportFragment()).setArguments(bundle).replace();
    }

    @OnClick(R.id.llNextFollowUp)
    void onPressNestFollowUp() {
        dismiss();
        MyApplication.Toast("llNextFollowUp", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.llStationGuide)
    void onStationInfo() {
        new SearchStationInfoDialog().show(stationCode -> {
        }, 0, false, "", false);
        dismiss();
    }
    @BindView(R.id.llGuestCalls)
    LinearLayout llGuestCall;

    public void show(String tell, String mobile) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_pending_mistakes_options);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.tell = tell;
        this.mobile = mobile;

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
