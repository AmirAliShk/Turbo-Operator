package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CallModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class GetStationCodeDialog {
    Dialog dialog;
    Unbinder unbinder;
    String stationCode;
    String serviceDetails;

    @OnClick(R.id.imgClose)
    void onClosePress() {
        dismiss();
    }

    @BindView(R.id.imgClose)
    ImageView imgClose;

    @BindView(R.id.txtNumber)
    TextView txtNumber;

    @BindView(R.id.vfSubmit)
    ViewFlipper vfSubmit;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        stationCode = txtNumber.getText().toString();
        if (stationCode.isEmpty()) {
            MyApplication.Toast("لطفا کد ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
            return;
        }
        insertService(serviceDetails);
    }

    public void show(String serviceDetails) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_get_station_code);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        unbinder = ButterKnife.bind(this, dialog);
        dialog.setCancelable(false);

        this.serviceDetails = serviceDetails;

        dialog.show();
    }

    private void insertService(String serviceDetails) {
        try {
            JSONObject objServiceDetails = new JSONObject(serviceDetails);
            LoadingDialog.makeCancelableLoader();
            RequestHelper.builder(EndPoints.INSERT_TRIP_SENDING_QUEUE)
                    .addParam("phoneNumber", objServiceDetails.getString("customerTel"))
                    .addParam("mobile", objServiceDetails.getString("customerMobile"))
                    .addParam("callerName", objServiceDetails.getString("customerName"))
                    .addParam("fixedComment", objServiceDetails.getString("customerFixedDes"))
                    .addParam("address", objServiceDetails.getString("customerAddress"))
                    .addParam("stationCode", objServiceDetails.getString("stationCode"))
                    .addParam("destinationStation", 0)
                    .addParam("destination", "")
                    .addParam("cityCode", objServiceDetails.getString("cityCode"))
                    .addParam("typeService", objServiceDetails.getString("typeService"))
                    .addParam("description", objServiceDetails.getString("serviceComment"))
                    .addParam("TrafficPlan", objServiceDetails.getString("TrafficPlan"))
                    .addParam("voipId", objServiceDetails.getString("VoipId"))
                    .addParam("classType", objServiceDetails.getString("classType")) //
                    .addParam("defaultClass", objServiceDetails.getString("defaultClass")) //
                    .addParam("count", objServiceDetails.getString("count")) // 1
                    .addParam("queue", objServiceDetails.getString("queue")) //
                    .addParam("senderClient", 0)
                    .listener(insertService)
                    .post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback insertService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (vfSubmit != null)
                        vfSubmit.setDisplayedChild(0);
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {

                        new GeneralDialog()
                                .title("ثبت شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .secondButton("بستن", null)
                                .show();
                    }
                    LoadingDialog.dismissCancelableDialog();

                } catch (JSONException e) {
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "TripRegisterActivity class, insertService onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                if (vfSubmit != null)
                    vfSubmit.setDisplayedChild(0);
            });
        }

        @Override
        public void onReloadPress(boolean v) {

            super.onReloadPress(v);
            try {
                if (v)
                    MyApplication.handler.post(LoadingDialog::makeCancelableLoader);
                else
                    MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);

            } catch (Exception e) {
                e.printStackTrace();
                AvaCrashReporter.send(e, "GetStationCodeDialog class, onReloadPress method");
            }
        }
    };

    private void dismiss() {
        MyApplication.handler.postDelayed(() -> KeyBoardHelper.hideKeyboard(), 30);
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.getMessage();
        }

        dialog = null;
    }

}
