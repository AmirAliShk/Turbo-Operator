package ir.taxi1880.operatormanagement.dialog;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.downloader.PRDownloader;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogPendingMistakesOptionsBinding;
import ir.taxi1880.operatormanagement.fragment.PassengerTripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PendingMistakesOptionsDialog {
    public static final String TAG = PendingMistakesOptionsDialog.class.getSimpleName();
    DialogPendingMistakesOptionsBinding binding;
    static Dialog dialog;
    String tell;
    String mobile;

    public void show(String tell, String mobile) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogPendingMistakesOptionsBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        this.tell = tell;
        this.mobile = mobile;

        binding.llPendingMistakesOptions.setOnClickListener(view -> {
            return;
        });

        binding.llGuestCalls.setOnClickListener(view -> {
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
        });

        binding.llSearchService.setOnClickListener(view -> {
            dismiss();

            Bundle bundle = new Bundle();
            bundle.putString("tellNumber", tell);
            FragmentHelper.toFragment(MyApplication.currentActivity, new PassengerTripSupportFragment()).setArguments(bundle).replace();
        });

        binding.llNextFollowUp.setOnClickListener(view -> {
            dismiss();
            MyApplication.Toast("llNextFollowUp", Toast.LENGTH_SHORT);
        });

        binding.llStationGuide.setOnClickListener(view -> {
            new SearchStationInfoDialog().show(stationCode -> {
            }, 0, false, "", false);
            dismiss();
        });

        binding.imgClose.setOnClickListener(view -> dismiss());
        binding.blrView.setOnClickListener(view -> dismiss());

        dialog.show();
    }

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