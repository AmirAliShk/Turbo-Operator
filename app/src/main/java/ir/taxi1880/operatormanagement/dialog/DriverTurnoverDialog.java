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

    ArrayList<DriverTurnoverModel> driverTurnoverModels;
    DriverTurnoverAdapter adapter;

    @BindView(R.id.vfFinancial)
    ViewFlipper vfFinancial;

    @BindView(R.id.listDriverTurnover)
    ListView listDriverTurnover;

    @OnClick(R.id.imgClose)
    void onPressClose() {
        dismiss();
    }

    public void show(String taxiCode, String carCode) {
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

        getFinancial(taxiCode, carCode);

        dialog.show();

    }

    public void getFinancial(String taxiCode, String carCode) {
        if (vfFinancial != null)
            vfFinancial.setDisplayedChild(0);

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
                    driverTurnoverModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
//                        {"code":703830,"date":"1399/12/16","time":"18:36","sharh":"جريمه کنسلي بيشتر از حد مجاز ماهانه ","debit":15000,"credit":0}
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            DriverTurnoverModel model = new DriverTurnoverModel();
                            model.setDate(dataObj.getString("date"));
                            model.setTime(dataObj.getString("time"));
                            model.setDescription(dataObj.getString("sharh"));
                            model.setAmount(dataObj.getString("debit"));
                            model.setDocumentType(dataObj.getString("credit"));
                            driverTurnoverModels.add(model);
                        }

                        if (driverTurnoverModels.size() == 0) {
                            if (vfFinancial != null)
                                vfFinancial.setDisplayedChild(2);
                        } else {
                            if (vfFinancial != null)
                                vfFinancial.setDisplayedChild(1);
                            adapter = new DriverTurnoverAdapter(MyApplication.context, driverTurnoverModels);
                            listDriverTurnover.setAdapter(adapter);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
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
