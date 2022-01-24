package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverStationRegistrationAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverStationRegistrationModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverStationRegistrationDialog {

    public static final String TAG = DriverStationRegistrationDialog.class.getSimpleName();
    Dialog dialog;
    Unbinder unbinder;
    ArrayList<DriverStationRegistrationModel> driverStationRegistrationModels;
    DriverStationRegistrationAdapter adapter;

    @BindView(R.id.vfStationRegistration)
    ViewFlipper vfStationRegistration;

    @BindView(R.id.listDriverStationRegistration)
    ListView listDriverStationRegistration;

    @OnClick(R.id.imgClose)
    void onPressClose() {
        dismiss();
    }

    public void show(JSONArray data) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_driver_station_registration);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        driverStationRegistration(data);

        dialog.show();
    }

    void driverStationRegistration(JSONArray dataArr) {
        driverStationRegistrationModels = new ArrayList<>();

        try {
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                DriverStationRegistrationModel model = new DriverStationRegistrationModel();
                model.setInDate(dataObj.getString("StrIndate"));
                model.setInTime(dataObj.getString("StrInTime"));
                model.setStationCode(dataObj.getString("StationCode"));
                model.setOutType(dataObj.getString("type"));
                model.setOutDate(dataObj.getString("StrOutDate"));
                model.setOutTime(dataObj.getString("StrOutTime"));
                driverStationRegistrationModels.add(model);
            }

            if (driverStationRegistrationModels.size() == 0) {
                if (vfStationRegistration != null)
                    vfStationRegistration.setDisplayedChild(1);
            } else {
                if (vfStationRegistration != null)
                    vfStationRegistration.setDisplayedChild(0);
                adapter = new DriverStationRegistrationAdapter(driverStationRegistrationModels);
                listDriverStationRegistration.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, driverStationRegistration method");
        }
    }

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
        unbinder.unbind();
    }
}