package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

public class SaveResultDialog {
    static Dialog dialog;
    Unbinder unbinder;
    String culprit;
    String result;
    DataBase dataBase;
    int mistakesId;
    LocalBroadcastManager broadcaster;
    View view;

    public interface MistakesResult {
        void onSuccess(boolean success);
    }

    MistakesResult mistakesResult;

    @BindView(R.id.rgResult)
    RadioGroup rgResult;

    @BindView(R.id.rgCulprit)
    RadioGroup rgCulprit;

    @BindView(R.id.vfLoader)
    ViewFlipper vfLoader;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        if (rgCulprit.getCheckedRadioButtonId() == -1) {
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
            case R.id.rbStationRegistrationOperatorBlame:
                culprit = "2";
                break;
            case R.id.rbUnknown:
                culprit = "3";
                break;
        }

        switch (rgResult.getCheckedRadioButtonId()) {
            case R.id.rbDeleteAddress:
                result = "1";
                break;
            case R.id.rbDeleteStation:
                result = "2";
                break;
            case R.id.rbDeleteCity:
                result = "3";
                break;
            case R.id.rbOtherCases:
                result = "4";
                break;
        }

        int listenId = dataBase.getMistakesRow().getId();

        sendResult(culprit, result, listenId);
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
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_save_result);
        view = dialog.getWindow().getDecorView();
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(btnSubmit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        dataBase = new DataBase(MyApplication.context);
        this.mistakesId = complaintId;
        this.mistakesResult = mistakesResult;

        dialog.show();
    }

    private void sendResult(String culprit, String result, int listenId) {
        LoadingDialog.makeCancelableLoader();
        if (vfLoader != null)
            vfLoader.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.LISTEN)
                .addParam("culprit", culprit)
                .addParam("result", result)
                .addParam("listenId", listenId)
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
