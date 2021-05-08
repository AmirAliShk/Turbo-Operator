package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
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
    Unbinder unbinder;
    static Dialog dialog;
    String historyOfWho;
    int taxiCode;
    String customerTell;
    String customerMobile;
    ComplaintsHistoryAdapter mAdapter;
    ArrayList<ComplaintsHistoryModel> complaintsHistoryModels;
    ComplaintsHistoryModel mComplaintsHistoryModel;

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

        if (historyOfWho == "driver") {
            complaintHistory();
        } else if (historyOfWho == "customer") {
//todo
        }
        dialog.show();
    }

    private void complaintHistory() {
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

//                        "countCulprit":0,
//                        "countComplaint":2,
//                        "saveDate":"1400/02/11",
//                        "saveTime":"18:35 ",
//                        "voipId":"0",
//                        "complaintType":"تعرفه بيش از حد مجاز",
//                        "serviceId":28520689,
//                        "customerId":25083573,
//                        "complaintId":17,
//                        "typeResultDes":null,
//                        "statusDes":"جدید",
//                        "customerName":"حاتمي"
                try {
                    complaintsHistoryModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfComplaintHistory != null)
                            vfComplaintHistory.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            ComplaintsHistoryModel model = new ComplaintsHistoryModel();

                            model.setSaveDate(dataObj.getString("saveDate"));
                            model.setSaveTime(dataObj.getString("saveTime"));
                            model.setComplaintType(dataObj.getString("complaintType"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setTypeResultDes(dataObj.getString("typeResultDes"));
                            model.setCountComplaint(dataObj.getInt("countComplaint"));
                            model.setCountCulprit(dataObj.getInt("countCulprit"));

                            if (historyOfWho == "driver") {
                                if (vfHeader != null)
                                    vfHeader.setDisplayedChild(0);
                            } else {
                                if (vfHeader != null)
                                    vfHeader.setDisplayedChild(1);
                            }

                            complaintsHistoryModels.add(model);
                        }

                        if (complaintsHistoryModels.size() == 0) {
                            if (vfComplaintHistory != null)
                                vfComplaintHistory.setDisplayedChild(2);
                        } else {
                            if (vfComplaintHistory != null)
                                vfComplaintHistory.setDisplayedChild(1);

                            mAdapter = new ComplaintsHistoryAdapter(MyApplication.currentActivity, complaintsHistoryModels);
                            listComplaintsHistory.setAdapter(mAdapter);
//                            txtCountCulprit.setText(mComplaintsHistoryModel.getCountCulprit());
//                            txtCountComplaint.setText(mComplaintsHistoryModel.getCountComplaint());
                        }
                    } else {
                        if (vfComplaintHistory != null)
                            vfComplaintHistory.setDisplayedChild(3);
                    }
                } catch (Exception e) {
                    if (vfComplaintHistory != null)
                        vfComplaintHistory.setDisplayedChild(3);
                    e.printStackTrace();
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
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "ReserveDialog class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}
