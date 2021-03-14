package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.RecentCallsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter.pauseVoice;

public class RecentCallsDialog {

    Dialog dialog;
    Unbinder unbinder;
    boolean fromPassengerCalls;
    String tell;
    String mobile;

    @BindView(R.id.vfHeader)
    ViewFlipper vfHeader;

    @BindView(R.id.listRecentCalls)
    RecyclerView listRecentCalls;

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

    @BindView(R.id.rgSearchType)
    RadioGroup rgSearchType;

    @OnClick(R.id.rbTell)
    void onTell() {
        if (tell.length() == 10 && !tell.startsWith("0")) {
            tell = "0" + tell;
            getRecentCalls("/src", tell, "/4");
        } else if (tell.length() == 8) {
            tell = "051" + tell;
            getRecentCalls("/src", tell, "/4");
        } else {
            if (vfDownload != null)
                vfDownload.setDisplayedChild(2);
        }
    }

    @OnClick(R.id.rbMobile)
    void onMobile() {
        if (rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
            getRecentCalls("/src", mobile.startsWith("0") ? mobile : "0" + mobile, "/4");
        }
    }

    RecentCallsAdapter mAdapter;
    ArrayList<RecentCallsModel> recentCallsModels;

    public void show(String tell, String mobile, int sip, Boolean fromPassengerCalls) {
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
        this.tell = tell;
        this.mobile = mobile;

        if (fromPassengerCalls) {
            vfHeader.setDisplayedChild(1);
            if (rgSearchType.getCheckedRadioButtonId() == R.id.rbTell) {
                if (tell.length() == 10 && !tell.startsWith("0")) {
                    tell = "0" + tell;
                    getRecentCalls("/src", tell, "/4");
                } else if (tell.length() == 8) {
                    tell = "051" + tell;
                    getRecentCalls("/src", tell, "/4");
                } else {
                    if (vfDownload != null)
                        vfDownload.setDisplayedChild(2);
                }
            } else if (rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                getRecentCalls("/src", mobile.startsWith("0") ? mobile : "0" + mobile, "/4");
            }
        } else {
            vfHeader.setDisplayedChild(0);
            getRecentCalls("/dst", sip + "", "/1");
        }
        dialog.show();
    }

    public void getRecentCalls(String type, String num, String dateInterval) {
        if (vfDownload != null)
            vfDownload.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.RECENT_CALLS + num + type + dateInterval)
                .listener(recentCallsCallBack)
                .get();
    }

    RequestHelper.Callback recentCallsCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    recentCallsModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfDownload != null)
                            vfDownload.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            RecentCallsModel model = new RecentCallsModel();

                            if (!dataObj.getString("disposition").equals("ANSWERED"))
                                continue;

                            model.setTxtDate(dataObj.getString("starttime"));
                            model.setVoipId(dataObj.getString("voiceId"));

                            if (!fromPassengerCalls) {
                                model.setPhone(dataObj.getString("src"));
                            }

                            recentCallsModels.add(model);
                        }

                        if (recentCallsModels.size() == 0) {
                            if (vfDownload != null)
                                vfDownload.setDisplayedChild(2);
                        } else {
                            if (vfDownload != null)
                                vfDownload.setDisplayedChild(1);
                            mAdapter = new RecentCallsAdapter(MyApplication.currentActivity, recentCallsModels);
                            listRecentCalls.setAdapter(mAdapter);
                        }
                    } else {
                        if (vfDownload != null)
                            vfDownload.setDisplayedChild(3);
                    }
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
                    if (vfDownload != null)
                        vfDownload.setDisplayedChild(3);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfDownload != null)
                    vfDownload.setDisplayedChild(3);
            });
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
