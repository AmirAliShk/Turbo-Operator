package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    Unbinder unbinder;

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
                    int userId = object.getInt("userId");
                    MyApplication.prefManager.setOperatorName(object.getString("name"));
                    if (status == 1) {
                        MyApplication.prefManager.setUserCode(userId);
                        MyApplication.prefManager.setUserName((edtUserName.getText().toString()));
                        MyApplication.prefManager.setPassword(edtPassword.getText().toString());
                        MyApplication.prefManager.isLoggedIn(true);
                        startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
                        MyApplication.currentActivity.finish();
                    } else {
                        ErrorDialog errorDialog = new ErrorDialog();
                        errorDialog.titleText("خطایی رخ داده");
                        errorDialog.messageText("نام کاربری یا رمز عبور اشتباه است");
                        errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> logIn(edtUserName.getText().toString(),edtPassword.getText().toString()));
                        errorDialog.closeBtnRunnable("بستن", null);
                        errorDialog.cancelable(true);
                        errorDialog.show();
                    }
                } catch (Exception e) {
//                    new ErrorDialog()
//                            .messageText("پردازش داده های ورودی با مشکل مواجه شد")
//                            .closeBtnRunnable("بستن", () -> {
//
//                            })
//                            .tryAgainBtnRunnable("تلاش مجدد", () -> {
//
//                            })
//                            .show();
                    e.printStackTrace();
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
