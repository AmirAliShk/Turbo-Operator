package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.databinding.DialogDriverInfoBinding;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverInfoDialog {

    private static final String TAG = DriverInfoDialog.class.getSimpleName();
    static Dialog dialog;
    DialogDriverInfoBinding binding;
    String driverMobile;

    public void show(String driverInfo) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogDriverInfoBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        DataBase dataBase = new DataBase(MyApplication.context);

        try {
            JSONObject driverInfoObj = new JSONObject(driverInfo);
            String city = dataBase.getCityName(driverInfoObj.getInt("cityCode"));
            binding.txtCity.setText(city);
            binding.txtDriverCode.setText(StringHelper.toPersianDigits(driverInfoObj.getInt("driverCode") + ""));
            int carCode = driverInfoObj.getInt("carCode");
            int smartCode = driverInfoObj.getInt("smartCode");
            binding.txtFullName.setText(driverInfoObj.getString("driverName"));
            driverMobile = driverInfoObj.getString("driverMobile");
            String carClass = "ثبت نشده";
            switch (driverInfoObj.getInt("carClass")) {
                case 1:
                    carClass = "اقتصادی";
                    break;

                case 2:
                    carClass = "ممتاز";
                    break;

                case 3:
                    carClass = "تشریفات";
                    break;

                case 4:
                    carClass = "تاکسی";
                    break;
            }
            binding.txtCarClass.setText(carClass);
            String gender = driverInfoObj.getInt("gender") == 1 ? "مرد" : "زن";
            int isLock = driverInfoObj.getInt("isLock");
            String lockDes = driverInfoObj.getString("lockDes");
            String lockFromDate = StringHelper.toPersianDigits(driverInfoObj.getString("lockFromDate").substring(5));
            String lockFromTime = StringHelper.toPersianDigits(driverInfoObj.getString("lockFromTime").substring(0, 5));

//                         String outDate = driverStationRegistrationModels.getOutDate().substring(5);
//            String outTime = driverStationRegistrationModels.getOutTime().substring(0,5);
//                        "lockFromDate": "1400/01/07",
//                                "lockFromTime": "17:23:54",
            binding.txtGender.setText(gender);
            binding.txtNationalCode.setText(StringHelper.toPersianDigits(driverInfoObj.getString("nationalCode")));
            binding.txtFatherName.setText(driverInfoObj.getString("fatherName"));
            binding.txtVinNo.setText(StringHelper.toPersianDigits(driverInfoObj.getString("vin")));
            binding.txtIbenNo.setText(StringHelper.toPersianDigits(driverInfoObj.getString("sheba")));
            binding.txtBirthCertificate.setText(StringHelper.toPersianDigits(driverInfoObj.getString("shenasname")));
            binding.imgSmartTaxiMeter.setImageResource(driverInfoObj.getInt("smartTaximeter") == 1 ? R.drawable.ic_true : R.drawable.ic_false);
            binding.imgConfirmInfo.setImageResource(driverInfoObj.getInt("confirmation") == 1 ? R.drawable.ic_true : R.drawable.ic_false);
            int cancelFuel = driverInfoObj.getInt("cancelFuel");
            int fuelRationing = driverInfoObj.getInt("fuelRationing");
            if (fuelRationing == 1) {
                if (cancelFuel == 1) {
                    binding.imgFuelQuota.setImageResource(R.drawable.ic_false);
                } else {
                    binding.imgFuelQuota.setImageResource(R.drawable.ic_true);
                }
            } else {
                binding.imgFuelQuota.setImageResource(R.drawable.ic_false);
            }

            String statusMessage = "";

            if (isLock == 2) {
                binding.rlLockStatus.setVisibility(View.VISIBLE);
                statusMessage = "راننده به دلیل " + lockDes + " از تاریخ " + lockFromDate + " ساعت " + lockFromTime + " قفل خواهد شد.";
            } else if (isLock == 1) {
                binding.rlLockStatus.setVisibility(View.VISIBLE);
                statusMessage = "راننده به دلیل " + lockDes + " قفل میباشد.";
            }
            binding.txtLockStatus.setText(statusMessage);

            binding.txtStartDate.setText(StringHelper.toPersianDigits(driverInfoObj.getString("startActiveDate")));
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, show method");
        }

        binding.llSendLinkToDriver.setOnClickListener(view -> sendAppLink(driverMobile));

        binding.imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    public void sendAppLink(String mobile) {
        if (binding.vfLoader != null)
            binding.vfLoader.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.DRIVER_SEND_APP_LINK)
                .ignore422Error(true)
                .addParam("mobile", mobile)
                .listener(sendLinkCallBack)
                .post();
    }

    RequestHelper.Callback sendLinkCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("ثبت شد")
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

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, sendLinkCallBack method");
                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfLoader != null)
                    binding.vfLoader.setDisplayedChild(0);
            });
        }
    };

    private static void dismiss() {
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