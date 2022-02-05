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
import ir.taxi1880.operatormanagement.databinding.DialogDescriptionBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DescriptionDialog {
    private static final String TAG = DescriptionDialog.class.getSimpleName();
    DialogDescriptionBinding binding;

    public interface Listener {
        void description(String description);

        void fixedDescription(String fixedDescription);
    }

//    private Listener listener;

    static Dialog dialog;

    public void show(Listener listener, String description, String normalDescription) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogDescriptionBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
//        this.listener = listener;

        binding.llDescription.setOnClickListener(view -> {
            return;
        });
        binding.edtAlwaysDescription.setText(description);
        binding.edtDescription.setText(normalDescription);

        binding.blrView.setOnClickListener(view -> dismiss());
//        InputFilter filter = new InputFilter() {
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                boolean temp = false;
//                for (int i = start; i < end; i++) {
//                    if (StringHelper.charCheck(source.charAt(i))) {
//                        temp = true;
//                    }
//
//                }
//                if (!temp) return "";
//                return null;
//            }
//        };
//        edtAlwaysDescription.setFilters(new InputFilter[]{filter});

        binding.btnSubmit.setOnClickListener(v -> {
            String description1 = binding.edtDescription.getText().toString();
            String fixedDescription = binding.edtAlwaysDescription.getText().toString();

//              if (description.isEmpty()){
//                  MyApplication.Toast("حداقل یکی از فیلدهای توضیحات را پر کنید", Toast.LENGTH_SHORT);
//                  return;
//              }

            listener.description(description1);
            listener.fixedDescription(fixedDescription);
            dismiss();
        });
        MyApplication.handler.postDelayed(() -> KeyBoardHelper.showKeyboard(MyApplication.context), 200);

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