package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogTripOptionBinding;
import ir.taxi1880.operatormanagement.fragment.PassengerTripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class TripOptionDialog {

    private static final String TAG = TripOptionDialog.class.getSimpleName();
    DialogTripOptionBinding binding;

    public interface Listener {
        void onClose(boolean b);
    }

    Listener listener;
    static Dialog dialog;

    public void show(Listener listener, String mobile, String name, int cityCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogTripOptionBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        this.listener = listener;

        binding.blrView.setOnClickListener(view -> dismiss());

        binding. llTripOption.setOnClickListener(view -> {
            return;
        });

        if (MyApplication.prefManager.getCustomerSupport() == 1) {
            binding.llSupport.setVisibility(View.VISIBLE);
            binding.llSupport.setOnClickListener(view -> {
                FragmentHelper.toFragment(MyApplication.currentActivity, new PassengerTripSupportFragment()).replace();
                dismiss();
            });
        } else {
            binding.llSupport.setVisibility(View.GONE);
        }

        binding.llHire.setOnClickListener(view -> {
            if (mobile.isEmpty()) {
                MyApplication.Toast("لطفا شماره موبایل را وارد کنید", Toast.LENGTH_SHORT);
                dismiss();
                return;
            }
            if (name.isEmpty()) {
                MyApplication.Toast("لطفا نام و نام خانوادگی را وارد کنید", Toast.LENGTH_SHORT);
                dismiss();
                return;
            }
            if (cityCode == 0) {
                MyApplication.Toast("لطفا شهر را مشخص کنید", Toast.LENGTH_SHORT);
                dismiss();
                return;
            }
            //            if (b) {
            //            }
            new HireDialog().show(listener::onClose, mobile, name, cityCode);
            dismiss();
        });

        binding.llReserve.setOnClickListener(view -> {
            new ReserveDialog().show();
            dismiss();
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

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