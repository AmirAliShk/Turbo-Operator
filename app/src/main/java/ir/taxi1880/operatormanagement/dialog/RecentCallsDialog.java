package ir.taxi1880.operatormanagement.dialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.downloader.PRDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapter;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapterK;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogRecentCallsBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.RecentCallsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class RecentCallsDialog {
    public static final String TAG = RecentCallsDialog.class.getSimpleName();
    Dialog dialog;
    DialogRecentCallsBinding binding;
    boolean fromPassengerCalls;
    String tell;
    String mobile;

    public interface DismissInterface {
        void onDismiss(boolean b);
    }

    DismissInterface dismissInterface;

    RecentCallsAdapter mAdapter;
    ArrayList<RecentCallsModel> recentCallsModels;

    public void show(String tell, String mobile, int sip, boolean fromPassengerCalls, DismissInterface dismissInterface) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogRecentCallsBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        this.fromPassengerCalls = fromPassengerCalls;
        this.tell = tell;
        this.mobile = mobile;
        this.dismissInterface = dismissInterface;

        if (fromPassengerCalls) {
            if (binding.vfHeader != null)
                binding.vfHeader.setDisplayedChild(1);
            if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbTell) {
                if (tell.length() == 10 && !tell.startsWith("0")) {
                    tell = "0" + tell;
                    getRecentCalls("/src", tell, "/4");
                } else if (tell.length() == 8) {
                    tell = "051" + tell;
                    getRecentCalls("/src", tell, "/4");
                } else {
                    if (binding.vfDownload != null)
                        binding.vfDownload.setDisplayedChild(2);
                }
            } else if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                getRecentCalls("/src", mobile.startsWith("0") ? mobile : "0" + mobile, "/4");
            }
        } else {
            if (binding.vfHeader != null)
                binding.vfHeader.setDisplayedChild(0);
            getRecentCalls("/dst", sip + "", "/1");
        }

        binding.rbTell.setOnClickListener(view -> {
            if (this.tell.length() == 10 && !this.tell.startsWith("0")) {
                this.tell = "0" + this.tell;
                getRecentCalls("/src", this.tell, "/4");
            } else if (this.tell.length() == 8) {
                this.tell = "051" + this.tell;
                getRecentCalls("/src", this.tell, "/4");
            } else {
                if (binding.vfDownload != null)
                    binding.vfDownload.setDisplayedChild(2);
            }
        });

        binding.rbMobile.setOnClickListener(view -> {
            if (binding.rgSearchType.getCheckedRadioButtonId() == R.id.rbMobile) {
                getRecentCalls("/src", mobile.startsWith("0") ? mobile : "0" + mobile, "/4");
            }
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    public void getRecentCalls(String type, String num, String dateInterval) {
        if (binding.vfDownload != null)
            binding.vfDownload.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.RECENT_CALLS + num.trim() + type + dateInterval)
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
                        if (binding.vfDownload != null)
                            binding.vfDownload.setDisplayedChild(1);
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
                            } else {
                                model.setDestinationOperator(dataObj.getString("dst"));
                            }

                            recentCallsModels.add(model);
                        }

                        if (recentCallsModels.size() == 0) {
                            if (binding.vfDownload != null)
                                binding.vfDownload.setDisplayedChild(2);
                        } else {
                            if (binding.vfDownload != null)
                                binding.vfDownload.setDisplayedChild(1);
                            mAdapter = new RecentCallsAdapter(recentCallsModels);
                            binding.listRecentCalls.setAdapter(mAdapter);
                        }
                    } else {
                        if (binding.vfDownload != null)
                            binding.vfDownload.setDisplayedChild(3);
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
                    if (binding.vfDownload != null)
                        binding.vfDownload.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, recentCallsCallBack method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfDownload != null)
                    binding.vfDownload.setDisplayedChild(3);
            });
            super.onFailure(reCall, e);
        }
    };

    private void dismiss() {
        Log.i("Taf","Hi before True");
        dismissInterface.onDismiss(true);
        Log.i("Taf","Hi after True");
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
        PRDownloader.cancelAll();
        PRDownloader.shutDown();
//        pauseVoice();
        RecentCallsAdapterK.Companion.pauseVoice();
    }
}