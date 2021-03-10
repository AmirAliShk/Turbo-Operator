package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerCallsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

public class RecentCallsDialog {

    Dialog dialog;
    Unbinder unbinder;
    boolean fromPassengerCalls;

    @BindView(R.id.vfHeader)
    ViewFlipper vfHeader;

    @BindView(R.id.listPassengerCalls)
    RecyclerView listPassengerCalls;

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    @BindView(R.id.vfDownload)
    ViewFlipper vfDownload;

    @BindView(R.id.progressDownload)
    ProgressBar progressDownload;

    @BindView(R.id.textProgress)
    TextView textProgress;

    RecentCallsAdapter mAdapter;
    ArrayList<PassengerCallsModel> passengerCallsModels;

    public void show(String tell, int sip, Boolean fromPassengerCalls) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_recent_calls);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        this.fromPassengerCalls = fromPassengerCalls;

        if (fromPassengerCalls) {
            vfHeader.setDisplayedChild(1);
            getPassengerCalls("/src",tell.startsWith("0") ? tell : "0" + tell, "/4");
        } else {
            vfHeader.setDisplayedChild(0);
            getPassengerCalls("/dst",sip + "", "/1");
        }
        dialog.show();
    }

    public void getPassengerCalls(String type, String num, String dateInterval) {
        if (vfDownload != null)
            vfDownload.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.RECENT_CALLS + num + type + dateInterval)
                .listener(passengerCallsCallBack)
                .get();
    }

    RequestHelper.Callback passengerCallsCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    passengerCallsModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfDownload != null)
                            vfDownload.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            PassengerCallsModel model = new PassengerCallsModel();

                            if (!dataObj.getString("disposition").equals("ANSWERED"))
                                continue;

                            model.setTxtDate(dataObj.getString("starttime"));
                            model.setTxtTime(dataObj.getString("disposition"));
                            model.setVoipId(dataObj.getString("voiceId"));

                            if (!fromPassengerCalls) {
                                model.setPhone(dataObj.getString("src"));
                            }

                            passengerCallsModels.add(model);
                        }
                    }
                    mAdapter = new RecentCallsAdapter(MyApplication.currentActivity, passengerCallsModels);
                    listPassengerCalls.setAdapter(mAdapter);

//                    "id": "6044cfee3214a60468e2a298",
//                     "src": "09376148583",
//                     "starttime": "2021-03-07T13:06:53.890Z",
//                     "voiceId": "1615122413.10363140",
//                     "duration": 1,
//                     "disposition": "NO ANSWER", "BUSY", "ANSWERED", " "
//                     "dst": "1880",
//                     "type": "incoming",
//                     "queueName": "1880",
//                     "endtime": "2021-03-07T13:06:54.890Z"

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
        pauseVoice();
        dialog = null;
        unbinder.unbind();
    }
}
