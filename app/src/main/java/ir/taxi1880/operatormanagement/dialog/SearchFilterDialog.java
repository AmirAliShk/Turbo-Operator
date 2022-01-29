package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogSearchFilterBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SearchFilterDialog {

    private static final String TAG = SearchFilterDialog.class.getSimpleName();
    private static Dialog dialog;
    DialogSearchFilterBinding binding;
    SearchCaseListener searchCaseListener;

    public interface SearchCaseListener {
        void searchCase(int type);
    }

    public void show(String dialogType, SearchCaseListener searchCaseListener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogSearchFilterBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(true);
        KeyBoardHelper.hideKeyboard();
        this.searchCaseListener = searchCaseListener;

        if (dialogType.equals("passenger")) {
            binding.llPassengerFilter.setVisibility(View.VISIBLE);
            binding.llDriverFilter.setVisibility(View.GONE);
        } else {
            binding.llPassengerFilter.setVisibility(View.GONE);
            binding.llDriverFilter.setVisibility(View.VISIBLE);
        }

        binding.llDestinationAddress.setOnClickListener(view -> {
            searchCaseListener.searchCase(11);
            dismiss();
        });

        binding.llDriverDestinationAddress.setOnClickListener(view -> {
            searchCaseListener.searchCase(10);
            dismiss();
        });

        binding.llDriverStationCode.setOnClickListener(view -> {
            searchCaseListener.searchCase(9);
            dismiss();
        });

        binding.llDriverAddress.setOnClickListener(view -> {
            searchCaseListener.searchCase(8);
            dismiss();
        });

        binding.llDriverTaxiCode.setOnClickListener(view -> {
            searchCaseListener.searchCase(7);
            dismiss();
        });

        binding.llDriverMobile.setOnClickListener(view -> {
            searchCaseListener.searchCase(6);
            dismiss();
        });

        binding.llStationCode.setOnClickListener(view -> {
            searchCaseListener.searchCase(5);
            dismiss();
        });

        binding.llTaxiCode.setOnClickListener(view -> {
            searchCaseListener.searchCase(4);
            dismiss();
        });

        binding.llAddress.setOnClickListener(view -> {
            searchCaseListener.searchCase(3);
            dismiss();
        });

        binding.llTell.setOnClickListener(view -> {
            searchCaseListener.searchCase(2);
            dismiss();
        });

        binding.llName.setOnClickListener(view -> {
            searchCaseListener.searchCase(1);
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