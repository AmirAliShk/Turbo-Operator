package com.example.operatormanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.operatormanagement.OkHttp.RequestHelper;
import com.example.operatormanagement.R;
import com.example.operatormanagement.app.EndPoints;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.dialog.ErrorDialog;
import com.example.operatormanagement.dialog.GeneralDialog;
import com.example.operatormanagement.fragment.MenuFragment;
import com.example.operatormanagement.fragment.NotificationFragment;
import com.example.operatormanagement.fragment.ReplacementFragment;
import com.example.operatormanagement.fragment.ReplacementWaitingFragment;
import com.example.operatormanagement.fragment.ShiftFragment;
import com.example.operatormanagement.helper.AppVersionHelper;
import com.example.operatormanagement.helper.FragmentHelper;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.OperatorModel;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

        txtVersion.setText("version "+new AppVersionHelper(MyApplication.context).getVerionName() + "");
        MyApplication.handler.postDelayed(() -> {
            getAppInfo(new AppVersionHelper(MyApplication.context).getVerionCode(), MyApplication.prefManager.getUserCode());
        }, 1500);

    }

    private void continueProcessing() {
        if (MyApplication.prefManager.getLoggedIn()) {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new MenuFragment())
                    .setAddToBackStack(false)
                    .replace();
        } else {
            startActivity(new Intent(MyApplication.context, LoginActivity.class));
            finish();
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

    private void getAppInfo(int versionCode, int operatorId) {
        JSONObject params = new JSONObject();
        try {

            params.put("versionCode", versionCode);
            params.put("operatorId", operatorId);

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
                    final String updateUrl = object.getString("updateUrl");

                    if (block == 1) {
                        new GeneralDialog()
                                .title("هشدار")
                                .message("شما به امکانات این نرم افزار دسترسی ندارید")
                                .firstButton("خروج", () -> MyApplication.currentActivity.finish())
                                .show();
                        return;
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

    private boolean removeCurrentFragment(Fragment fragment, boolean withAnimation) {
        FragmentTransaction transaction = MyApplication.fragmentManagerV4.beginTransaction();
        if (fragment != null) {
            if (fragment.isVisible()) {
                if (withAnimation)
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
                transaction.remove(fragment).commit();
                MyApplication.fragmentManagerV4.popBackStack();
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        try {
//            Fragment fragment;
//            fragment = getSupportFragmentManager().findFragmentByTag(MenuFragment.TAG);
//            if (removeCurrentFragment(fragment, true)) return;
//
//            fragment = getSupportFragmentManager().findFragmentByTag(ShiftFragment.TAG);
//            if (removeCurrentFragment(fragment, true)) return;

//            fragment = getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG);
//            if (removeCurrentFragment(fragment, true)) return;
//
//            fragment = getSupportFragmentManager().findFragmentByTag(ReplacementFragment.TAG);
//            if (removeCurrentFragment(fragment, true)) return;
//
//            fragment = getSupportFragmentManager().findFragmentByTag(ReplacementWaitingFragment.TAG);
//            if (removeCurrentFragment(fragment, true)) return;

            if (getFragmentManager().getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                getSupportFragmentManager().popBackStack();
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
