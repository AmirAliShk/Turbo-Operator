package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogExtendedTimeBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ExtendedTimeDialog {

    private static final String TAG = ExtendedTimeDialog.class.getSimpleName();
    private static Dialog dialog;
    DialogExtendedTimeBinding binding;
    ExtendedTimeListener extendedTime;

    public interface ExtendedTimeListener {
        void extendTime(int type, String title, int icon);
    }

    public void show(ExtendedTimeListener searchCaseListener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogExtendedTimeBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        KeyBoardHelper.hideKeyboard();
        this.extendedTime = searchCaseListener;

        binding.llToday.setOnClickListener(view -> {
            extendedTime.extendTime(1, "امروز", R.drawable.ic_today); // today
            dismiss();
        });

        binding.llYesterday.setOnClickListener(view -> {
            extendedTime.extendTime(2, "دیروز", R.drawable.ic_yesterday); // yesterday
            dismiss();
        });

        binding.llTwoDayAgo.setOnClickListener(view -> {
            extendedTime.extendTime(3, "دوروز قبل", R.drawable.ic_twodaysago); // two day ago
            dismiss();
        });

        binding.blrView.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                if (dialog.isShowing())
                    dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
    }
}