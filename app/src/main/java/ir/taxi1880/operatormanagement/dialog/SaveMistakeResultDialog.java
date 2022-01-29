package ir.taxi1880.operatormanagement.dialog;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.databinding.DialogSaveMistakeResultBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.MistakeReasonsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SaveMistakeResultDialog {
    public static final String TAG = SaveMistakeResultDialog.class.getSimpleName();
    static Dialog dialog;
    private Dialog staticDialog = null;
    DialogSaveMistakeResultBinding binding;
    String culprit;
    String result;
    String sipNumber = "0";
    DataBase dataBase;
    int mistakesId;
    LocalBroadcastManager broadcaster;
    private boolean singleInstance = true;
    View view;
    int reasonId;

    public interface MistakesResult {
        void onSuccess(boolean success);

        void dismiss();
    }

    MistakesResult mistakesResult;

    public void show(int complaintId, MistakesResult mistakesResult) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        Dialog tempDialog = null;
        if (singleInstance) {
            if (staticDialog != null) {
                staticDialog.dismiss();
                staticDialog = null;
            }
            staticDialog = new Dialog(MyApplication.currentActivity);
            tempDialog = staticDialog;
        } else {
            dialog = new Dialog(MyApplication.currentActivity);
            tempDialog = dialog;
            binding = DialogSaveMistakeResultBinding.inflate(LayoutInflater.from(dialog.getContext()));
        }
        tempDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tempDialog.setContentView(binding.getRoot());
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tempDialog.setCancelable(false);
        TypefaceUtil.overrideFonts(tempDialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = tempDialog.getWindow().getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        tempDialog.getWindow().setAttributes(wlp);
        initMistakeReasonsSpinner();

        dataBase = new DataBase(MyApplication.context);
        this.mistakesId = complaintId;
        this.mistakesResult = mistakesResult;
        binding.edtAnotherOperator.setEnabled(false);
        binding.rbAnotherOperator.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbAnotherOperator.isChecked()) {
                binding.rgCulprit.clearCheck();
                binding.edtAnotherOperator.setEnabled(true);
                if (!binding.edtAnotherOperator.getText().toString().isEmpty()) {
                    sipNumber = binding.edtAnotherOperator.getText().toString();
                }
            } else {
                binding.edtAnotherOperator.setEnabled(false);
            }
        });

        binding.rbStationRegistrationDestinationOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbStationRegistrationDestinationOperatorBlame.isChecked()) {
                binding.rbAnotherOperator.setChecked(false);
            }
        });

        binding.rbStationRegistrationOriginOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbStationRegistrationOriginOperatorBlame.isChecked()) {
                binding.rbAnotherOperator.setChecked(false);
            }
        });

        binding.rbTripRegistrationOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbTripRegistrationOperatorBlame.isChecked()) {
                binding.rbAnotherOperator.setChecked(false);
            }
        });

        binding.rbUnknown.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.rbUnknown.isChecked()) {
                binding.rbAnotherOperator.setChecked(false);
            }
        });

        binding.lLMainSaveMistake.setOnClickListener(view1 -> binding.spinnerSaveMisRes.performClick());

        binding.btnSubmit.setOnClickListener(view1 -> {
            if (binding.rgCulprit.getCheckedRadioButtonId() == -1 && !binding.rbAnotherOperator.isChecked()) {
                MyApplication.Toast("لطفا مقصر را مشخص کنید.", Toast.LENGTH_SHORT);
                return;
            }

            if (binding.rgResult.getCheckedRadioButtonId() == -1) {
                MyApplication.Toast("لطفا نتیجه را مشخص کنید.", Toast.LENGTH_SHORT);
                return;
            }

            if (reasonId == 0) {
                MyApplication.Toast("لطفا دلیل را انتخاب کنید.", Toast.LENGTH_SHORT);
                return;
            }
            switch (binding.rgCulprit.getCheckedRadioButtonId()) {
                case R.id.rbTripRegistrationOperatorBlame:
                    culprit = "1";
                    break;
                case R.id.rbStationRegistrationOriginOperatorBlame:
                    culprit = "2";
                    break;
                case R.id.rbStationRegistrationDestinationOperatorBlame:
                    culprit = "4";
                    break;
                case R.id.rbChecker:
                    culprit = "6";
                    break;
                case R.id.rbUnknown:
                    culprit = "3";
                    break;
            }
            if (binding.rbAnotherOperator.isChecked()) {
                culprit = "5";
            }
            if (binding.rbAnotherOperator.isChecked() && (binding.edtAnotherOperator.getText().toString().isEmpty() || binding.edtAnotherOperator.getText().toString().equals("0"))) {
                MyApplication.Toast("لطفا سیپ اپراتور را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }

            switch (binding.rgResult.getCheckedRadioButtonId()) {
                case R.id.rbDeleteOriginAddress:
                    result = "1";
                    break;
                case R.id.rbDeleteDestinationAddress:
                    result = "6";
                    break;
                case R.id.rbDeleteOriginStation:
                    result = "2";
                    break;
                case R.id.rbDeleteDestinationStation:
                    result = "5";
                    break;
                case R.id.rbDeleteCity:
                    result = "3";
                    break;
                case R.id.rbOtherCases:
                    result = "4";
                    break;
            }

            int listenId = dataBase.getMistakesRow().getId();
            if (binding.edtAnotherOperator.getText().toString().isEmpty()) {
                sipNumber = "0";
            } else {
                sipNumber = binding.edtAnotherOperator.getText().toString();
            }

            sendResult(culprit, result, listenId, sipNumber);
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        tempDialog.show();
    }

    private void initMistakeReasonsSpinner() {
        ArrayList<MistakeReasonsModel> mistakeReasons = new ArrayList<>();
        ArrayList<String> reasons = new ArrayList<>();
        try {
            JSONArray jReasons = new JSONArray(MyApplication.prefManager.getMistakeReason());
            reasons.add(0, "یک دلیل انتخاب شود ...");
            for (int i = 0; i < jReasons.length(); i++) {
                JSONObject reasonObj = jReasons.getJSONObject(i);

                MistakeReasonsModel mistakeReason = new MistakeReasonsModel();
                mistakeReason.setId(reasonObj.getInt("id"));
                mistakeReason.setReasons(reasonObj.getString("reason"));

                mistakeReasons.add(mistakeReason);
                reasons.add(i + 1, reasonObj.getString("reason"));
            }
            binding.spinnerSaveMisRes.setAdapter(new SpinnerAdapter(MyApplication.context, R.layout.item_spinner, reasons));
            binding.spinnerSaveMisRes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        reasonId = 0;
                        return;
                    }
                    reasonId = mistakeReasons.get(position - 1).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, initMistakeReasonsSpinner method");
        }
    }

    private void sendResult(String culprit, String result, int listenId, String sipNumber) {
        LoadingDialog.makeCancelableLoader();
        if (binding.vfLoader != null)
            binding.vfLoader.setDisplayedChild(1);
//        api/operator/v3/support/v3/listen
        RequestHelper.builder(EndPoints.V2_LISTEN)
                .addParam("culprit", culprit)
                .addParam("result", result)
                .addParam("listenId", listenId)
                .addParam("sipNumber", sipNumber)
                .addParam("reasonId", reasonId)
                .listener(sendResult)
                .put();
    }

    RequestHelper.Callback sendResult = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("تایید شد")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {

                                        mistakesResult.onSuccess(true);
                                        dismiss();
                                        dataBase.deleteMistakesRow(mistakesId);
                                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                                        Intent broadcastIntent = new Intent(KEY_PENDING_MISTAKE_COUNT);
                                        broadcastIntent.putExtra(PENDING_MISTAKE_COUNT, dataBase.getMistakesCount());
                                        broadcaster.sendBroadcast(broadcastIntent);
                                    })
                                    .show();
                        }
                        if (binding.vfLoader != null)
                            binding.vfLoader.setDisplayedChild(0);
                    }
                } catch (Exception e) {
                    LoadingDialog.dismissCancelableDialog();
                    mistakesResult.onSuccess(false);
                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, sendResult method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                mistakesResult.onSuccess(false);
                if (binding.vfLoader != null)
                    binding.vfLoader.setDisplayedChild(0);
            });
        }
    };

    private void dismiss() {
        mistakesResult.dismiss();
        try {
            if (singleInstance) {
                if (staticDialog != null) {
                    staticDialog.dismiss();
                    staticDialog = null;
                }
            } else {
                if (dialog != null)
                    if (dialog.isShowing())
                        dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}