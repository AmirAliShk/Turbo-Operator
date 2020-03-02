package ir.taxi1880.operatormanagement.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
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
  private String[] permission = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO};
  int REQUEST_PERMISSION_CODE = 1;

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

    if ((ContextCompat.checkSelfPermission(MyApplication.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(MyApplication.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
      new GeneralDialog()
              .title("مجوز")
              .message("لطفا مجوز های لازم را به برنامه بدهید")
              .firstButton("اجازه میدم", () -> ActivityCompat.requestPermissions(MyApplication.currentActivity, permission, REQUEST_PERMISSION_CODE))
              .secondButton("اجازه نمیدم", null)
              .show();
    } else {
      if (MyApplication.prefManager.getAccessInsertService() == 0) {
        new GeneralDialog()
                .title("هشدار")
                .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                .firstButton("باشه", new Runnable() {
                  @Override
                  public void run() {
//                  TODO delete this runnable
                    MyApplication.Toast("حالا چون داری تست میکنی میزارم بری داخل :)", Toast.LENGTH_SHORT);
                    startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
                  }
                })
                .show();
      } else {
        startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
      }
    }
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

//    MyApplication.configureAccount();

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
    txtOperatorCharge.setText("شارژ شما : " + MyApplication.prefManager.getBalance());

    MyApplication.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if ((ContextCompat.checkSelfPermission(MyApplication.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(MyApplication.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
          new GeneralDialog()
                  .title("مجوز")
                  .message("لطفا مجوز های لازم را به برنامه بدهید")
                  .firstButton("اجازه میدم", () -> ActivityCompat.requestPermissions(MyApplication.currentActivity, permission, REQUEST_PERMISSION_CODE))
                  .secondButton("اجازه نمیدم", null)
                  .show();
        }
      }
    }, 300);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        if (MyApplication.prefManager.getAccessInsertService() == 0) {
          new GeneralDialog()
                  .title("هشدار")
                  .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                  .firstButton("باشه", new Runnable() {
                    @Override
                    public void run() {
//                  TODO delete this runnable
                      MyApplication.Toast("حالا چون داری تست میکنی میزارم بری داخل :)", Toast.LENGTH_SHORT);
                      startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
                    }
                  })
                  .show();
        } else {
          startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
        }

      } else {
        new GeneralDialog()
                .title("مجوز")
                .message("لطفا مجوز های لازم را به برنامه بدهید")
                .firstButton("اجازه میدم", () ->
                        ActivityCompat.requestPermissions(MyApplication.currentActivity, permission, REQUEST_PERMISSION_CODE))
                .secondButton("اجازه نمیدم", null)
                .show();
      }
    }
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
