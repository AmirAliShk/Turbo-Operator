package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class DescriptionDialog {

    private static final String TAG = DescriptionDialog.class.getSimpleName();

    public interface Listener {
        void description(String description);
        void fixedDescription(String fixedDescription);

    }

    private Listener listener;

    static Dialog dialog;

    public void show(Listener listener,String description,String normalDescription) {
        if (MyApplication.currentActivity==null)return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_description);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        EditText edtAlwaysDescription = dialog.findViewById(R.id.edtAlwaysDescription);
        EditText edtDescription = dialog.findViewById(R.id.edtDescription);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        edtAlwaysDescription.setText(description);
        edtDescription.setText(normalDescription);

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String description=edtDescription.getText().toString();
              String fixedDescription=edtAlwaysDescription.getText().toString();

//              if (description.isEmpty()){
//                  MyApplication.Toast("حداقل یکی از فیلدهای توضیحات را پر کنید", Toast.LENGTH_SHORT);
//                  return;
//              }

              listener.description(description);
              listener.fixedDescription(fixedDescription);
              dismiss();

          }
      });
        MyApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardHelper.showKeyboard(MyApplication.context);

            }
        }, 200);

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null){
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
        }
        dialog = null;
    }

}
