package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentHomeBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class HomeFragment extends Fragment {
    public final String TAG = HomeFragment.class.getSimpleName();
    FragmentHomeBinding binding;
    private CoreListenerStub mListener;
    private Core core;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.txtOperatorName, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtCharge, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtDayScore, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtMonthScore, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtDayForm, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtMonthForm, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtDayWrong, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(binding.txtMonthWrong, MyApplication.IraSanSBold);
        core = LinphoneService.getCore();
        getBalance();

        binding.txtOperatorName.setText(MyApplication.prefManager.getOperatorName());

        binding.txtDayScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getDailyScore()));
        binding.txtMonthScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getMonthScore()));
        binding.txtDayForm.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getServiceCountToday()));
        binding.txtMonthForm.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getServiceCountMonth()));
        binding.txtDayWrong.setText(StringHelper.toPersianDigits("46"));
        binding.txtMonthWrong.setText(StringHelper.toPersianDigits("466"));

        mListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core lc, ProxyConfig proxy, RegistrationState state, String message) {
                if (core.getDefaultProxyConfig() != null && core.getDefaultProxyConfig().equals(proxy)) {
                    binding.imgSipStatus.setImageResource(getStatusIconResource(state));
                } else if (core.getDefaultProxyConfig() == null) {
                    binding.imgSipStatus.setImageResource(getStatusIconResource(state));
                }

                try {
                    binding.imgSipStatus.setOnClickListener(
                            v -> {
                                Core core = LinphoneService.getCore();
                                if (core != null) {
                                    core.refreshRegisters();
                                }
                            });
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                    AvaCrashReporter.send(ise, TAG + " class, onCreateView method");
                }
            }
        };

        binding.llCharge.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new AccountFragment())
                .replace());

        return binding.getRoot();
    }

    private int getStatusIconResource(RegistrationState state) {
        try {
            Core core = LinphoneService.getCore();
            boolean defaultAccountConnected = (core != null && core.getDefaultProxyConfig() != null && core.getDefaultProxyConfig().getState() == RegistrationState.Ok);
            if (state == RegistrationState.Ok && defaultAccountConnected) {
                return R.drawable.ic_successful;
            } else if (state == RegistrationState.Progress) {
                return R.drawable.ic_pendig;
            } else if (state == RegistrationState.Failed) {
                return R.drawable.ic_error;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, getStatusIconResource method");
        }

        return R.drawable.ic_error;
    }

    private void getBalance() {
        if (binding.vfBalance != null)
            binding.vfBalance.setDisplayedChild(0);

        RequestHelper.builder(EndPoints.BALANCE)
                .listener(getBalance)
                .get();
    }

    RequestHelper.Callback getBalance = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        String accountBalance = dataObj.getString("accountBalance");
                        String balance = StringHelper.setComma(accountBalance);
                        if (binding.txtCharge != null)
                            binding.txtCharge.setText(StringHelper.toPersianDigits(balance + " تومان "));
                        if (binding.vfBalance != null)
                            binding.vfBalance.setDisplayedChild(1);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, getBalance onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfBalance != null)
                    binding.vfBalance.setDisplayedChild(1);
            });
        }

    };

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (core != null) {
            core.addListener(mListener);
            ProxyConfig lpc = core.getDefaultProxyConfig();
            if (lpc != null) {
                mListener.onRegistrationStateChanged(core, lpc, lpc.getState(), null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (core != null) {
            core.removeListener(mListener);
        }
    }
}