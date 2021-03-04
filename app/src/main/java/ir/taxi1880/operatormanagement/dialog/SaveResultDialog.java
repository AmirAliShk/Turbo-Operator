package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SaveResultDialog {
    static Dialog dialog;
    Unbinder unbinder;
    String culprit;
    String result;
    DataBase dataBase;
    int complaintId;

    public interface ComplaintResult {
        void onSuccess(boolean success);
    }

    ComplaintResult complaintResult;

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

        int listenId = dataBase.getComplaintRow().getId();

        sendResult(culprit, result, listenId);
    }

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    public void show(int complaintId, ComplaintResult complaintResult) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_save_result);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(btnSubmit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        dataBase = new DataBase(MyApplication.context);
        this.complaintId = complaintId;
        this.complaintResult = complaintResult;

        dialog.show();
    }

    private void sendResult(String culprit, String result, int listenId) {
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
                                        complaintResult.onSuccess(true);
                                        dismiss();
                                        dataBase.deleteComplaintRow(complaintId);
                                    })
                                    .show();
                        }
                        if (vfLoader != null)
                            vfLoader.setDisplayedChild(0);
                    }
                } catch (Exception e) {
                    complaintResult.onSuccess(false);
                    if (vfLoader != null)
                        vfLoader.setDisplayedChild(0);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                complaintResult.onSuccess(false);
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
