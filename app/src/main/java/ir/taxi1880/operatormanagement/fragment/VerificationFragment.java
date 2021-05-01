package ir.taxi1880.operatormanagement.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class VerificationFragment extends Fragment {
    Unbinder unbinder;
    String mobileNumber;

    @BindView(R.id.edtMobileNumber)
    EditText edtMobileNumber;

    @BindView(R.id.vfSend)
    ViewFlipper vfSend;

    @BindView(R.id.cbRules)
    CheckBox cbRules;

    @OnClick(R.id.llRules)
    void OnRules() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://turbotaxi.ir:1880/operatorRules"));
        MyApplication.currentActivity.startActivity(i);
    }

    @OnClick(R.id.btnSend)
    void onSend() {
        mobileNumber = edtMobileNumber.getText().toString();

        if (mobileNumber.isEmpty()) {
            MyApplication.Toast("شماره موبایل را وارد کنید.", Toast.LENGTH_SHORT);
            return;
        }

        if (!PhoneNumberValidation.isValid(mobileNumber)) {
            MyApplication.Toast("شماره موبایل نا معتبر میباشد.", Toast.LENGTH_SHORT);
            return;
        }
        if (!cbRules.isChecked()) {
            MyApplication.ErrorToast("لطفا قوانین و مقررات را قبول نمایید.", Toast.LENGTH_SHORT);
            return;
        }
        mobileNumber = mobileNumber.startsWith("0") ? mobileNumber : "0" + mobileNumber;

        KeyBoardHelper.hideKeyboard();
        verification(mobileNumber);
    }

    @OnClick(R.id.llEnterWithUserName)
    void onEnterWithUserName() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new LoginFragment())
                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                .setAddToBackStack(false)
                .replace();
    }

    @OnClick(R.id.llParent)
    void onParent() {
        KeyBoardHelper.hideKeyboard();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        TextView txtRules = view.findViewById(R.id.txtRules);
        txtRules.setPaintFlags(txtRules.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    private void verification(String phoneNumber) {
        if (vfSend != null) {
            vfSend.setDisplayedChild(1);
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

                    if (vfSend != null) {
                        vfSend.setDisplayedChild(0);
                    }
                } catch (Exception e) {
                    if (vfSend != null) {
                        vfSend.setDisplayedChild(0);
                    }
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "VerificationFragment class, onVerificationCallBack onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfSend != null) {
                    vfSend.setDisplayedChild(0);
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}