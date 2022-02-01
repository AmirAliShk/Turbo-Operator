package ir.taxi1880.operatormanagement.dialog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.downloader.PRDownloader;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RecentCallsAdapterK;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogCallBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class CallDialog {

    private static final String TAG = CallDialog.class.getSimpleName();
    DialogCallBinding binding;
    Dialog dialog;
    Call call;
    Core core;
    CallBack callBack;
    Address callAddress;
    boolean cancelable = true;

    public interface CallBack {
        void onDismiss();

        void onCallReceived();

        void onCallTransferred();

        void onCallEnded();
    }

    public CallDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public void show(CallBack callBack, boolean isFromSupport) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogCallBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        setCancelable(cancelable);
        this.callBack = callBack;
        //TODO  if call is available this layer must be visible
        core = LinphoneService.getCore();
        call = core.getCurrentCall();
        binding.vfCall.setDisplayedChild((call == null) ? 0 : 1);
        core.addListener(coreListener);

        if (MyApplication.prefManager.getCustomerSupport() == 1) {
            binding.llTransfer.setVisibility(View.GONE);
            binding.llCallSupport.setVisibility(View.GONE);
            binding.llOperatorRecentCalls.setVisibility(View.GONE);
        } else if (MyApplication.prefManager.getCustomerSupport() == 0) {
            binding.llSupportOperatorRecentCalls.setVisibility(View.GONE);
        }

        if (isFromSupport) {
            binding.vfCall.setDisplayedChild(1);
        }

        binding.llStationGuide.setOnClickListener(view -> {
            new SearchStationInfoDialog().show(stationCode -> {
            }, 0, false, "", false);
            dismiss();
        });

        binding.llCallDialog.setOnClickListener(view -> {
            return;
        });

        binding.llSupportOperatorRecentCalls.setOnClickListener(view -> {
            dismiss();
            new RecentCallsDialog().show("0", "0", MyApplication.prefManager.getSipNumber(), false, (b) -> {
                if (b) {
                    PRDownloader.cancelAll();
                    PRDownloader.shutDown();
                    RecentCallsAdapterK.Companion.pauseVoice();
                }
            });
        });

        binding.llOperatorRecentCalls.setOnClickListener(view -> {
            dismiss();
            new RecentCallsDialog().show("0", "0", MyApplication.prefManager.getSipNumber(), false, (b) -> {
                if (b) {
                    PRDownloader.cancelAll();
                    PRDownloader.shutDown();
                    RecentCallsAdapterK.Companion.pauseVoice();

                }
            });
        });

        binding.llTransfer.setOnClickListener(view -> {
            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call != null && call.getState() == Call.State.StreamsRunning) {
                    call.transfer("950");
                    callBack.onCallTransferred();
                }
            }
            MyApplication.Toast("تماس به صف پشتیبانی منتقل شد", Toast.LENGTH_SHORT);
//    if (callBack != null)
            dismiss();
        });

        binding.llPause.setOnClickListener(view -> {
            core.getCurrentCall().pause();
            binding.vfPause.setDisplayedChild(1);
        });

        binding.llPlay.setOnClickListener(view -> {
            Call call = core.getCurrentCall();
            if (call == null)
                call = core.getCalls().length > 0 ? core.getCalls()[0] : null;
            if (call != null)
                call.resume();
            binding.vfPause.setDisplayedChild(0);
        });

        binding.llEndCall.setOnClickListener(view -> {
            Core mCore = LinphoneService.getCore();
            Call currentCall = mCore.getCurrentCall();
            for (Call call : mCore.getCalls()) {
                if (call != null && call.getConference() != null) {
//        if (mCore.isInConference()) {
//          displayConferenceCall(call);
//          conferenceDisplayed = true;
//        } else if (!pausedConferenceDisplayed) {
//          displayPausedConference();
//          pausedConferenceDisplayed = true;
//        }
                } else if (call != null && call != currentCall) {
                    Call.State state = call.getState();
                    if (state == Call.State.Paused
                            || state == Call.State.PausedByRemote
                            || state == Call.State.Pausing) {
                        call.terminate();
                        callBack.onCallEnded();
                    }
                } else if (call != null && call == currentCall) {
                    call.terminate();
                    callBack.onCallEnded();
                }
            }
            dismiss();
        });

        binding.llEndCall2.setOnClickListener(view -> {
            try {
                Call call = core.getCallByRemoteAddress2(callAddress);
                if (call != null) {
                    call.terminate();
                    callBack.onCallEnded();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AvaCrashReporter.send(e, TAG + " class, onEndPress method");
            }
            binding.vfCall.setDisplayedChild(0);
            setCancelable(true);
        });

        binding.llCallSupport.setOnClickListener(view -> {
            Address addressToCall = core.interpretUrl("950");
            CallParams params = core.createCallParams(null);
            params.enableVideo(false);
            if (addressToCall != null) {
                core.inviteAddressWithParams(addressToCall, params);
            }
            setCancelable(false);
            callAddress = addressToCall;

            binding.vfCall.setDisplayedChild(2);
        });

        binding.llTestConnection.setOnClickListener(view -> {
            Address addressToCall = core.interpretUrl("998");
            CallParams params = core.createCallParams(null);
            params.enableVideo(false);
            if (addressToCall != null) {
                core.inviteAddressWithParams(addressToCall, params);
            }
            callAddress = addressToCall;

            setCancelable(false);
            binding.vfCall.setDisplayedChild(2);
        });

        binding.imgClose.setOnClickListener(view -> dismiss());

        binding.blrView.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core lc, Call _call, Call.State state, String message) {
            super.onCallStateChanged(lc, _call, state, message);
            call = _call;

            if (state == Call.State.IncomingReceived) {
                callBack.onCallReceived();
            } else if (state == Call.State.Released) {
                dismiss();
            } else if (state == Call.State.Connected) {
            } else if (state == Call.State.End) {
                callBack.onCallEnded();
                dismiss();
            }
        }
    };

    private void dismiss() {
        if (callBack != null)
            callBack.onDismiss();
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }

        dialog = null;
    }

    private void setCancelable(boolean v) {
        if (dialog != null) {
            dialog.setCancelable(v);
            binding.imgClose.setVisibility(v ? View.VISIBLE : View.GONE);
        }
    }
}