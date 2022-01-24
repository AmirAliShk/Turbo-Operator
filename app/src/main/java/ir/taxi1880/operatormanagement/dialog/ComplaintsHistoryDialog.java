package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ComplaintsHistoryAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsHistoryModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ComplaintsHistoryDialog {

    public static final String TAG = ComplaintsHistoryDialog.class.getSimpleName();
    Unbinder unbinder;
    static Dialog dialog;
    String historyOfWho;
    int taxiCode;
    String customerTell;
    String customerMobile;
    ComplaintsHistoryAdapter mAdapter;
    ArrayList<ComplaintsHistoryModel> complaintsHistoryModels;

    @BindView(R.id.txtCountCulprit)
    TextView txtCountCulprit;

    @BindView(R.id.txtCountComplaint)
    TextView txtCountComplaint;

    @BindView(R.id.vfHeader)
    ViewFlipper vfHeader;

    @BindView(R.id.vfComplaintHistory)
    ViewFlipper vfComplaintHistory;

    @BindView(R.id.listComplaintsHistory)
    RecyclerView listComplaintsHistory;

    @BindView(R.id.rgSearchType)
    RadioGroup rgSearchType;

    @OnClick(R.id.rbTell)
    void onTell() {
        if (customerTell.length() == 10 && customerTell.startsWith("0")) {
            customerTell.replaceFirst("0", "");
            complaintCustomerHistory(customerTell);
        } else if (customerTell.length() == 8) {
            customerTell = "51" + customerTell;
            complaintCustomerHistory(customerTell);
        } else {
            complaintCustomerHistory(customerTell);
        }
    }

    @OnClick(R.id.rbMobile)
    void onMobile() {
        if (rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
            complaintCustomerHistory(customerMobile.startsWith("0") ? customerMobile.replaceFirst("0", "") : customerMobile);
        }
    }

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    public void show(String historyOfWho, int taxiCode, String customerTell, String customerMobile) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_complaints_history);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        this.historyOfWho = historyOfWho;
        this.taxiCode = taxiCode;
        this.customerTell = customerTell;
        this.customerMobile = customerMobile;

        if (historyOfWho.equals("driver")) {

            if (vfHeader != null)
                vfHeader.setDisplayedChild(0);

            complaintDriverHistory();

        } else if (historyOfWho.equals("customer")) {

            if (vfHeader != null)
                vfHeader.setDisplayedChild(1);

            if (rgSearchType.getCheckedRadioButtonId() == R.id.rbTell) {

                if (customerTell.length() == 10 && customerTell.startsWith("0")) {
                    customerTell.replaceFirst("0", "");
                    complaintCustomerHistory(customerTell);
                } else if (customerTell.length() == 8) {
                    customerTell = "51" + customerTell;
                    complaintCustomerHistory(customerTell);
                } else {
                    complaintCustomerHistory(customerTell);
                }
            } else if (rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                complaintCustomerHistory(customerMobile.startsWith("0") ? customerMobile.replaceFirst("0", "") : customerMobile);
            }

        }
        dialog.show();
    }

    private void complaintCustomerHistory(String phone) {
        if (vfComplaintHistory != null)
            vfComplaintHistory.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_CUSTOMER_HISTORY + phone)
                .listener(historyCallBack)
                .get();
    }

    private void complaintDriverHistory() {
        if (vfComplaintHistory != null)
            vfComplaintHistory.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_DRIVER_HISTORY + taxiCode)
                .listener(historyCallBack)
                .get();
    }

    RequestHelper.Callback historyCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    complaintsHistoryModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfComplaintHistory != null)
                            vfComplaintHistory.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        JSONObject dataObj = dataArr.getJSONObject(0);
                        txtCountCulprit.setText(dataObj.getInt("countCulprit") + "");
                        txtCountComplaint.setText(dataObj.getInt("countComplaint") + "");

                        String content = dataObj.getString("list");
                        JSONArray contentArr = new JSONArray(content);
                        for (int j = 0; j < contentArr.length(); j++) {
                            JSONObject contentObj = contentArr.getJSONObject(j);
                            ComplaintsHistoryModel model = new ComplaintsHistoryModel();
                            model.setSaveDate(contentObj.getString("saveDate"));
                            model.setSaveTime(contentObj.getString("saveTime"));
                            model.setComplaintType(contentObj.getString("complaintType"));
                            model.setCustomerName(contentObj.getString("customerName"));
                            model.setTypeResultDes(contentObj.getString("typeResultDes"));
                            model.setStatus(contentObj.getInt("status"));

                            complaintsHistoryModels.add(model);
                        }
                        if (historyOfWho.equals("driver")) {
                            if (vfHeader != null)
                                vfHeader.setDisplayedChild(0);
                        } else if (historyOfWho.equals("customer")) {
                            if (vfHeader != null)
                                vfHeader.setDisplayedChild(1);
                        }

                        if (complaintsHistoryModels.size() == 0) {
                            if (vfComplaintHistory != null)
                                vfComplaintHistory.setDisplayedChild(2);
                        } else {
                            if (vfComplaintHistory != null)
                                vfComplaintHistory.setDisplayedChild(1);

                            mAdapter = new ComplaintsHistoryAdapter(complaintsHistoryModels);
                            listComplaintsHistory.setAdapter(mAdapter);
                        }
                    } else {
                        if (vfComplaintHistory != null)
                            vfComplaintHistory.setDisplayedChild(3);
                    }
                } catch (Exception e) {
                    if (vfComplaintHistory != null)
                        vfComplaintHistory.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, historyCallBack method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfComplaintHistory != null)
                    vfComplaintHistory.setDisplayedChild(3);
            });
        }
    };

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}