package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public final String TAG = HomeFragment.class.getSimpleName();
    private Unbinder unbinder;

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

        getBalance();

        txtOperatorName.setText(MyApplication.prefManager.getOperatorName());

        txtDayScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getDailyScore()));
        txtMonthScore.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getMonthScore()));
        txtDayForm.setText(StringHelper.toPersianDigits("54"));
        txtMonthForm.setText(StringHelper.toPersianDigits("345"));
        txtDayWrong.setText(StringHelper.toPersianDigits("46"));
        txtMonthWrong.setText(StringHelper.toPersianDigits("466"));

        return view;
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
            MyApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "run: " + args[0].toString());
                        JSONObject obj = new JSONObject(args[0].toString());
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");
                        JSONObject dataObj = obj.getJSONObject("data");
                        String accountBalance = dataObj.getString("accountBalance");
                        String balance = StringHelper.setComma(accountBalance);

                        if (success) {
                            if (txtCharge != null)
                                txtCharge.setText(StringHelper.toPersianDigits(balance + " تومان "));
                            if (vfBalance != null)
                                vfBalance.setDisplayedChild(1);
                        } else {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        AvaCrashReporter.send(e, "HomeFragment class, getBalance onResponse method");
                    }
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
