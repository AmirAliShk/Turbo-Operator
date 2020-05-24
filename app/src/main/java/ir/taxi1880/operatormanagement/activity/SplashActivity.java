package ir.taxi1880.operatormanagement.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class SplashActivity extends AppCompatActivity {

  //    @BindView(R.id.splashAvl)
//    AVLoadingIndicatorView splashAvl;
  public static final String TAG = SplashActivity.class.getSimpleName();
  boolean doubleBackToExitPressedOnce = false;
  Unbinder unbinder;
  private final String PUSH_PROJECT_ID = "5";

  @BindView(R.id.txtVersion)
  TextView txtVersion;

//  @OnClick(R.id.btnTextConnection)
//  void onCallPress(){
//    Core core = LinphoneService.getCore();
//    Address addressToCall = core.interpretUrl("998");
//    CallParams params = core.createCallParams(null);
//    AudioManager mAudioManager = ((AudioManager) MyApplication.context.getSystemService(Context.AUDIO_SERVICE));
//
//    mAudioManager.setSpeakerphoneOn(true);
//    params.enableVideo(false);
//    if (addressToCall != null) {
//      core.inviteAddressWithParams(addressToCall, params);
//    }
//  }

  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    View view = getWindow().getDecorView();
    getSupportActionBar().hide();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
      window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    ACRA.getErrorReporter().putCustomData("projectId", PUSH_PROJECT_ID);
    ACRA.getErrorReporter().putCustomData("LineCode", MyApplication.prefManager.getUserCode()+"");

    txtVersion.setText(StringHelper.toPersianDigits("نسخه " + new AppVersionHelper(MyApplication.context).getVerionName() + ""));

//    startVoipService();

    MyApplication.handler.postDelayed(() -> {
      checkPermission();

    }, 1500);

  }

  String[] permissionsRequired = new String[]{
          Manifest.permission.RECORD_AUDIO};
  private static final int PERMISSION_CALLBACK_CONSTANT = 100;

  public void checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if ((ContextCompat.checkSelfPermission(MyApplication.currentActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
              || (ContextCompat.checkSelfPermission(MyApplication.currentActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
        new GeneralDialog()
                .title("دسترسی")
                .message("برای ورود به برنامه ضروری است تا دسترسی های لازم را برای عملکرد بهتر به برنامه داده شود لطفا جهت بهبود عملکرد دسترسی های لازم را اعمال نمایید")
                .cancelable(false)
                .firstButton("باشه", new Runnable() {
                  @Override
                  public void run() {
                    ActivityCompat.requestPermissions(MyApplication.currentActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                  }
                })
                .show();
      } else {
        getAppInfo();
      }
    } else {
      getAppInfo();
    }
  }


  private void continueProcessing() {
    if (MyApplication.prefManager.getLoggedIn()) {
      startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
      MyApplication.currentActivity.finish();
    } else {
      FragmentHelper
              .toFragment(MyApplication.currentActivity, new LoginFragment())
              .setAddToBackStack(false)
              .replace();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    MyApplication.currentActivity = this;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  @Override
  protected void onStart() {
    super.onStart();
    MyApplication.currentActivity = this;
  }

  private void getAppInfo() {
    RequestHelper.builder(EndPoints.GET_APP_INFO)
            .addParam("versionCode", new AppVersionHelper(MyApplication.context).getVerionCode())
            .addParam("operatorId", MyApplication.prefManager.getUserCode())
            .addParam("userName", MyApplication.prefManager.getUserName())
            .addParam("password", MyApplication.prefManager.getPassword())
            .listener(onAppInfo)
            .post();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    checkPermission();
  }

  RequestHelper.Callback onAppInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          Log.i(TAG, "onResponse: " + args[0].toString());
          JSONObject object = new JSONObject(args[0].toString());
          int block = 0;
          int updateAvailable = object.getInt("updateAvailable");
          int forceUpdate = object.getInt("forceUpdate");
          String updateUrl = object.getString("updateUrl");
          int changePass = object.getInt("changePassword");
          int countRequest = object.getInt("countRequest");
          int sipNumber = object.getInt("sipNumber");
          String sipServer = object.getString("sipServer");
          String sipPassword = object.getString("sipPassword");
          String sheba = object.getString("sheba");
          String cardNumber = object.getString("cardNumber");
          String accountNumber = object.getString("accountNumber");
          int accessInsertService = object.getInt("accessInsertService");
          int balance = object.getInt("balance");
          String typeService = object.getString("typeService");
          String queue = object.getString("queue");
          String city = object.getString("city");
          int pushId = object.getInt("pushId");
          String pushToken = object.getString("pushToken");

          if (block == 1) {
            new GeneralDialog()
                    .title("هشدار")
                    .message("اکانت شما توسط سیستم مسدود شده است")
                    .firstButton("خروج از برنامه", () -> MyApplication.currentActivity.finish())
                    .show();
            return;
          }

          if (changePass == 1) {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new LoginFragment())
                    .setAddToBackStack(false)
                    .replace();
            return;
          }

          MyApplication.prefManager.setSipServer(sipServer);
          MyApplication.prefManager.setSipNumber(sipNumber);
          MyApplication.prefManager.setSipPassword(sipPassword);
          if (updateAvailable == 1) {
            updatePart(forceUpdate, updateUrl);
            return;
          }

          startVoipService();

          if (sipNumber != MyApplication.prefManager.getSipNumber() ||
                  !sipPassword.equals(MyApplication.prefManager.getSipPassword()) ||
                  !sipServer.equals(MyApplication.prefManager.getSipServer())) {
            if (sipNumber != 0) {
              MyApplication.configureAccount();
            }
          }

          JSONArray shiftArr = object.getJSONArray("shifs");
          MyApplication.prefManager.setShiftList(shiftArr.toString());

          MyApplication.prefManager.setCountNotification(object.getInt("countNotification"));
          MyApplication.prefManager.setCountRequest(object.getInt("countRequest"));

          MyApplication.prefManager.setPushId(pushId);
          MyApplication.prefManager.setPushToken(pushToken);
          MyApplication.prefManager.setSheba(sheba);
          MyApplication.prefManager.setCardNumber(cardNumber);
          MyApplication.prefManager.setAccountNumber(accountNumber);
          MyApplication.prefManager.setBalance(balance);
          MyApplication.prefManager.setServiceType(typeService);
          MyApplication.prefManager.setQueueList(queue);
          MyApplication.prefManager.setCity(city);
          MyApplication.prefManager.setAccessInsertService(accessInsertService);

        } catch (JSONException e) {
          e.printStackTrace();
        }

      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  private void updatePart(int isForce, final String url) {
    GeneralDialog generalDialog = new GeneralDialog();
    if (isForce == 1) {
      generalDialog.title("به روز رسانی");
      generalDialog.cancelable(false);
      generalDialog.message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید");
      generalDialog.firstButton("به روز رسانی", () -> {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        MyApplication.currentActivity.startActivity(i);
        MyApplication.currentActivity.finish();
      });
      generalDialog.secondButton("بستن برنامه", () -> MyApplication.currentActivity.finish());
      generalDialog.show();
    } else {
      generalDialog.title("به روز رسانی");
      generalDialog.cancelable(false);
      generalDialog.message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید");
      generalDialog.firstButton("به روز رسانی", () -> {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        MyApplication.currentActivity.startActivity(i);
        MyApplication.currentActivity.finish();
      });
      generalDialog.secondButton("فعلا نه", () -> startVoipService());
      generalDialog.show();
    }
  }

  @Override
  public void onBackPressed() {
    try {
      if (getSupportFragmentManager().getBackStackEntryCount() > 0 || getFragmentManager().getBackStackEntryCount() > 0) {
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

  // This thread will periodically check if the Service is ready, and then call onServiceReady
  private class ServiceWaitThread extends Thread {
    public void run() {
      while (!LinphoneService.isReady()) {
        try {
          sleep(30);
        } catch (InterruptedException e) {
          throw new RuntimeException("waiting thread sleep() has been interrupted");
        }
      }
      // As we're in a thread, we can't do UI stuff in it, must post a runnable in UI thread
      MyApplication.handler.post(
              new Runnable() {
                @Override
                public void run() {
                  continueProcessing();
                }
              });
    }
  }

  private void startVoipService() {
    if (LinphoneService.isReady()) {
      continueProcessing();
    } else {
      // If it's not, let's start it
      startService(
              new Intent().setClass(this, LinphoneService.class));
      // And wait for it to be ready, so we can safely use it afterwards
      new ServiceWaitThread().start();
    }
  }
}
