package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogDriverComplaintRegistreationBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverComplaintRegistrationDialog {

    private static final String TAG = DriverComplaintRegistrationDialog.class.getSimpleName();
    DialogDriverComplaintRegistreationBinding binding;

//    private Spinner spComplaintType;
    private int complaintType;

    static Dialog dialog;

    public void show(String serviceId, String voipId) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogDriverComplaintRegistreationBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        dialog.setCancelable(false);

        initSpinner();
        binding.imgClose.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            dismiss();
        });

        binding.btnSubmit.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            setComplaint(serviceId, voipId);
            dismiss();
        });

        dialog.show();
    }

    private void setComplaint(String serviceId, String voipId) {
//        if (binding.vfLoader != null)
            binding.vfLoader.setDisplayedChild(1);

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.INSERT_DRIVER_COMPLAINT)
                .addParam("serviceId", serviceId)
                .addParam("complaintType", complaintType)
                .addParam("voipId", DataHolder.getInstance().getVoipId())
                .addParam("description", " ")
                .listener(onSetComplaint)
                .post();
    }

    RequestHelper.Callback onSetComplaint = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("result");
                        if (status) {
                            new GeneralDialog()
                                    .title("تایید شد")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                            DataHolder.getInstance().setVoipId("0");
                        } else {
                            new GeneralDialog()
                                    .title("خطا")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        }
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }

//                    if (binding.vfLoader != null) {
                        binding.vfLoader.setDisplayedChild(0);


                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onSetComplaint method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
//                if (binding.vfLoader != null) {
                    binding.vfLoader.setDisplayedChild(0);
            });
        }
    };

    private void initSpinner() {
        ArrayList<TypeServiceModel> typeServiceModels = new ArrayList<>();
        ArrayList<String> serviceList = new ArrayList<String>();
        try {
            JSONArray serviceArr = new JSONArray(MyApplication.prefManager.getDriverComplaint());
            for (int i = 0; i < serviceArr.length(); i++) {
                JSONObject serviceObj = serviceArr.getJSONObject(i);
                TypeServiceModel typeServiceModel = new TypeServiceModel();
                typeServiceModel.setName(serviceObj.getString("ShektypeSharh"));
                typeServiceModel.setId(serviceObj.getInt("sheKtypeId"));
                typeServiceModels.add(typeServiceModel);
                serviceList.add(serviceObj.getString("ShektypeSharh"));
            }
//            if (binding.spComplaintType == null)
//                return;

            binding.spComplaintType.setEnabled(true);

            binding.spComplaintType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

            binding.spComplaintType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (spComplaintType != null)
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    complaintType = typeServiceModels.get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, initSpinner method");
        }
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}