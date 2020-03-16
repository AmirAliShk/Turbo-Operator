package ir.taxi1880.operatormanagement.activity;

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
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.tools.Log;

import java.util.Timer;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.SoundHelper;
import ir.taxi1880.operatormanagement.services.LinphoneService;
import ir.taxi1880.operatormanagement.services.LinphoneUtils;

public class CallIncomingActivity extends AppCompatActivity {
  public static final String TAG = CallIncomingActivity.class.getSimpleName();
  private CoreListenerStub mListener;
  Timer timer;
  int timercount;

  @BindView(R.id.txtCallerNum)
  TextView txtCallerNum;

  @BindView(R.id.txtCallerName)
  TextView txtCallerName;

  @OnClick(R.id.imgAccept)
  void onAcceptPress() {
    call.accept();
//    txtTimer.setVisibility(View.VISIBLE);
//    startTimer();
  }

//  @BindView(R.id.txtTimer)
//  TextView txtTimer;

  @OnClick(R.id.imgReject)
  void onRejectPress() {
    call.terminate();
  }

  Unbinder unbinder;
  Call call;
  @Override
  protected void onResume() {
    Core core = LinphoneService.getCore();
    if (core != null) {
      core.addListener(mListener);
    }

    call =  core.getCurrentCall();
//    core.getIdentity();
//    Conference conference = call.getConference();
//     core.getConference().getId();
    Address address = call.getRemoteAddress();

    txtCallerNum.setText(address.getUsername());
    MyApplication.prefManager.setParticipant(address.getUsername());
    super.onResume();

  }

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

    unbinder = ButterKnife.bind(this);
    mListener =
            new CoreListenerStub() {
              @Override
              public void onCallStateChanged(Core core, Call call, Call.State state, String message) {


                if (state == Call.State.End || state == Call.State.Released) {
//                  stopTimer();
                  finish();
                } else if (state == Call.State.Connected) {
                  gotoCalling();
                }
              }
            };
    RipplePulseLayout mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);
    mRipplePulseLayout.startRippleAnimation();

    SoundHelper.ringing(R.raw.ring);

  }

  private void gotoCalling() {
    Intent intent = new Intent(this, TripRegisterActivity.class);
    // This flag is required to start an Activity from a Service context
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    SoundHelper.stop();

  }

//  void startTimer() {
//    timercount = 0;
//    timer = new Timer();
//    timer.schedule(new TimerTask() {
//      @Override
//      public void run() {
//        runOnUiThread(new Runnable() {
//          @Override
//          public void run() {
//            timercount++;
////            Call call = LinphoneService.getCore().getCurrentCall();
////            int min = call.getDuration() / 60;
////            int sec = call.getDuration() % 60;
////            String time = String.format("%02d:%02d", min, sec);
////            txtTimer.setText(time);
//          }
//        });
//      }
//    }, 0, 1000);
//  }

//  public void stopTimer() {
//    if (timer == null) return;
//    timer.cancel();
//    timer = null;
//  }


  @Override
  protected void onPause() {
    Core core = LinphoneService.getCore();
    if (core != null) {
      core.removeListener(mListener);
    }
    super.onPause();
  }


  public boolean acceptCall(Call call) {

    android.util.Log.i("LOG", "acceptCall ");
    if (call == null) return false;

    Core core = LinphoneService.getCore();
    CallParams params = core.createCallParams(call);

    boolean isLowBandwidthConnection =
            !LinphoneUtils.isHighBandwidthConnection(MyApplication.context);

    if (params != null) {
      params.enableLowBandwidth(isLowBandwidthConnection);
    } else {
      Log.e("[Call Manager] Could not create call params for call");
      return false;
    }

    call.acceptWithParams(params);
    return true;
  }


}
