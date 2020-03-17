package ir.taxi1880.operatormanagement.dialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;


public class LoadingDialog {

  private static final String TAG = LoadingDialog.class.getSimpleName();
  private static Dialog ldialog;


  public static void makeLoader() {
    if (ldialog != null) return;
    try {

      if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
        return;

      ldialog = new Dialog(MyApplication.currentActivity);
      ldialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      ldialog.setContentView(R.layout.dialog_loder);
      ldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      WindowManager.LayoutParams wlp = ldialog.getWindow().getAttributes();
      ldialog.getWindow().setAttributes(wlp);
      // ldialog.setCancelable(false);
      ldialog.show();

    } catch (Exception e) {
      Log.e(TAG, "makeLoader: " + e);
    }
  }


  public static void dismiss() {
    try {

      if (ldialog != null)
        ldialog.dismiss();
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(TAG, "dismiss: " + e.getMessage());
    }
    ldialog = null;
  }

}