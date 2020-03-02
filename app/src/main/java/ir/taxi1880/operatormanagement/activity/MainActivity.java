package ir.taxi1880.operatormanagement.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.AccountFragment;
import ir.taxi1880.operatormanagement.fragment.MessageFragment;
import ir.taxi1880.operatormanagement.fragment.NotificationFragment;
import ir.taxi1880.operatormanagement.fragment.ReplacementWaitingFragment;
import ir.taxi1880.operatormanagement.fragment.SendReplacementReqFragment;
import ir.taxi1880.operatormanagement.fragment.ShiftFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class MainActivity extends AppCompatActivity {

  Unbinder unbinder;
  boolean doubleBackToExitPressedOnce = false;

  @OnClick(R.id.llNotification)
  void onNotification() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new NotificationFragment())
            .replace();
  }

  @OnClick(R.id.llShift)
  void onShifts() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ShiftFragment())
            .setAddToBackStack(true)
            .replace();
  }

  @OnClick(R.id.llTripRegister)
  void onTripRegister() {
//    FragmentHelper
//            .toFragment(MyApplication.currentActivity, new TripRegisterFragment())
//            .setAddToBackStack(true)
//            .replace();
    startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
  }

  @OnClick(R.id.llReplacement)
  void onReplacement() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new SendReplacementReqFragment())
            .replace();
  }

  @OnClick(R.id.llWaitReplacement)
  void onWaotReplacement() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ReplacementWaitingFragment())
            .replace();
  }

  @OnClick(R.id.llMessage)
  void onMessage() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new MessageFragment())
            .replace();
  }

  @BindView(R.id.txtBadgeCount)
  TextView txtBadgeCount;

  @BindView(R.id.txtRequestCount)
  TextView txtRequestCount;

  @BindView(R.id.txtOperatorName)
  TextView txtOperatorName;

  @BindView(R.id.txtOperatorCharge)
  TextView txtOperatorCharge;

  @OnClick(R.id.llProfile)
  void onPressProfile() {

    FragmentHelper
            .toFragment(MyApplication.currentActivity, new AccountFragment())
            .replace();

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    View view = getWindow().getDecorView();
    getSupportActionBar().hide();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
      window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    MyApplication.configureAccount();

    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    if (MyApplication.prefManager.getCountNotification() == 0) {
      txtBadgeCount.setVisibility(View.GONE);
    } else {
      txtBadgeCount.setVisibility(View.VISIBLE);
      txtBadgeCount.setText(MyApplication.prefManager.getCountNotification() + "");
    }

    if (MyApplication.prefManager.getCountRequest() == 0) {
      txtRequestCount.setVisibility(View.GONE);
    } else {
      txtRequestCount.setVisibility(View.VISIBLE);
      txtRequestCount.setText(MyApplication.prefManager.getCountRequest() + "");
    }

    txtOperatorName.setText(MyApplication.prefManager.getOperatorName());
    txtOperatorCharge.setText("شارژ شما : "+MyApplication.prefManager.getBalance());

  }

  @Override
  protected void onResume() {
    super.onResume();
    MyApplication.currentActivity = this;
  }

  @Override
  protected void onStart() {
    super.onStart();
    MyApplication.currentActivity = this;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  @Override
  public void onBackPressed() {

    try {
      if (getFragmentManager().getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {
        super.onBackPressed();
      } else {
        if (doubleBackToExitPressedOnce) {
          super.onBackPressed();
        } else {
          this.doubleBackToExitPressedOnce = true;
          MyApplication.Toast(getString(R.string.txt_please_for_exit_reenter_back), Toast.LENGTH_SHORT);
          new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1500);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
