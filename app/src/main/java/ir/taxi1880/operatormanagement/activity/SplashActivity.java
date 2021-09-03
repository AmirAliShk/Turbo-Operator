package ir.taxi1880.operatormanagement.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.acra.ACRA;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.ThemeHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.webServices.GetAppInfo;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    Unbinder unbinder;

    @BindView(R.id.txtVersion)
    TextView txtVersion;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.onActivityCreateSetTheme(this);

        setContentView(R.layout.activity_splash);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryLighter));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        ACRA.getErrorReporter().putCustomData("projectId", Constant.PUSH_PROJECT_ID);
        ACRA.getErrorReporter().putCustomData("LineCode", MyApplication.prefManager.getUserCode() + "");

        txtVersion.setText(StringHelper.toPersianDigits("نسخه " + new AppVersionHelper(context).getVerionName() + ""));

        MyApplication.handler.postDelayed(() -> {
            checkPermission();

        }, 1500);

    }

    String[] permissionsRequired = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(MyApplication.currentActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ||(ContextCompat.checkSelfPermission(MyApplication.currentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                new GeneralDialog()
                        .title("دسترسی")
                        .message("برای ورود به برنامه ضروری است تا دسترسی های لازم را برای عملکرد بهتر به برنامه داده شود لطفا جهت بهبود عملکرد دسترسی های لازم را اعمال نمایید")
                        .cancelable(false)
                        .firstButton("باشه", () -> ActivityCompat.requestPermissions(MyApplication.currentActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT))
                        .show();
            } else {
                new GetAppInfo().callAppInfoAPI();
            }
        } else {
            new GetAppInfo().callAppInfoAPI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.prefManager.setAppRun(false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }

    @Override
    public void onBackPressed() {
        try {
            KeyBoardHelper.hideKeyboard();
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
            AvaCrashReporter.send(e, "SplashActivity class, onBackPressed method");
        }
    }
}
