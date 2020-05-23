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

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class OptionDialog {

  private static final String TAG = OptionDialog.class.getSimpleName();

  public interface Listener {
    void onClose(boolean b);
  }

  Listener listener;

  static Dialog dialog;

  public void show(Listener listener, String mobile, String name, int cityCode) {
    if (MyApplication.currentActivity==null)return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_option);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    LinearLayout llHire = dialog.findViewById(R.id.llHire);
    LinearLayout llReserve = dialog.findViewById(R.id.llReserve);
    ImageView imgClose = dialog.findViewById(R.id.imgClose);

    llHire.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
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
        new HireDialog().show(new HireDialog.Listener() {
          @Override
          public void onClose(boolean b) {
//            if (b) {

              listener.onClose(b);
//            }
          }
        }, mobile, name, cityCode);
        dismiss();
      }
    });

    llReserve.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new ReserveDialog().show();
        dismiss();
      }
    });

    imgClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });


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
    }
    dialog = null;
  }

}
