package ir.taxi1880.operatormanagement.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class CallIncomingActivity extends AppCompatActivity {
  public static final String TAG = CallIncomingActivity.class.getSimpleName();
  private CoreListenerStub mListener;
  NotificationManager mNotificationManager;

  @BindView(R.id.txtCallerNum)
  TextView txtCallerNum;

  @BindView(R.id.txtCallerName)
  TextView txtCallerName;

  @OnClick(R.id.imgAccept)
  void onAcceptPress() {
    try {
      call.accept();
    }catch (Exception e){
      e.printStackTrace();
      AvaCrashReporter.send(e,"CallIncomingActivity class, onAcceptPress");
    }
//    txtTimer.setVisibility(View.VISIBLE);
//    startTimer();
  }

//  @BindView(R.id.txtTimer)
//  TextView txtTimer;

  @OnClick(R.id.imgReject)
  void onRejectPress() {
    Core mCore = LinphoneService.getCore();
    Call currentCall = mCore.getCurrentCall();
    boolean flagTerminate = false;
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
        if (state == Call.State.Paused || state == Call.State.PausedByRemote || state == Call.State.Pausing) {
          call.terminate();
          flagTerminate = true;
        }
      } else if (call != null && call == currentCall) {
        flagTerminate = true;
        call.terminate();
      }
    }
    if(!flagTerminate){
      finish();
    }
  }

  Unbinder unbinder;
  Call call;
  int notifManagerId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_call_incoming);
    View view = getWindow().getDecorView();
    getSupportActionBar().hide();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
      window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(notifManagerId);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    unbinder = ButterKnife.bind(this);
    TypefaceUtil.overrideFonts(view);

    mListener =
            new CoreListenerStub() {
              @Override
              public void onCallStateChanged(Core core, Call call, Call.State state, String message) {

                if (state == Call.State.End || state == Call.State.Released) {
                  finish();
                } else if (state == Call.State.Connected) {
                  gotoCalling();
                } else if (state == Call.State.IncomingReceived) {
                }
              }
            };
    RipplePulseLayout mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);
    mRipplePulseLayout.startRippleAnimation();
  }

  private void gotoCalling() {
    Intent intent = new Intent(this, TripRegisterActivity.class);
    // This flag is required to start an Activity from a Service context
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();

  }

  @Override
  protected void onResume() {
    super.onResume();
    MyApplication.currentActivity = this;
    try {
      Core core = LinphoneService.getCore();
      if (core != null) {
        core.addListener(mListener);
      }
      Call[] calls = core.getCalls();
      for (Call callList : calls){
        if (callList.getState() == Call.State.IncomingReceived){
          call = callList;
          Address address = callList.getRemoteAddress();
          txtCallerNum.setText(address.getUsername());
        }
      }
    }catch (Exception e){
      e.printStackTrace();
      AvaCrashReporter.send(e,"callIncomingActivity class, onResume method");
    }

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();

  }

  @Override
  protected void onPause() {
    Core core = LinphoneService.getCore();
    if (core != null) {
      core.removeListener(mListener);
    }
    super.onPause();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
