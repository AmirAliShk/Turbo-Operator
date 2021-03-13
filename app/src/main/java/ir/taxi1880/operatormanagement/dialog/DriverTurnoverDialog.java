package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverTurnoverAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

public class DriverTurnoverDialog {

    Dialog dialog;
    Unbinder unbinder;

    ListView listDriverTurnover;
    ArrayList<DriverTurnoverModel> driverTurnoverModels;

    public void show(String taxiCode, String carCode){
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_driver_turnover);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        listDriverTurnover = dialog.findViewById(R.id.listDriverTurnover);

        getFinancial(taxiCode, carCode);

//        driverTurnoverModels = new ArrayList<>();
//
//        DriverTurnoverAdapter driverTurnoverAdapter = new DriverTurnoverAdapter(MyApplication.context, driverTurnoverModels);
//
//        driverTurnoverModels.add(new DriverTurnoverModel());
//        driverTurnoverModels.add(new DriverTurnoverModel());
//        driverTurnoverModels.add(new DriverTurnoverModel());
//        driverTurnoverModels.add(new DriverTurnoverModel());
//
//        listDriverTurnover.setAdapter(driverTurnoverAdapter);

    }

    public void getFinancial(String taxiCode, String carCode){
        RequestHelper.builder(EndPoints.DRIVER_FINANCIAL)
                .ignore422Error(true)
                .addPath(taxiCode) // driverCode
                .addPath(carCode) // carCode
                .listener(onGetFinancial)
                .get();
    }

    RequestHelper.Callback onGetFinancial = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
        }
    } ;

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
