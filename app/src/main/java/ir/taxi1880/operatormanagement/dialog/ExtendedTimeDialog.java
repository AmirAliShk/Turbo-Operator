package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ExtendedTimeDialog {

  private static final String TAG = ExtendedTimeDialog.class.getSimpleName();
  private static Dialog dialog;
  static Unbinder unbinder;
  ExtendedTimeListener extendedTime;

  public interface ExtendedTimeListener {
    void extendTime(int type, String title, int icon);
  }

  @OnClick(R.id.llToday)
  void onPressName() {
    extendedTime.extendTime(1, "امروز",R.drawable.ic_today); // today
    dismiss();
  }

  @OnClick(R.id.llYesterday)
  void onPressTell() {
    extendedTime.extendTime(2, "دیروز",R.drawable.ic_yesterday); // yesterday
    dismiss();
  }

  @OnClick(R.id.llTwoDayAgo)
  void onPressAddress() {
    extendedTime.extendTime(3, "دوروز قبل",R.drawable.ic_twodayago); // two day ago
    dismiss();
  }

  public void show(ExtendedTimeListener searchCaseListener) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_extended_time);
    unbinder = ButterKnife.bind(this, dialog);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(),MyApplication.IraSanSMedume);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.TOP | Gravity.RIGHT;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    KeyBoardHelper.hideKeyboard();
    this.extendedTime = searchCaseListener;

    dialog.show();

  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        if (dialog.isShowing())
          dialog.dismiss();
        unbinder.unbind();
        KeyBoardHelper.hideKeyboard();
      }
      dialog = null;
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
      AvaCrashReporter.send(e, "CityDialog class, dismiss method");
    }
  }

}
