package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverStationRegistrationAdapter;
import ir.taxi1880.operatormanagement.adapter.DriverTurnoverAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverStationRegistrationModel;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverStationRegistrationDialog {

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

    public void show(String driverCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_driver_station_registration);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        getRegistrationReport(driverCode);

        dialog.show();
    }

    public void getRegistrationReport(String driverCode) {
        RequestHelper.builder(EndPoints.DRIVER_STATION_REGISTRATION + "/" + driverCode)
                .listener(onGetRegistrationReport)
                .get();
    }

    RequestHelper.Callback onGetRegistrationReport = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    driverStationRegistrationModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                         JSONArray dataArr = listenObj.getJSONArray("data");
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
                                vfStationRegistration.setDisplayedChild(2);
                        } else {
                            if (vfStationRegistration != null)
                                vfStationRegistration.setDisplayedChild(1);
                            adapter = new DriverStationRegistrationAdapter(MyApplication.context, driverStationRegistrationModels);
                            listDriverStationRegistration.setAdapter(adapter);
                        }
                    }
//                    "StationCode": 71,
//                            "StrIndate": "1399/12/23",
//                            "StrInTime": "05:18:50",
//                            "type": "قطع اينترنت",
//                            "StrOutDate": "1399/12/23",
//                            "StrOutTime": "05:38:37",
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
