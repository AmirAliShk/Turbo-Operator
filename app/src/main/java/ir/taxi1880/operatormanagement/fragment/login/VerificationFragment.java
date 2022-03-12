package ir.taxi1880.operatormanagement.fragment.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentVerificationBinding;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class VerificationFragment extends Fragment {
    public static final String TAG = VerificationFragment.class.getSimpleName();
    FragmentVerificationBinding binding;
    String mobileNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeLite);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding = FragmentVerificationBinding.inflate(localInflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        TypefaceUtil.overrideFonts(binding.getRoot());

        binding.txtRules.setPaintFlags(binding.txtRules.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.llRules.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://turbotaxi.ir:1880/operatorRules"));
            MyApplication.currentActivity.startActivity(i);
        });

        binding.btnSend.setOnClickListener(view -> {
            mobileNumber = binding.edtMobileNumber.getText().toString();

            if (mobileNumber.isEmpty()) {
                MyApplication.Toast("شماره موبایل را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }

            if (!PhoneNumberValidation.isValid(mobileNumber)) {
                MyApplication.Toast("شماره موبایل نا معتبر میباشد.", Toast.LENGTH_SHORT);
                return;
            }
            if (!binding.cbRules.isChecked()) {
                MyApplication.Toast("لطفا قوانین و مقررات را قبول نمایید.", Toast.LENGTH_SHORT);
                return;
            }
            mobileNumber = mobileNumber.startsWith("0") ? mobileNumber : "0" + mobileNumber;

            KeyBoardHelper.hideKeyboard();
            verification(mobileNumber);
        });

        binding.llEnterWithUserName.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new LoginFragment())
                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                .setAddToBackStack(false)
                .replace());

        binding.llParent.setOnClickListener(view -> KeyBoardHelper.hideKeyboard());

        return binding.getRoot();
    }

    private void verification(String phoneNumber) {
        if (binding.vfSend != null) {
            binding.vfSend.setDisplayedChild(1);
        }

        RequestHelper.builder(EndPoints.VERIFICATION)
                .addParam("phoneNumber", phoneNumber)
                .listener(onVerificationCallBack)
                .post();
    }

    private RequestHelper.Callback onVerificationCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    {"success":true,"message":"با موفقیت ارسال شد","data":{"repetitionTime":120}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject objData = object.getJSONObject("data");
                        int repetitionTime = objData.getInt("repetitionTime");
                        MyApplication.prefManager.setRepetitionTime(repetitionTime);
                        Bundle bundle = new Bundle();
                        bundle.putString("mobileNumber", mobileNumber);
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new CheckVerificationFragment())
                                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                                .setArguments(bundle)
                                .setAddToBackStack(false)
                                .replace();
                    }
                    MyApplication.Toast(message, Toast.LENGTH_SHORT);

                    if (binding.vfSend != null) {
                        binding.vfSend.setDisplayedChild(0);
                    }
                } catch (Exception e) {
                    if (binding.vfSend != null) {
                        binding.vfSend.setDisplayedChild(0);
                    }
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onVerificationCallBack onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfSend != null) {
                    binding.vfSend.setDisplayedChild(0);
                }
            });
        }
    };
}