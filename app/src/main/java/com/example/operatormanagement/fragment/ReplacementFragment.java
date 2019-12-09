package com.example.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.operatormanagement.OkHttp.RequestHelper;
import com.example.operatormanagement.R;
import com.example.operatormanagement.app.EndPoints;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.dialog.GeneralDialog;
import com.example.operatormanagement.dialog.OperatorDialog;
import com.example.operatormanagement.helper.DateHelper;
import com.example.operatormanagement.helper.FragmentHelper;
import com.example.operatormanagement.helper.KeyBoardHelper;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.OperatorModel;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReplacementFragment extends android.app.Fragment {
    public static final String TAG = ReplacementFragment.class.getSimpleName();
    Unbinder unbinder;
    String[] shift = {"صبح", "عصر", "شب", "استراحت"};
    GeneralDialog generalDialog = new GeneralDialog();
    int shiftId;

    @OnClick(R.id.imgBack)
    void onBack() {
        KeyBoardHelper.hideKeyboard();
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.llLoader)
    LinearLayout llLoader;

    @BindView(R.id.llDate)
    LinearLayout llDate;

    @BindView(R.id.llShift)
    LinearLayout llShift;

    @BindView(R.id.edtDate)
    TextView edtDate;

    @BindView(R.id.spinnerShift)
    TextView spinnerShift;

    @BindView(R.id.edtOperator)
    TextView edtOperator;

    int opId = 0;

    @OnClick(R.id.llOperator)
    void onOperator() {

        switch (spinnerShift.getText().toString()) {
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
        getOnlineOperator(edtDate.getText().toString(), shiftId, MyApplication.prefManager.getUserCode());
    }

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.vfOperator)
    ViewFlipper vfOperator;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (edtOperator.getText().equals("")) {
            MyApplication.Toast("اپراتور مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT);
            return;
        }

        new GeneralDialog()
                .title("ثبت درخواست")
                .message(" شما میخواهید خانم " + edtOperator.getText() + " در تاریخ " + edtDate.getText() + " در شیفت " + spinnerShift.getText() + " به جای شما حضور یابد.")
                .firstButton("بله", () -> {
                    shiftReplacementRequest(MyApplication.prefManager.getUserCode(), opId, shiftId, edtDate.getText().toString());
                    edtOperator.setText("");
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
        if (bundle.getString("shiftName").equals("استراحت")) {
            llDate.setVisibility(View.GONE);
            llShift.setVisibility(View.GONE);
        } else {
            spinnerShift.setText(bundle.getString("shiftName"));
            edtDate.setText(bundle.getString("shiftDate"));
        }

        return view;
    }

    private void shiftReplacementRequest(int operatorId, int intendedOperatorId, int shift, String date) {
        llLoader.setVisibility(View.VISIBLE);
        JSONObject params = new JSONObject();
        try {
            params.put("operatorId", operatorId);
            params.put("intendedOperatorId", intendedOperatorId);
            params.put("shift", shift);
            params.put("date", date);

            RequestHelper.builder(EndPoints.SHIFT_REPLACEMENT_REQUEST)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onShiftReplacementRequest)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onShiftReplacementRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    String msgStatus = object.getString("messageStatus");
                    if (status == 1) {
                        generalDialog.thirdButton("باشه", () -> generalDialog.dismiss());
                        generalDialog.message(msgStatus);
                        generalDialog.title("تایید");
                        generalDialog.show();
//                        MyApplication.Toast(msgStatus, Toast.LENGTH_SHORT);
                    } else {
                        generalDialog.thirdButton("باشه", () -> generalDialog.dismiss());
                        generalDialog.message(msgStatus);
                        generalDialog.title("هشدار");
                        generalDialog.show();
                    }
//                    else {
//                        new ErrorDialog()
//                                .titleText("خطایی رخ داده")
//                                .messageText("پردازش داده های ورودی با مشکل مواجه گردید")
//                                .tryAgainRunnable("تلاش مجدد", () -> shiftReplacementRequest(1, 1, " ", " "))
//                                .closeRunnable("بستن", () -> MyApplication.currentActivity.finish())
//                                .show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                llLoader.setVisibility(View.GONE);
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

    private void getOnlineOperator(String date, int shiftId, int operatorId) {
        vfOperator.setDisplayedChild(1);
        JSONObject params = new JSONObject();
        try {
            params.put("date", date);
            params.put("shiftId", shiftId);
            params.put("operatorId", operatorId);

            RequestHelper.builder(EndPoints.GET_SHIFT_OPERATOR)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onGetOnlineOperator)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onGetOnlineOperator = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONArray operatorArr = new JSONArray(args[0].toString());
                    MyApplication.prefManager.setOperatorList(operatorArr.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                vfOperator.setDisplayedChild(0);
                new OperatorDialog().show((op) -> {
                    edtOperator.setText(op.getOperatorName());
                    opId = op.getOperatorId();
                });
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
