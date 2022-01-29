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
import ir.taxi1880.operatormanagement.databinding.DialogRequestBinding;
import ir.taxi1880.operatormanagement.fragment.ReplacementWaitingFragment;
import ir.taxi1880.operatormanagement.fragment.SendReplacementReqFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class RequestDialog {

    public static final String TAG = RequestDialog.class.getSimpleName();
    DialogRequestBinding binding;
    static Dialog dialog;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogRequestBinding.inflate(LayoutInflater.from(dialog.getContext()));
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

        binding.llGetRequest.setOnClickListener(view -> {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new ReplacementWaitingFragment())
                    .replace();
            dismiss();
        });

        binding.llSendRequest.setOnClickListener(view -> {
            FragmentHelper.toFragment(MyApplication.currentActivity, new SendReplacementReqFragment()).replace();
            dismiss();
        });

        binding.llRequest.setOnClickListener(view -> {
            return;
        });

        binding.blrView.setOnClickListener(view -> dismiss());

        binding.btnClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();

            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}