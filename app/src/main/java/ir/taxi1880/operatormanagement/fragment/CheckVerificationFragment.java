package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.PinEntryEditText;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class CheckVerificationFragment extends Fragment {

    public static final String TAG = CheckVerificationFragment.class.getSimpleName();
    Unbinder unbinder;
    String code;
    String phoneNumber;
    static CountDownTimer countDownTimer;

    @OnClick(R.id.llResendCode)
    void onPressResendCode() {
        verification(phoneNumber);
    }

    @OnClick(R.id.llChangeNumber)
    void onPressChangeNumber() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        FragmentHelper.toFragment(MyApplication.currentActivity, new VerificationFragment()).setAddToBackStack(false).replace();
    }

    @OnClick(R.id.btnEnter)
    void onPressEnter() {
        code = edtCode.getText().toString();

        if (code.isEmpty()) {
            MyApplication.Toast("کد را وارد کنید", Toast.LENGTH_SHORT);
            return;
        }

        checkVerification();
    }

    @BindView(R.id.edtCode)
    PinEntryEditText edtCode;

    @BindView(R.id.txtResendCode)
    TextView txtResendCode;

    @BindView(R.id.txtPhoneNumber)
    TextView txtPhoneNumber;

    @BindView(R.id.vfTime)
    ViewFlipper vfTime;

    @BindView(R.id.vfEnter)
    ViewFlipper vfEnter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_verification, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString("mobileNumber");
            txtPhoneNumber.setText(phoneNumber);
        }

        startWaitingTime();

        return view;
    }

    private void verification(String phoneNumber) {
        if (vfTime != null) {
            vfTime.setDisplayedChild(2);
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
                    if (vfTime != null) {
                        vfTime.setDisplayedChild(0);
                    }
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject objData = object.getJSONObject("data");
                        int repetitionTime = objData.getInt("repetitionTime");
                        MyApplication.prefManager.setRepetitionTime(repetitionTime);
                        startWaitingTime();
                    } else {
                        MyApplication.Toast(message, Toast.LENGTH_SHORT);
//                        {"success":false,"message":"محدودیت زمانی","data":{}}
                        //TODO show dialog error
                    }
                } catch (Exception e) {
                    if (vfTime != null) {
                        vfTime.setDisplayedChild(1);
                    }
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "CheckVerificationFragment class, CheckVerificationFragment onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfTime != null) {
                    vfTime.setDisplayedChild(1);
                }
            });
        }
    };

    private void checkVerification() {
        if (vfEnter != null) {
            vfEnter.setDisplayedChild(1);
        }

        RequestHelper.builder(EndPoints.CHECK)
                .addParam("phoneNumber", phoneNumber)
                .addParam("scope", Constant.SCOPE)
                .addParam("code", code)
                .listener(onCheckVerificationCallBack)
                .post();

    }

    private RequestHelper.Callback onCheckVerificationCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                  {"success":true,"message":"با موفقیت وارد شدید","data":{"id_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzLCJ1c2VybmFtZSI6IjEyMzQiLCJpYXQiOjE2MDkzMjg2NTYsImV4cCI6MTYwOTMyODk1Nn0.u_twFCxWzu73CMkPtb73Q0WdgzozgWKbgZYSmzlIgHg","access_token":"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3R1cmJvdGF4aS5pciIsImF1ZCI6IlVzZXJzIiwiZXhwIjoxNjA5MzI4OTU2LCJzY29wZSI6Im9wZXJhdG9yIiwic3ViIjoidHVyYm90YXhpIiwianRpIjoiNkQ0OTc3ODI3NzFGN0ZEMSIsImFsZyI6IkhTMjU2IiwiaWF0IjoxNjA5MzI4NjU2fQ.8Ssz4-AhK10cy8ma1635iIgquj9gtHHB4S1ETyioRN4","refresh_token":"kTDDNxxc4tQVrN1qQhQFBXZE5qFu3mbelgEbExnsnUElmZv0fFUDpOilLVeOegN5nDCX92mlahXHxP7hWjN52AoOZnZbDG7nz7mcqjowrpxiAgjWsHw5DeOW0RBvadgnRXGEYYS9YByTrYwTL3C4VZEY0DzeTzVyfZsRG2D8LX1jeE87yDx7Afe8D0em4htKfM1KvMWlptdMQbrZrE6yZRuvofubZAFgHgazoi8EDfiWtanu5jNiW86KuPJgbC0r"}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject data = object.getJSONObject("data");
                        MyApplication.prefManager.setIdToken(data.getString("id_token"));
                        MyApplication.prefManager.setAuthorization(data.getString("access_token"));
                        MyApplication.prefManager.setRefreshToken(data.getString("refresh_token"));
                        new SplashActivity().getAppInfo(b -> {
                            if (vfEnter != null) {
                                vfEnter.setDisplayedChild(0);
                            }
                        });
                    } else {
                        if (vfEnter != null) {
                            vfEnter.setDisplayedChild(0);
                        }
                        MyApplication.Toast(message, Toast.LENGTH_SHORT);
//                        {"success":false,"message":".اطلاعات صحیح نمی باشد","data":{}}
                        //TODO show dialog error
                    }

                } catch (Exception e) {
                    if (vfEnter != null) {
                        vfEnter.setDisplayedChild(0);
                    }
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "CheckVerificationFragment class, CheckVerificationFragment onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfEnter != null) {
                    vfEnter.setDisplayedChild(0);
                }
            });
        }
    };

    private void startWaitingTime() {
        if (MyApplication.prefManager.getActivationRemainingTime() < Calendar.getInstance().getTimeInMillis())
            MyApplication.prefManager.setActivationRemainingTime(Calendar.getInstance().getTimeInMillis() + (MyApplication.prefManager.getRepetitionTime() * 1000));
        countDownTimer();
    }

    private void countDownTimer() {
        long remainingTime = MyApplication.prefManager.getActivationRemainingTime() - Calendar.getInstance().getTimeInMillis();

        countDownTimer = new CountDownTimer(remainingTime, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                if (txtResendCode != null) {
                    if (vfTime != null)
                        vfTime.setDisplayedChild(0);
                    txtResendCode.setText( millisUntilFinished / 1000 +"");
                }
            }

            public void onFinish() {
                if (txtResendCode != null) {
                    if (vfTime != null)
                        vfTime.setDisplayedChild(1);
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}