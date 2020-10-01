package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class CallDialog {

  private static final String TAG = CallDialog.class.getSimpleName();

  public interface CallBack {
    void onDismiss();

    void onCallReceived();

    void onCallTransferred();

  }

  Unbinder unbinder;

  @BindView(R.id.vfPause)
  ViewFlipper vfPause;

  @Optional
  @OnClick(R.id.llTransfer)
  void onTransferCallPress() {
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
  }

  @OnClick(R.id.llPause)
  void onPausePress() {
    core.getCurrentCall().pause();
    vfPause.setDisplayedChild(1);

  }

  @OnClick(R.id.llPlay)
  void onPlayPress() {
    Call call = core.getCurrentCall();
    if (call == null)
      call = core.getCalls().length > 0 ? core.getCalls()[0] : null;
    if (call != null)
      call.resume();
    vfPause.setDisplayedChild(0);

  }

  @Optional
  @OnClick(R.id.llEndCall)
  void onEndCallPress() {
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
        }
      } else if (call != null && call == currentCall) {
        call.terminate();
      }
    }
    dismiss();
  }

  @Optional
  @OnClick(R.id.llEndCall2)
  void onEndPress() {

    try {
      Call call = core.getCallByRemoteAddress2(callAddress);
      if (call != null)
        call.terminate();
    } catch (Exception e) {
      e.printStackTrace();
      AvaCrashReporter.send(e, "CallDialog class, onEndPress method");

    }

    vfCall.setDisplayedChild(0);
    setCancelable(true);
  }

  @OnClick(R.id.llCallSupport)
  void onCallSupportPress() {
    Address addressToCall = core.interpretUrl("950");
    CallParams params = core.createCallParams(null);
    params.enableVideo(false);
    if (addressToCall != null) {
      core.inviteAddressWithParams(addressToCall, params);
    }
    setCancelable(false);
    callAddress = addressToCall;

    vfCall.setDisplayedChild(2);
  }

  @OnClick(R.id.llLastConversation)
  void onLastConversation() {
    new PlayLastConversationDialog().show();
  }

  @OnClick(R.id.llTestConnection)
  void onTestConnectionPress() {
    Address addressToCall = core.interpretUrl("998");
    CallParams params = core.createCallParams(null);
    params.enableVideo(false);
    if (addressToCall != null) {
      core.inviteAddressWithParams(addressToCall, params);
    }
    callAddress = addressToCall;

    setCancelable(false);
    vfCall.setDisplayedChild(2);
  }

  @OnClick(R.id.llLastCall)
  void onLastCallPress() {
    if (MyApplication.prefManager.getLastCall() == "null") {
      MyApplication.Toast("اخیرا تماسی برقرار نشده است.", Toast.LENGTH_LONG);
    } else {
      Address addressToCall = core.interpretUrl(MyApplication.prefManager.getLastCall());
      CallParams params = core.createCallParams(null);
      params.enableVideo(false);
      if (addressToCall != null) {
        core.inviteAddressWithParams(addressToCall, params);
      }
      callAddress = addressToCall;

      setCancelable(false);
      vfCall.setDisplayedChild(2);
    }
  }

  @OnClick(R.id.imgClose)
  void onClosePress() {
    dismiss();
  }

  @BindView(R.id.vfCall)
  ViewFlipper vfCall;

  @BindView(R.id.imgClose)
  ImageView imgClose;

  Dialog dialog;
  Call call;
  Core core;
  CallBack callBack;
  Address callAddress;

  public void show(CallBack callBack) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_call);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    unbinder = ButterKnife.bind(this, dialog);
    setCancelable(true);
    this.callBack = callBack;
    //TODO  if call is available this layer must be visible
    core = LinphoneService.getCore();
    call = core.getCurrentCall();
    vfCall.setDisplayedChild((call == null) ? 0 : 1);
    core.addListener(coreListener);

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
      AvaCrashReporter.send(e, "CallDialog class, dismiss method");
    }

    dialog = null;
  }

  private void setCancelable(boolean v) {
    if (dialog != null) {
      dialog.setCancelable(v);
      imgClose.setVisibility(v ? View.VISIBLE : View.GONE);
    }
  }
}
