package ir.taxi1880.operatormanagement.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.util.Timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.SoundHelper;
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
    Boolean isReceivedCall = false;

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
                        else if (state == Call.State.IncomingReceived){
                            isReceivedCall = true;
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

    public void createNotification() {
        String CALLCHANNEL = "callChannel";

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);

        Intent intent = new Intent(MyApplication.context, CallIncomingActivity.class);
        collapsedView.setOnClickPendingIntent(R.id.linearNotif, PendingIntent.getActivity(MyApplication.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        expandedView.setOnClickPendingIntent(R.id.btnBackToCall, PendingIntent.getActivity(MyApplication.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.context.getApplicationContext(), "notify_timer")
                .setSmallIcon(R.drawable.return_call)
                .setCustomContentView(collapsedView)
                .setAutoCancel(true)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        mNotificationManager = (NotificationManager) MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CALLCHANNEL, "call channel", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(CALLCHANNEL);
        }
        mNotificationManager.notify(0, mBuilder.build());

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Core core = LinphoneService.getCore();
            if (core != null) {
                core.addListener(mListener);
            }
            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call.getState() == Call.State.Connected) {
                    Address address = call.getRemoteAddress();
                    txtCallerNum.setText(address.getUsername());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "callIncomingActivity");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        SoundHelper.stop();

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
        if (isReceivedCall)
            createNotification();

        super.onBackPressed();
    }
}
