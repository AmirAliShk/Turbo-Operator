package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentLoginBinding;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.webServices.GetAppInfo;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    FragmentLoginBinding binding;
    String userName;
    String password;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeLite);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding = FragmentLoginBinding.inflate(localInflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        TypefaceUtil.overrideFonts(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        binding.txtRules.setPaintFlags(binding.txtRules.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                userName = binding.edtUserName.getText().toString();
                password = binding.edtPassword.getText().toString();

                if (userName.isEmpty()) {
                    MyApplication.Toast("لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT);
                    return false;
                }
                if (password.isEmpty()) {
                    MyApplication.Toast("لطفا رمز عبور خود را وارد نمایید", Toast.LENGTH_SHORT);
                    return false;
                }
                if (!binding.cbRules.isChecked()) {
                    MyApplication.Toast("لطفا قوانین و مقررات را قبول نمایید.", Toast.LENGTH_SHORT);
                    return false;
                }
                logIn(userName, password);
                KeyBoardHelper.hideKeyboard();
                return true;
            }
            return false;
        });

        binding.llRules.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://turbotaxi.ir:1880/operatorRules"));
            MyApplication.currentActivity.startActivity(i);
        });

        binding.btnLogin.setOnClickListener(view -> {
            userName = binding.edtUserName.getText().toString();
            password = binding.edtPassword.getText().toString();

            if (userName.isEmpty()) {
                MyApplication.Toast("لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT);
                return;
            }
            if (password.isEmpty()) {
                MyApplication.Toast("لطفا رمز عبور خود را وارد نمایید", Toast.LENGTH_SHORT);
                return;
            }
            if (!binding.cbRules.isChecked()) {
                MyApplication.Toast("لطفا قوانین و مقررات را قبول نمایید.", Toast.LENGTH_SHORT);
                return;
            }
            logIn(userName, password);
            KeyBoardHelper.hideKeyboard();
        });

        binding.llParent.setOnClickListener(view -> KeyBoardHelper.hideKeyboard());

        binding.llEnterWithMobile.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new VerificationFragment())
                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                .setAddToBackStack(false)
                .replace());

        return binding.getRoot();
    }

    private void logIn(String username, String password) {
        if (binding.vfEnter != null) {
            binding.vfEnter.setDisplayedChild(1);
        }
        RequestHelper.builder(EndPoints.LOGIN)
                .addParam("username", username)
                .addParam("password", password)
                .addParam("scope", Constant.SCOPE)
                .doNotSendHeader(true)
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
                        new GetAppInfo().callAppInfoAPI();
                    } else {
                        if (binding.vfEnter != null) {
                            binding.vfEnter.setDisplayedChild(0);
                        }
                        new ErrorDialog()
                                .titleText("خطایی رخ داده")
                                .messageText(message)
                                .closeBtnRunnable("بستن", null)
                                .tryAgainBtnRunnable("تلاش مجدد", () -> {
                                    if (binding.edtUserName != null) {
                                        binding.edtUserName.requestFocus();
                                        KeyBoardHelper.showKeyboard(MyApplication.context);
                                    }
                                })
                                .show();
                    }

                } catch (Exception e) {
                    if (binding.vfEnter != null) {
                        binding.vfEnter.setDisplayedChild(0);
                    }
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfEnter != null) {
                    binding.vfEnter.setDisplayedChild(0);
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
