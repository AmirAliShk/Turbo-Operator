package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;

import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.OperatorDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReplacementFragment extends Fragment {
    public static final String TAG = ReplacementFragment.class.getSimpleName();
    Unbinder unbinder;
    String[] shift = {"صبح", "عصر", "شب", "استراحت"};
    GeneralDialog generalDialog = new GeneralDialog();
    int shiftId;
    String shiftDate;
    String shiftName;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.txtOperator)
    TextView txtOperator;

    @BindView(R.id.loader)
    AVLoadingIndicatorView loader;

    int opId = 0;

    @OnClick(R.id.llOperator)
    void onOperator() {

        switch (shiftName) {
            case "صبح":
                shiftId = 1;
                break;
            case "عصر":
                shiftId = 2;
                break;
            case "شب":
                shiftId = 3;
                break;
            case "استراحت":
                shiftId = 4;
                break;
            default:
                shiftId = 0;
                break;
        }
        getOnlineOperator();
    }

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (txtOperator.getText().equals("")) {
            MyApplication.Toast("اپراتور مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT);
            return;
        }

        new GeneralDialog()
                .title("ثبت درخواست")
                .message(" شما میخواهید خانم " + txtOperator.getText() + " در تاریخ " + shiftDate + " در شیفت " + shiftName + " به جای شما حضور یابد.")
                .firstButton("بله", () -> {
                    shiftReplacementRequest();
                    txtOperator.setText("");
                })
                .secondButton("خیر", null)
                .show();
    }

    @SuppressLint("Clickab leViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replacement, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            shiftDate = bundle.getString("shiftDate");
            shiftName = bundle.getString("shiftName");
        }

        return view;
    }

    private void shiftReplacementRequest() {
        RequestHelper.builder(EndPoints.SHIFT_REPLACEMENT_REQUEST)
                .addParam("intendedOperatorId", opId)
                .addParam("shift", shiftId)
                .addParam("date", shiftDate)
                .listener(onShiftReplacementRequest)
                .post();

    }

    RequestHelper.Callback onShiftReplacementRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    String msgStatus = object.getString("messageStatus");
                        generalDialog.firstButton("باشه", () -> generalDialog.dismiss());
                        generalDialog.message(msgStatus);
                        generalDialog.title("تایید");
                        generalDialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "ReplacementFragment class, onShiftReplacementRequest onResponse method");
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }
    };

    private void getOnlineOperator() {
        // TODO test it ...
        if (loader != null)
            loader.setVisibility(View.VISIBLE);

        RequestHelper.builder(EndPoints.GET_SHIFT_OPERATOR)
                .addParam("shiftDate", shiftDate)
                .addParam("shiftId", shiftId + "")
                .listener(onGetOnlineOperator)
                .post();

    }

    RequestHelper.Callback onGetOnlineOperator = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONArray operatorArr = new JSONArray(args[0].toString());
                    MyApplication.prefManager.setOperatorList(operatorArr.toString());

                    if (loader != null)
                        loader.setVisibility(View.GONE);

                    new OperatorDialog().show((op) -> {
                        if (txtOperator != null)
                            txtOperator.setText(op.getOperatorName());
                        opId = op.getOperatorId();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "ReplacementFragment class, onGetOnlineOperator onResponse method");
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
