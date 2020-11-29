package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.SupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SupportDialog {

  private static Dialog dialog;
  Unbinder unbinder;
  String tellNumber;

  @OnClick(R.id.btnSupport)
  void onSupport() {
    Bundle bundle = new Bundle();
    bundle.putString("tellNumber", tellNumber);
    dismiss();
    FragmentHelper.toFragment(MyApplication.currentActivity, new SupportFragment()).setArguments(bundle).replace();
    Log.e("SupportDialog", "onSupport: SupportDialog dismiss");
  }

  @OnClick(R.id.btnClose)
  void onClose() {
    dismiss();
    Log.e("SupportDialog", "onSupport: SupportDialog dismiss");
  }

  @BindView(R.id.txtTripStatus)
  TextView txtTripStatus;

  @BindView(R.id.txtTime)
  TextView txtTime;

  public void show(String status, int time, String tellNumber) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_support);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(false);

    txtTime.setText(" " + time + " دقیقه ");
    txtTripStatus.setText(" " + status + " ");
    this.tellNumber = tellNumber;

    dialog.show();

  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        if (dialog.isShowing())
          dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      AvaCrashReporter.send(e, "SupportDialog class, dismiss method");
    }
    dialog = null;
  }
}
