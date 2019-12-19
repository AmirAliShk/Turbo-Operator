package ir.taxi1880.operatormanagement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SplashActivity extends AppCompatActivity {

    //    @BindView(R.id.splashAvl)
//    AVLoadingIndicatorView splashAvl;
    boolean doubleBackToExitPressedOnce = false;
    Unbinder unbinder;
    @BindView(R.id.txtVersion)
    TextView txtVersion;

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

        txtVersion.setText("نسخه " + new AppVersionHelper(MyApplication.context).getVerionName() + "");
        MyApplication.handler.postDelayed(() -> {
            getAppInfo(new AppVersionHelper(MyApplication.context).getVerionCode(), MyApplication.prefManager.getUserCode(), MyApplication.prefManager.getUserName(), MyApplication.prefManager.getPassword());
        }, 1500);

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

    private void getAppInfo(int versionCode, int operatorId, String userName, String password) {
        JSONObject params = new JSONObject();
        try {

            params.put("versionCode", versionCode);
            params.put("operatorId", operatorId);
            params.put("userName", userName);
            params.put("password", password);

            Log.i("TAG", "getAppInfo: "+params);

            RequestHelper.builder(EndPoints.GET_APP_INFO)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onAppInfo)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onAppInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int block = object.getInt("isBlock");
                    int updateAvailable = object.getInt("updateAvailable");
                    int forceUpdate = object.getInt("forceUpdate");
                    String updateUrl = object.getString("updateUrl");
                    int changePass = object.getInt("changePassword");

                    if (block == 1) {
                        new GeneralDialog()
                                .title("هشدار")
                                .message("شما به امکانات این نرم افزار دسترسی ندارید")
                                .firstButton("خروج", () -> MyApplication.currentActivity.finish())
                                .show();
                        return;
                    }

                    if (changePass == 1) {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new LoginFragment())
                                .setAddToBackStack(false)
                                .replace();
                    }

                    if (updateAvailable == 1) {
                        updatePart(forceUpdate, updateUrl);
                        return;
                    } else {
                        continueProcessing();
                    }

                    JSONArray shiftArr = object.getJSONArray("shifs");
                    MyApplication.prefManager.setShiftList(shiftArr.toString());

                    MyApplication.prefManager.setCountNotification(object.getInt("countNotification"));
                    MyApplication.prefManager.setCountRequest(object.getInt("countRequest"));

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
            generalDialog.secondButton("فعلا نه", () -> continueProcessing());
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
}
