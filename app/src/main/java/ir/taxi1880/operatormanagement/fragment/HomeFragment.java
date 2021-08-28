package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public final String TAG = HomeFragment.class.getSimpleName();
    private Unbinder unbinder;
    private CoreListenerStub mListener;
    private Core core;

    @BindView(R.id.txtOperatorName)
    TextView txtOperatorName;

    @BindView(R.id.txtCharge)
    TextView txtCharge;

    @BindView(R.id.txtDayScore)
    TextView txtDayScore;

    @BindView(R.id.txtMonthScore)
    TextView txtMonthScore;

    @BindView(R.id.txtDayForm)
    TextView txtDayForm;

    @BindView(R.id.txtMonthForm)
    TextView txtMonthForm;

    @BindView(R.id.txtDayWrong)
    TextView txtDayWrong;

    @BindView(R.id.txtMonthWrong)
    TextView txtMonthWrong;

    @BindView(R.id.vfBalance)
    ViewFlipper vfBalance;

    @BindView(R.id.imgSipStatus)
    ImageView imgSipStatus;

    @OnClick(R.id.llCharge)
    void onCharge() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new AccountFragment())
                .replace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        TypefaceUtil.overrideFonts(txtOperatorName, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtCharge, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtDayScore, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtMonthScore, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtDayForm, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtMonthForm, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtDayWrong, MyApplication.IraSanSBold);
        TypefaceUtil.overrideFonts(txtMonthWrong, MyApplication.IraSanSBold);
        core = LinphoneService.getCore();
        getBalance();

        txtOperatorName.setText(MyApplication.prefManager.getOperatorName());

        txtDayScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getDailyScore()));
        txtMonthScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getMonthScore()));
        txtDayForm.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getServiceCountToday()));
        txtMonthForm.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getServiceCountMonth()));
        txtDayWrong.setText(StringHelper.toPersianDigits("46"));
        txtMonthWrong.setText(StringHelper.toPersianDigits("466"));

        mListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core lc, ProxyConfig proxy, RegistrationState state, String message) {
                if (core.getDefaultProxyConfig() != null && core.getDefaultProxyConfig().equals(proxy)) {
                    imgSipStatus.setImageResource(getStatusIconResource(state));
                } else if (core.getDefaultProxyConfig() == null) {
                    imgSipStatus.setImageResource(getStatusIconResource(state));
                }

                try {
                    imgSipStatus.setOnClickListener(
                            v -> {
                                Core core = LinphoneService.getCore();
                                if (core != null) {
                                    core.refreshRegisters();
                                }
                            });
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                }
            }
        };

        return view;
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
        }

        return R.drawable.ic_error;
    }

    private void getBalance() {
        if (vfBalance != null)
            vfBalance.setDisplayedChild(0);

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
                        if (txtCharge != null)
                            txtCharge.setText(StringHelper.toPersianDigits(balance + " تومان "));
                        if (vfBalance != null)
                            vfBalance.setDisplayedChild(1);
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
                    AvaCrashReporter.send(e, "HomeFragment class, getBalance onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfBalance != null)
                    vfBalance.setDisplayedChild(1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
