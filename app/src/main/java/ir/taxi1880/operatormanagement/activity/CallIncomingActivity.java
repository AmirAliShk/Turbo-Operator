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
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

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
    }

    Unbinder unbinder;
    Call call;

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
        Core core = LinphoneService.getCore();
        call = core.getCurrentCall();
        Address address = call.getRemoteAddress();
        MyApplication.prefManager.setLastCall(address.getUsername());
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


}
