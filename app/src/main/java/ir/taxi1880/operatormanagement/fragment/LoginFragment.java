package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.publicAPI.SplashApi;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    Unbinder unbinder;
    String userName;
    String password;

    @BindView(R.id.edtUserName)
    EditText edtUserName;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @OnClick(R.id.btnLogin)
    void onLogin() {
        userName = edtUserName.getText().toString();
        password = edtPassword.getText().toString();

        if (userName.isEmpty()) {
            MyApplication.Toast("لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT);
            return;
        }
        if (password.isEmpty()) {
            MyApplication.Toast("لطفا رمز عبور خود را وارد نمایید", Toast.LENGTH_SHORT);
            return;
        }

        logIn(userName, password);
//        FragmentHelper
//                .toFragment(MyApplication.currentActivity, new LoginFragment())
//                .setAddToBackStack(false)
//                .replace();
        KeyBoardHelper.hideKeyboard();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        return view;
    }

    private void logIn(String username, String password) {

        RequestHelper.builder(EndPoints.LOGIN)
                .addParam("username", username)
                .addParam("password", password)
                .addParam("scope", Constant.SCOPE)
                .listener(onLogIn)
                .post();

    }

    RequestHelper.Callback onLogIn = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject data = object.getJSONObject("data");
                        MyApplication.prefManager.setIdToken(data.getString("id_token"));
                        MyApplication.prefManager.setAuthorization(data.getString("access_token"));
                        MyApplication.prefManager.setRefreshToken(data.getString("refresh_token"));
                        //TODO here call splash again ?!?!?!?!?!?!?!?
                        new SplashApi().getAppInfo(new SplashApi.SplashInterface() {
                            @Override
                            public void isFinishContract(boolean finished) {
                                new GeneralDialog()
                                        .title("اتمام قرار داد")
                                        .message("مدت قرار داد شما به اتمام رسیده است. لطفا برای تمدید آن اقدام کنید.")
                                        .cancelable(false)
                                        .firstButton("مشاهده قرارداد", () -> {
                                            FragmentHelper
                                                    .toFragment(MyApplication.currentActivity, new ContractFragment())
                                                    .setAddToBackStack(false)
                                                    .replace();
                                        })
                                        .secondButton("امضا قرارداد", () -> {
                                            FragmentHelper
                                                    .toFragment(MyApplication.currentActivity, new SignatureFragment())
                                                    .setAddToBackStack(false)
                                                    .replace();
                                        })
                                        .show();
                            }

                            @Override
                            public void update(String updateUrl, int isForce, int updateAvailable) {
                                if (updateAvailable == 1) {
                                    new SplashActivity().updatePart(isForce, updateUrl);
                                }
                            }

                            @Override
                            public void isBlock(boolean isBlock) {
                                if (isBlock) {
                                    new GeneralDialog()
                                            .title("هشدار")
                                            .message("اکانت شما توسط سیستم مسدود شده است")
                                            .firstButton("خروج از برنامه", () -> MyApplication.currentActivity.finish())
                                            .show();
                                }
                            }

                            @Override
                            public void changePass(boolean isChangePass) {
                                //TODO what to do here?
                                if (edtUserName != null) {
                                    edtUserName.requestFocus();
                                    KeyBoardHelper.showKeyboard(MyApplication.context);
                                }
                            }

                            @Override
                            public void continueProcessing(boolean continueProcess) {
                                new SplashActivity().startVoipService();
                            }
                        });
                    } else {
                        new ErrorDialog()
                                .titleText("خطایی رخ داده")
                                .messageText(message)
                                .closeBtnRunnable("بستن", null)
                                .tryAgainBtnRunnable("تلاش مجدد", () -> {
                                    if (edtUserName != null) {
                                        edtUserName.requestFocus();
                                        KeyBoardHelper.showKeyboard(MyApplication.context);
                                    }
                                })
                                .show();
                        //TODO what to do???
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "LoginFragment class, onLogIn onResponse method, pushToken = " + MyApplication.prefManager.getPushToken() + ", pushId = " + MyApplication.prefManager.getPushId() + ", userId = " + MyApplication.prefManager.getUserCode());
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
