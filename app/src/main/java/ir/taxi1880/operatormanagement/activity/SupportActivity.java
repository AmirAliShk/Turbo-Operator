package ir.taxi1880.operatormanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SupportViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.Keys;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.fragment.SupportDriverTripsFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

public class SupportActivity extends AppCompatActivity {
    public static final String TAG = SupportActivity.class.getSimpleName();
    Unbinder unbinder;
    SupportViewPagerAdapter supportViewPagerAdapter;
    RipplePulseLayout mRipplePulseLayout;
    private Runnable mCallQualityUpdater = null;
    private int mDisplayedQuality = -1;
    public static boolean supportActivityIsRunning = false;
    String voipId = "0";
    Core core;
    Call call;
    int mistakeCountNew;
    int mistakeCountPending;
    DataBase dataBase;

    @BindView(R.id.vpSupport)
    ViewPager2 vpSupport;

    @BindView(R.id.tbLayout)
    TabLayout tbLayout;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.btnActivate)
    Button btnActivate;

    @BindView(R.id.btnDeActivate)
    Button btnDeActivate;

    @OnClick(R.id.btnActivate)
    void onActivePress() {
        KeyBoardHelper.hideKeyboard();
        new GeneralDialog()
                .title("هشدار")
                .cancelable(false)
                .message("مطمئنی میخوای وارد صف بشی؟")
                .firstButton("مطمئنم", new Runnable() {
                    @Override
                    public void run() {
                        setActivate(MyApplication.prefManager.getSipNumber());
//                MyApplication.Toast("activated",Toast.LENGTH_SHORT);
                    }
                })
                .secondButton("نیستم", null)
                .show();

    }

    @OnClick(R.id.btnDeActivate)
    void onDeActivePress() {
        KeyBoardHelper.hideKeyboard();
        new GeneralDialog()
                .title("هشدار")
                .cancelable(false)
                .message("مطمئنی میخوای خارج بشی؟")
                .firstButton("مطمئنم", () -> {
                    if (MyApplication.prefManager.isCallIncoming()) {
                        MyApplication.Toast(getString(R.string.exit), Toast.LENGTH_SHORT);
                    } else {
                        setDeActivate(MyApplication.prefManager.getSipNumber());
                    }
                })
                .secondButton("نیستم", null)
                .show();
    }

    @BindView(R.id.rlNewInComingCall)
    RelativeLayout rlNewInComingCall;

    @BindView(R.id.llActionBar)
    LinearLayout llActionBar;

    @BindView(R.id.txtCallerNum)
    TextView txtCallerNum;

    @BindView(R.id.imgOpenDriverSupport)
    ImageView imgOpenDriverSupport;

    @OnClick(R.id.imgOpenDriverSupport)
    void onPressOpenDriverSupport() {
        FragmentHelper.toFragment(MyApplication.currentActivity, new SupportDriverTripsFragment()).replace();
    }

    @BindView(R.id.imgCallQuality)
    ImageView imgCallQuality;

    @BindView(R.id.imgTestConnection)
    ImageView imgTestConnection;

    @OnClick(R.id.imgTestConnection)
    void onTestConnectionPress() {
        new CallDialog().show(new CallDialog.CallBack() {
            @Override
            public void onDismiss() {
            }

            @Override
            public void onCallReceived() {
            }

            @Override
            public void onCallTransferred() {
            }

            @Override
            public void onCallEnded() {
                if (imgTestConnection != null)
                    imgTestConnection.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }, false);
    }

    @OnClick(R.id.imgAccept)
    void onAcceptPress() {
        call = core.getCurrentCall();
        Call[] calls = core.getCalls();
        int i = calls.length;
        Log.i(TAG, "onRejectPress: " + i);
        if (call != null) {
            call.accept();
            Address address = call.getRemoteAddress();
            Bundle b = new Bundle();
            b.putString("number", address.getUsername());
            FragmentHelper.toFragment(MyApplication.currentActivity, new SupportDriverTripsFragment()).setArguments(b).replace();
//      if (getMobileNumber().isEmpty() && isTellValidable)
//        MyApplication.handler.postDelayed(() -> onPressDownload(), 400);
        } else if (calls.length > 0) {
            calls[0].accept();
        }


        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("passengerTell", txtCallerNum.getText().toString());
        clipboard.setPrimaryClip(clip);

    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        MyApplication.configureAccount();
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        supportViewPagerAdapter = new SupportViewPagerAdapter(this);
        vpSupport.setAdapter(supportViewPagerAdapter);
        vpSupport.setUserInputEnabled(false);

        dataBase = new DataBase(MyApplication.context);

        new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
            tab.setCustomView(supportViewPagerAdapter.getTabView(position, 0, dataBase.getMistakesCount()));
        }).attach();

        tbLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                supportViewPagerAdapter.setSelectView(tbLayout, tab.getPosition(), "select");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                supportViewPagerAdapter.setSelectView(tbLayout, tab.getPosition(), "unSelect");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        if (intent.getBooleanExtra("comeFromCallActivity", false)) {
            MyApplication.handler.postDelayed(() -> FragmentHelper.toFragment(MyApplication.currentActivity, new SupportDriverTripsFragment()).replace(), 400);
        }

        if (MyApplication.prefManager.getActivateStatus()) {
            btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        } else {
            btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        }
    }

    private void setActivate(int sipNumber) {

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.ACTIVATE_SUPPORT)
                .addParam("sipNumber", sipNumber)
                .listener(setActivate)
                .post();

    }

    RequestHelper.Callback setActivate = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        MyApplication.prefManager.setActivityStatus(2);
                        MyApplication.Toast("شما باموفقیت وارد صف شدید", Toast.LENGTH_SHORT);
                        if (btnActivate != null)
                            btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                        MyApplication.prefManager.setActivateStatus(true);
                        if (btnDeActivate != null) {
                            btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .firstButton("تلاش مجدد", () -> setActivate(MyApplication.prefManager.getSipNumber()))
                                .secondButton("بعدا امتحان میکنم", null)
                                .show();
                    }
                    LoadingDialog.dismiss();
                } catch (JSONException e) {
                    LoadingDialog.dismiss();
                    e.printStackTrace();
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismiss);
        }

    };

    private void setDeActivate(int sipNumber) {

        JSONObject params = new JSONObject();
        try {
            params.put("sipNumber", sipNumber);

            Log.i(TAG, "setDeActivate: " + params);

            LoadingDialog.makeCancelableLoader();
            RequestHelper.builder(EndPoints.DEACTIVATE_SUPPORT)
                    .addParam("sipNumber", sipNumber)
                    .listener(setDeActivate)
                    .post();
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, setDeActivate method");

        }
    }

    RequestHelper.Callback setDeActivate = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        MyApplication.prefManager.setActivityStatus(0);
                        MyApplication.Toast("شما باموفقیت از صف خارج شدید", Toast.LENGTH_SHORT);
                        MyApplication.prefManager.setActivateStatus(false);
                        if (btnActivate != null)
                            btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                        if (btnDeActivate != null) {
                            btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
                            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .firstButton("تلاش مجدد", () -> setDeActivate(MyApplication.prefManager.getSipNumber()))
                                .secondButton("بعدا امتحان میکنم", null)
                                .show();
                    }
                    LoadingDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadingDialog.dismiss();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismiss);

        }
    };

    private void showCallIncoming() {
        mRipplePulseLayout.startRippleAnimation();
        call = core.getCurrentCall();
        Address address = call.getRemoteAddress();
        txtCallerNum.setText(address.getUsername());
        rlNewInComingCall.setVisibility(View.VISIBLE);
        llActionBar.setVisibility(View.GONE);
    }

    private void showTitleBar() {
        mRipplePulseLayout.stopRippleAnimation();
        rlNewInComingCall.setVisibility(View.GONE);
        llActionBar.setVisibility(View.VISIBLE);
    }

    //receive userStatus from local broadcast
    BroadcastReceiver userStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean userStatus = intent.getBooleanExtra(Keys.KEY_USER_STATUS, false);
            if (!userStatus) {
                btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
                btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                MyApplication.prefManager.setActivateStatus(false);
            } else {
                btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                MyApplication.prefManager.setActivateStatus(true);
            }
        }
    };

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            SupportActivity.this.call = call;

            if (state == Call.State.IncomingReceived) {
                showCallIncoming();
            } else if (state == Call.State.Released) {
                if (imgTestConnection != null)
                    imgTestConnection.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            } else if (state == Call.State.Connected) {
                startCallQuality();
                if (imgTestConnection != null)
                    imgTestConnection.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                Address address = call.getRemoteAddress();
//                if (voipId.equals("0")) {  //TODO
//                    edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
//                }
                showTitleBar();
            } else if (state == Call.State.Error) {
                showTitleBar();
            } else if (state == Call.State.End) {
                if (imgCallQuality != null)
                    imgCallQuality.setVisibility(View.INVISIBLE);
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            }
        }
    };

    BroadcastReceiver counterReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mistakeCountNew = intent.getIntExtra(NEW_MISTAKE_COUNT, 0);
            if (vpSupport != null) {
                new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
                    tab.setCustomView(supportViewPagerAdapter.getTabView(position, mistakeCountNew, dataBase.getMistakesCount()));
                }).attach();
            }
        }
    };

    BroadcastReceiver counterReceiverPending = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mistakeCountPending = intent.getIntExtra(PENDING_MISTAKE_COUNT, 0);
            if (vpSupport != null) {
                new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
                    tab.setCustomView(supportViewPagerAdapter.getTabView(position, mistakeCountNew, mistakeCountPending));
                }).attach();
            }
        }
    };

    private void startCallQuality() {
        if (mCallQualityUpdater == null)
            LinphoneService.dispatchOnUIThreadAfter(
                    mCallQualityUpdater =
                            new Runnable() {
                                final Call mCurrentCall = LinphoneService.getCore().getCurrentCall();

                                public void run() {
                                    if (mCurrentCall == null) {
                                        mCallQualityUpdater = null;
                                        return;
                                    }
                                    float newQuality = mCurrentCall.getCurrentQuality();
                                    updateQualityOfSignalIcon(newQuality);

                                    if (MyApplication.prefManager.getConnectedCall())
                                        LinphoneService.dispatchOnUIThreadAfter(this, 1000);
                                }
                            },
                    1000);
    }

    private void updateQualityOfSignalIcon(float quality) {
        Log.d(TAG, "updateQualityOfSignalIcon: " + quality);
        int iQuality = (int) quality;

        int imageRes = R.drawable.ic_quality_0;

        if (iQuality == mDisplayedQuality) return;
        if (quality >= 4) { // Good Quality
            imageRes = R.drawable.ic_quality_4;
        } else if (quality >= 3) {// Average quality
            imageRes = (R.drawable.ic_quality_3);
        } else if (quality >= 2) { // Low quality
            imageRes = (R.drawable.ic_quality_2);
        } else if (quality >= 1) { // Very low quality
            imageRes = (R.drawable.ic_quality_1);
        }
        if (imgCallQuality != null) {
            imgCallQuality.setVisibility(View.VISIBLE);
            imgCallQuality.setImageResource(imageRes);
        }
        mDisplayedQuality = iQuality;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);
        showTitleBar();
        if (MyApplication.prefManager.getConnectedCall()) {
            startCallQuality();
            imgTestConnection.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);

            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call != null && call.getState() == Call.State.StreamsRunning) {
//                    if (voipId.equals("0")) {
//                        Address address = call.getRemoteAddress();
//                        edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
//                    }
                }
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.prefManager.setAppRun(false);
        if (userStatusReceiver != null) {
            unregisterReceiver(userStatusReceiver);
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).unregisterReceiver(userStatusReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.prefManager.setAppRun(false);
        supportActivityIsRunning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.currentActivity = this;
        supportActivityIsRunning = true;
        MyApplication.prefManager.setAppRun(true);
        core = LinphoneService.getCore();
        core.addListener(mCoreListener);

        registerReceiver(userStatusReceiver, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((userStatusReceiver), new IntentFilter(Keys.KEY_REFRESH_USER_STATUS));

        registerReceiver(counterReceiverNew, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((counterReceiverNew), new IntentFilter(KEY_NEW_MISTAKE_COUNT));

        registerReceiver(counterReceiverPending, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((counterReceiverPending), new IntentFilter(KEY_PENDING_MISTAKE_COUNT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.prefManager.setAppRun(false);
        unbinder.unbind();
        core.removeListener(mCoreListener);
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
        core = null;
    }

    @Override
    public void onBackPressed() {
        KeyBoardHelper.hideKeyboard();
        if (getFragmentManager().getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(MyApplication.context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}