package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SearchFilterDialog {

  private static final String TAG = SearchFilterDialog.class.getSimpleName();
  private static Dialog dialog;
  static Unbinder unbinder;
  SearchCaseListener searchCaseListener;

  public interface SearchCaseListener {
    void searchCase(int type);
  }

  @BindView(R.id.llPassengerFilter)
  LinearLayout llPassengerFilter;

  @BindView(R.id.llDriverFilter)
  LinearLayout llDriverFilter;

  @OnClick(R.id.llName)
  void onPressName() {
    searchCaseListener.searchCase(1);
    dismiss();
  }

  @OnClick(R.id.llTell)
  void onPressTell() {
    searchCaseListener.searchCase(2);
    dismiss();
  }

  @OnClick(R.id.llAddress)
  void onPressAddress() {
    searchCaseListener.searchCase(3);
    dismiss();
  }

  @OnClick(R.id.llTaxiCode)
  void onPressTaxiCode() {
    searchCaseListener.searchCase(4);
    dismiss();
  }

  @OnClick(R.id.llStationCode)
  void onPressStationCode() {
    searchCaseListener.searchCase(5);
    dismiss();
  }

  @OnClick(R.id.llDriverMobile)
  void onPressDriverCode() {
    searchCaseListener.searchCase(6);
    dismiss();
  }

  @OnClick(R.id.llDriverTaxiCode)
  void onPressDriverTaxiCode() {
    searchCaseListener.searchCase(7);
    dismiss();
  }

  @OnClick(R.id.llDriverAddress)
  void onPressDDriverAddress() {
    searchCaseListener.searchCase(8);
    dismiss();
  }

  @OnClick(R.id.llDriverStationCode)
  void onPressDriverStationCode() {
    searchCaseListener.searchCase(9);
    dismiss();
  }

  public void show(String dialogType, SearchCaseListener searchCaseListener) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_search_filter);
    unbinder = ButterKnife.bind(this, dialog);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(),MyApplication.IraSanSMedume);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.TOP | Gravity.RIGHT;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    KeyBoardHelper.hideKeyboard();
    this.searchCaseListener = searchCaseListener;

    if (dialogType.equals("passenger")){
      llPassengerFilter.setVisibility(View.VISIBLE);
      llDriverFilter.setVisibility(View.GONE);
    }else {
      llPassengerFilter.setVisibility(View.GONE);
      llDriverFilter.setVisibility(View.VISIBLE);
    }

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
      AvaCrashReporter.send(e, "SearchFilterDialog class, dismiss method");
    }
  }

}
