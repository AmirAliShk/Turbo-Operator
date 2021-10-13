package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.MistakeReasonsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

public class SaveMistakeResultDialog {
    static Dialog dialog;
    private Dialog staticDialog = null;
    Unbinder unbinder;
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

    @BindView(R.id.rgResult)
    RadioGroup rgResult;

    @BindView(R.id.rbAnotherOperator)
    RadioButton rbAnotherOperator;

    @BindView(R.id.rbUnknown)
    RadioButton rbUnknown;

    @BindView(R.id.rbStationRegistrationDestinationOperatorBlame)
    RadioButton rbStationRegistrationDestinationOperatorBlame;

    @BindView(R.id.rbStationRegistrationOriginOperatorBlame)
    RadioButton rbStationRegistrationOriginOperatorBlame;

    @BindView(R.id.rbTripRegistrationOperatorBlame)
    RadioButton rbTripRegistrationOperatorBlame;

    @BindView(R.id.rgCulprit)
    RadioGroup rgCulprit;

    @BindView(R.id.vfLoader)
    ViewFlipper vfLoader;

    @BindView(R.id.edtAnotherOperator)
    EditText edtAnotherOperator;

    @BindView(R.id.spinnerSaveMisRes)
    Spinner reasonSpinner;

    @OnClick(R.id.lLMainSaveMistake)
    void onOpenSpinner()
    {
        reasonSpinner.performClick();
    }

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (rgCulprit.getCheckedRadioButtonId() == -1 && !rbAnotherOperator.isChecked()) {
            MyApplication.Toast("لطفا مقصر را مشخص کنید.", Toast.LENGTH_SHORT);
            return;
        }

        if (rgResult.getCheckedRadioButtonId() == -1) {
            MyApplication.Toast("لطفا نتیجه را مشخص کنید.", Toast.LENGTH_SHORT);
            return;
        }

        switch (rgCulprit.getCheckedRadioButtonId()) {
            case R.id.rbTripRegistrationOperatorBlame:
                culprit = "1";
                break;
            case R.id.rbStationRegistrationOriginOperatorBlame:
                culprit = "2";
                break;
            case R.id.rbStationRegistrationDestinationOperatorBlame:
                culprit = "4";
                break;
            case R.id.rbUnknown:
                culprit = "3";
                break;
        }
        if (rbAnotherOperator.isChecked()) {
            culprit = "5";
        }
        if (rbAnotherOperator.isChecked() && (edtAnotherOperator.getText().toString().isEmpty() || edtAnotherOperator.getText().toString().equals("0"))) {
            MyApplication.Toast("لطفا سیپ اپراتور را وارد کنید.", Toast.LENGTH_SHORT);
            return;
        }

        switch (rgResult.getCheckedRadioButtonId()) {
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
        if (edtAnotherOperator.getText().toString().isEmpty()) {
            sipNumber = "0";
        } else {
            sipNumber = edtAnotherOperator.getText().toString();
        }

        sendResult(culprit, result, listenId, sipNumber);
    }

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

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
        }
        tempDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tempDialog.setContentView(R.layout.dialog_save_mistake_result);
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tempDialog.setCancelable(false);
        unbinder = ButterKnife.bind(this, tempDialog);
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
        edtAnotherOperator.setEnabled(false);
        rbAnotherOperator.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbAnotherOperator.isChecked()) {
                rgCulprit.clearCheck();
                edtAnotherOperator.setEnabled(true);
                if (!edtAnotherOperator.getText().toString().isEmpty()) {
                    sipNumber = edtAnotherOperator.getText().toString();
                }
            } else {
                edtAnotherOperator.setEnabled(false);
            }
        });

        rbStationRegistrationDestinationOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbStationRegistrationDestinationOperatorBlame.isChecked()) {
                rbAnotherOperator.setChecked(false);
            }
        });

        rbStationRegistrationOriginOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbStationRegistrationOriginOperatorBlame.isChecked()) {
                rbAnotherOperator.setChecked(false);
            }
        });

        rbTripRegistrationOperatorBlame.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbTripRegistrationOperatorBlame.isChecked()) {
                rbAnotherOperator.setChecked(false);
            }
        });

        rbUnknown.setOnCheckedChangeListener((compoundButton, b) -> {
            if (rbUnknown.isChecked()) {
                rbAnotherOperator.setChecked(false);
            }
        });

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
            reasonSpinner.setAdapter(new SpinnerAdapter(MyApplication.context, R.layout.item_spinner, reasons));
            reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0)
                    {
                        reasonId = 0;
                        return;
                    }
                    reasonId = mistakeReasons.get(position - 1).getId();
                    Log.i("TAF",reasonId+"");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendResult(String culprit, String result, int listenId, String sipNumber) {
        LoadingDialog.makeCancelableLoader();
        if (vfLoader != null)
            vfLoader.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.V1_LISTEN)
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
                        if (vfLoader != null)
                            vfLoader.setDisplayedChild(0);
                    }
                } catch (Exception e) {
                    LoadingDialog.dismissCancelableDialog();
                    mistakesResult.onSuccess(false);
                    if (vfLoader != null)
                        vfLoader.setDisplayedChild(0);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                mistakesResult.onSuccess(false);
                if (vfLoader != null)
                    vfLoader.setDisplayedChild(0);
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
            AvaCrashReporter.send(e, "SaveResultDialog class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}
