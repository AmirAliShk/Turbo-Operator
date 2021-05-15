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
    JSONObject objServiceDetails;

    @OnClick(R.id.imgClose)
    void onClosePress() {
        dismiss();
    }

    @BindView(R.id.imgClose)
    ImageView imgClose;

    @BindView(R.id.edtNumber)
    TextView edtNumber;

    @BindView(R.id.vfSubmit)
    ViewFlipper vfSubmit;

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        stationCode = edtNumber.getText().toString();
        if (stationCode.isEmpty()) {
            MyApplication.Toast("لطفا کد ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
            return;
        }
        new GeneralDialog()
                .title("هشدار")
                .message("آیا از ارسال مجدد سرویس اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::cancelService)
                .secondButton("خیر", null)
                .show();
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
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        unbinder = ButterKnife.bind(this, dialog);
        dialog.setCancelable(false);

        try {
            objServiceDetails = new JSONObject(serviceDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();
    }

    private void cancelService() {
        try {
            LoadingDialog.makeCancelableLoader();
            if (vfSubmit != null) {
                vfSubmit.setDisplayedChild(1);
            }
            RequestHelper.builder(EndPoints.CANCEL)
                    .addParam("serviceId", objServiceDetails.getString("serviceId"))
                    .addParam("scope", "driver")
                    .listener(onCancelService)
                    .post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onCancelService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {

                            insertService(objServiceDetails);// register service again...

                        } else {
                            //TODO  what to do? show error dialog?
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDialog.dismissCancelableDialog();
                    if (vfSubmit != null)
                        vfSubmit.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                //TODO  what to do? show error dialog?
                LoadingDialog.dismissCancelableDialog();
                if (vfSubmit != null)
                    vfSubmit.setDisplayedChild(0);
            });
        }

    };

    private void insertService(JSONObject objServiceDetails) {
        try {
            RequestHelper.builder(EndPoints.INSERT_TRIP_SENDING_QUEUE)
                    .addParam("phoneNumber", objServiceDetails.getString("customerTel").trim())
                    .addParam("mobile", objServiceDetails.getString("customerMobile").trim())
                    .addParam("callerName", objServiceDetails.getString("customerName"))
                    .addParam("fixedComment", objServiceDetails.getString("customerFixedDes"))
                    .addParam("address", objServiceDetails.getString("customerAddress"))
                    .addParam("stationCode", stationCode)
                    .addParam("destinationStation", 0)
                    .addParam("destination", " ")
                    .addParam("cityCode", objServiceDetails.getInt("cityCode"))
                    .addParam("typeService", objServiceDetails.getInt("ServiceTypeId"))
                    .addParam("description", objServiceDetails.getString("serviceComment"))
                    .addParam("TrafficPlan", objServiceDetails.getInt("TrafficPlan"))
                    .addParam("voipId", objServiceDetails.getString("VoipId"))
                    .addParam("classType", objServiceDetails.getInt("classType"))
                    .addParam("defaultClass", 0)
                    .addParam("count", 1)
                    .addParam("queue", objServiceDetails.getString("queue"))
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
                    LoadingDialog.dismissCancelableDialog();
                    if (vfSubmit != null) {
                        vfSubmit.setDisplayedChild(0);
                    }
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        new GeneralDialog()
                                .title("ثبت شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", () -> dismiss())
                                .show();
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .secondButton("بستن", null)
                                .show();
                    }
                } catch (JSONException e) {
                    //TODO  what to do? show error dialog?
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                    if (vfSubmit != null)
                        vfSubmit.setDisplayedChild(0);
                    AvaCrashReporter.send(e, "TripRegisterActivity class, insertService onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                //TODO  what to do? show error dialog?
                if (vfSubmit != null)
                    vfSubmit.setDisplayedChild(0);
            });
        }

        @Override
        public void onReloadPress(boolean v) {
            super.onReloadPress(v);
        }
    };

    private void dismiss() {
        MyApplication.handler.postDelayed(() -> KeyBoardHelper.hideKeyboard(), 50);
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
