package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mmin18.widget.RealtimeBlurView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.TripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class TripOptionDialog {

    private static final String TAG = TripOptionDialog.class.getSimpleName();

    public interface Listener {
        void onClose(boolean b);
    }

    Listener listener;
    RealtimeBlurView blrView;
    static Dialog dialog;

    public void show(Listener listener, String mobile, String name, int cityCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_trip_option);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        LinearLayout llHire = dialog.findViewById(R.id.llHire);
        LinearLayout llSupport = dialog.findViewById(R.id.llSupport);
        LinearLayout llReserve = dialog.findViewById(R.id.llReserve);
        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        blrView = dialog.findViewById(R.id.blrView);

        blrView.setOnClickListener(view -> dismiss());

        if (MyApplication.prefManager.getCustomerSupport() == 1) {
            llSupport.setVisibility(View.VISIBLE);
            llSupport.setOnClickListener(view -> {
                FragmentHelper.toFragment(MyApplication.currentActivity, new TripSupportFragment()).replace();
                dismiss();
            });
        } else {
            llSupport.setVisibility(View.GONE);
        }

        llHire.setOnClickListener(view -> {
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

        llReserve.setOnClickListener(view -> {
            new ReserveDialog().show();
            dismiss();
        });

        imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "OptionDialog class, dismiss method");
        }
        dialog = null;
    }

}
