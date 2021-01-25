package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import androidx.fragment.app.Fragment;

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

public class ContractFragment extends Fragment {

    public static final String TAG = ContractFragment.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.vfTxtOfContract)
    ViewFlipper vfTxtOfContract;

    @BindView(R.id.txtContract)
    TextView txtContract;

    @OnClick(R.id.btnSign)
    void btnSign() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new SignatureFragment())
                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                .setAddToBackStack(false)
                .replace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract, container, false);
        TypefaceUtil.overrideFonts(view);
        unbinder = ButterKnife.bind(this, view);
        getTextOfContract();
        return view;
    }

    private void getTextOfContract() {
        if (vfTxtOfContract != null)
            vfTxtOfContract.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.CONTRACT)
                .listener(onContract)
                .get();
    }

    private RequestHelper.Callback onContract = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    String message = object.getString("message");
                    boolean success = object.getBoolean("success");
                    String contract = object.getString("data");

                    if (success) {
                        if (txtContract != null && vfTxtOfContract != null) {
                            vfTxtOfContract.setDisplayedChild(1);
                            txtContract.setText(StringHelper.toPersianDigits(contract));
                        }
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .secondButton("باشه", null)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "Contract class, onContract onResponse method");
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
