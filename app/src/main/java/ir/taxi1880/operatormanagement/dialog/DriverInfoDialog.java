package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverInfoDialog {

    private static final String TAG = DriverInfoDialog.class.getSimpleName();
    static Dialog dialog;
    Unbinder unbinder;

    @BindView(R.id.txtFullName)
    TextView txtFullName;

    @BindView(R.id.txtFatherName)
    TextView txtFatherName;

    @BindView(R.id.txtNationalCode)
    TextView txtNationalCode;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtGender)
    TextView txtGender;

    @BindView(R.id.txtBirthCertificate)
    TextView txtBirthCertificate;

    @BindView(R.id.txtDriverCode)
    TextView txtDriverCode;

    @BindView(R.id.txtStartDate)
    TextView txtStartDate;

    @BindView(R.id.txtCarClass)
    TextView txtCarClass;

    @BindView(R.id.txtIbenNo)
    TextView txtIbenNo;

    @BindView(R.id.txtVinNo)
    TextView txtVinNo;

    @BindView(R.id.imgSmartTaxiMeter)
    ImageView imgSmartTaxiMeter;

    @BindView(R.id.imgFuelQuota)
    ImageView imgFuelQuota;

    @BindView(R.id.imgConfirmInfo)
    ImageView imgConfirmInfo;

    @OnClick(R.id.imgClose)
    void onPressCLose() {
        dismiss();
    }

    public void show(String driverInfo) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_driver_info);
        unbinder = ButterKnife.bind(this, dialog);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        DataBase dataBase = new DataBase(MyApplication.context);

        try {
            JSONObject driverInfoObj = new JSONObject(driverInfo);
            String city = dataBase.getCityName(driverInfoObj.getInt("cityCode"));
            txtCity.setText(city);
            txtDriverCode.setText(StringHelper.toPersianDigits(driverInfoObj.getInt("driverCode") + ""));
            int carCode = driverInfoObj.getInt("carCode");
            int smartCode = driverInfoObj.getInt("smartCode");
            txtFullName.setText(driverInfoObj.getString("driverName"));
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
            txtCarClass.setText(carClass);
            String gender = driverInfoObj.getInt("gender") == 1 ? "مرد" : "زن";
            txtGender.setText(gender);
            txtNationalCode.setText(StringHelper.toPersianDigits(driverInfoObj.getString("nationalCode")));
            txtFatherName.setText(driverInfoObj.getString("fatherName"));
            txtVinNo.setText(StringHelper.toPersianDigits(driverInfoObj.getString("vin")));
            txtIbenNo.setText(StringHelper.toPersianDigits(driverInfoObj.getString("sheba")));
            txtBirthCertificate.setText(StringHelper.toPersianDigits(driverInfoObj.getString("shenasname")));
            imgSmartTaxiMeter.setImageResource(driverInfoObj.getInt("smartTaximeter") == 1 ? R.drawable.ic_tick : R.drawable.ic_close_black_24dp);
            imgConfirmInfo.setImageResource(driverInfoObj.getInt("confirmation") == 1 ? R.drawable.ic_tick : R.drawable.ic_close_black_24dp);
            int cancelFuel = driverInfoObj.getInt("cancelFuel");
            int fuelRationing = driverInfoObj.getInt("fuelRationing");
            if (fuelRationing == 1) {
                if (cancelFuel == 1) {
                    imgFuelQuota.setImageResource(R.drawable.ic_close_black_24dp);
                } else {
                    imgFuelQuota.setImageResource(R.drawable.ic_tick);
                }
            } else {
                imgFuelQuota.setImageResource(R.drawable.ic_close_black_24dp);
            }
            txtStartDate.setText(StringHelper.toPersianDigits(driverInfoObj.getString("startActiveDate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
        }
        dialog = null;
    }

}
