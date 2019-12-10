package ir.taxi1880.operatormanagement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.fragment.MenuFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {

    Unbinder unbinder;
    boolean doubleBackToExitPressedOnce=false;

    @BindView(R.id.edtUserName)
    EditText edtUserName;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @OnClick(R.id.btnLogin)
    void onLogin() {
        if (edtUserName.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT);
            return;
        }
        if (edtPassword.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا رمز عبور خود را وارد نمایید", Toast.LENGTH_SHORT);
            return;
        }

        logIn(edtUserName.getText().toString(), edtPassword.getText().toString());
//        FragmentHelper
//                .toFragment(MyApplication.currentActivity, new MenuFragment())
//                .setAddToBackStack(false)
//                .replace();
        KeyBoardHelper.hideKeyboard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View view = getWindow().getDecorView();
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

    }

    private void logIn(String userName, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("userName", userName);
            params.put("password", password);

            RequestHelper.builder(EndPoints.LOGIN)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onLogIn)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onLogIn = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    MyApplication.prefManager.setOperatorName(object.getString("name"));
                    if (status == 1) {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new MenuFragment())
                                .setAddToBackStack(false)
                                .replace();
                        MyApplication.prefManager.setUserCode(Integer.parseInt(edtUserName.getText().toString()));
                        MyApplication.prefManager.setPassword(edtPassword.getText().toString());
                        MyApplication.prefManager.isLoggedIn(true);

                    } else {
                        ErrorDialog errorDialog = new ErrorDialog();
                        errorDialog.titleText("خطایی رخ داده");
                        errorDialog.messageText("نام کاربری یا رمز عبور اشتباه است");
                        errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> errorDialog.dismiss());
                        errorDialog.closeBtnRunnable("بستن", () -> MyApplication.currentActivity.finish());
                        errorDialog.show();

                    }
                } catch (Exception e) {
                    new ErrorDialog()
                            .messageText("پردازش داده های ورودی با مشکل مواجه شد")
                            .closeBtnRunnable("بستن", () -> {

                            })
                            .tryAgainBtnRunnable("تلاش مجدد", () -> {

                            })
                            .show();
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

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
