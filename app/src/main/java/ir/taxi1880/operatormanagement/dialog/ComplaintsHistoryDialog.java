package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ComplaintsHistoryAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogComplaintsHistoryBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsHistoryModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ComplaintsHistoryDialog {

    public static final String TAG = ComplaintsHistoryDialog.class.getSimpleName();
    DialogComplaintsHistoryBinding binding;
    static Dialog dialog;
    String historyOfWho;
    int taxiCode;
    String customerTell;
    String customerMobile;
    ComplaintsHistoryAdapter mAdapter;
    ArrayList<ComplaintsHistoryModel> complaintsHistoryModels;

    public void show(String historyOfWho, int taxiCode, String customerTell, String customerMobile) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogComplaintsHistoryBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
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

            if (binding.vfHeader != null)
                binding.vfHeader.setDisplayedChild(0);

            complaintDriverHistory();

        } else if (historyOfWho.equals("customer")) {

            if (binding.vfHeader != null)
                binding.vfHeader.setDisplayedChild(1);

            if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbTell) {

                if (customerTell.length() == 10 && customerTell.startsWith("0")) {
                    customerTell.replaceFirst("0", "");
                    complaintCustomerHistory(customerTell);
                } else if (customerTell.length() == 8) {
                    customerTell = "51" + customerTell;
                    complaintCustomerHistory(customerTell);
                } else {
                    complaintCustomerHistory(customerTell);
                }
            } else if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                complaintCustomerHistory(customerMobile.startsWith("0") ? customerMobile.replaceFirst("0", "") : customerMobile);
            }

        }

        binding.rbTell.setOnClickListener(view -> {
            if (this.customerTell.length() == 10 && this.customerTell.startsWith("0")) {
                this.customerTell.replaceFirst("0", "");
                complaintCustomerHistory(this.customerTell);
            } else if (this.customerTell.length() == 8) {
                this.customerTell = "51" + this.customerTell;
                complaintCustomerHistory(this.customerTell);
            } else {
                complaintCustomerHistory(this.customerTell);
            }
        });

        binding.rbMobile.setOnClickListener(view -> {
            if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                complaintCustomerHistory(customerMobile.startsWith("0") ? customerMobile.replaceFirst("0", "") : customerMobile);
            }
        });

        binding.imgClose.setOnClickListener(view -> {
            dismiss();
        });

        dialog.show();
    }

    private void complaintCustomerHistory(String phone) {
        if (binding.vfComplaintHistory != null)
            binding.vfComplaintHistory.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_CUSTOMER_HISTORY + phone)
                .listener(historyCallBack)
                .get();
    }

    private void complaintDriverHistory() {
        if (binding.vfComplaintHistory != null)
            binding.vfComplaintHistory.setDisplayedChild(0);
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
                        if (binding.vfComplaintHistory != null)
                            binding.vfComplaintHistory.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        JSONObject dataObj = dataArr.getJSONObject(0);
                        binding.txtCountCulprit.setText(dataObj.getInt("countCulprit") + "");
                        binding.txtCountComplaint.setText(dataObj.getInt("countComplaint") + "");

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
                            if (binding.vfHeader != null)
                                binding.vfHeader.setDisplayedChild(0);
                        } else if (historyOfWho.equals("customer")) {
                            if (binding.vfHeader != null)
                                binding.vfHeader.setDisplayedChild(1);
                        }

                        if (complaintsHistoryModels.size() == 0) {
                            if (binding.vfComplaintHistory != null)
                                binding.vfComplaintHistory.setDisplayedChild(2);
                        } else {
                            if (binding.vfComplaintHistory != null)
                                binding.vfComplaintHistory.setDisplayedChild(1);

                            mAdapter = new ComplaintsHistoryAdapter(complaintsHistoryModels);
                            binding.listComplaintsHistory.setAdapter(mAdapter);
                        }
                    } else {
                        if (binding.vfComplaintHistory != null)
                            binding.vfComplaintHistory.setDisplayedChild(3);
                    }
                } catch (Exception e) {
                    if (binding.vfComplaintHistory != null)
                        binding.vfComplaintHistory.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, historyCallBack method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfComplaintHistory != null)
                    binding.vfComplaintHistory.setDisplayedChild(3);
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
    }
}