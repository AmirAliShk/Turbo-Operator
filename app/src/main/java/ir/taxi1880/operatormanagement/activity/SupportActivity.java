package ir.taxi1880.operatormanagement.activity;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.MyApplication.context;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SupportViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.Keys;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.databinding.ActivitySupportBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.fragment.DriverTripSupportFragment;
import ir.taxi1880.operatormanagement.fragment.PendingMistakesFragmentK;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.ThemeHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class SupportActivity extends AppCompatActivity {
    public static final String TAG = SupportActivity.class.getSimpleName();
    ActivitySupportBinding binding;
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
    LocalBroadcastManager broadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (MyApplication.prefManager.isDarkMode()) {
                window.setNavigationBarColor(getResources().getColor(R.color.dark_navigation_bar));
                window.setStatusBarColor(getResources().getColor(R.color.dark_action_bar));
            } else {
                window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        binding = ActivitySupportBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());
        MyApplication.configureAccount();
        getMistakeReason();
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        supportViewPagerAdapter = new SupportViewPagerAdapter(this);
        binding.vpSupport.setAdapter(supportViewPagerAdapter);
        binding.vpSupport.setUserInputEnabled(false);

        dataBase = new DataBase(context);
        broadcaster = LocalBroadcastManager.getInstance(context);

        new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(supportViewPagerAdapter.getTabView(position, 0, dataBase.getMistakesCount()))).attach();

        binding.tbLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                supportViewPagerAdapter.setSelectView(binding.tbLayout, tab.getPosition(), "select");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                supportViewPagerAdapter.setSelectView(binding.tbLayout, tab.getPosition(), "unSelect");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        if (intent.getBooleanExtra("comeFromCallActivity", false)) {
            MyApplication.handler.postDelayed(() -> FragmentHelper.toFragment(MyApplication.currentActivity, new DriverTripSupportFragment()).replace(), 400);
        }

        if (MyApplication.prefManager.isActiveInSupport()) {
            binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        } else {
            binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        }

        binding.imgReject.setOnClickListener(view -> {
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
        });

        binding.imgAccept.setOnClickListener(view -> {
            call = core.getCurrentCall();
            Call[] calls = core.getCalls();
            int i = calls.length;
            Log.i(TAG, "onRejectPress: " + i);
            if (call != null) {
                call.accept();
                Address address = call.getRemoteAddress();
                Bundle b = new Bundle();
                b.putString("number", address.getUsername());
                FragmentHelper.toFragment(MyApplication.currentActivity, new DriverTripSupportFragment()).setArguments(b).replace();
//      if (getMobileNumber().isEmpty() && isTellValidable)
//        MyApplication.handler.postDelayed(() -> onPressDownload(), 400);
            } else if (calls.length > 0) {
                calls[0].accept();
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("passengerTell", binding.txtCallerNum.getText().toString());
            clipboard.setPrimaryClip(clip);
        });

        binding.imgOpenDriverSupport.setOnClickListener(view -> {
            new PendingMistakesFragmentK().pauseVoice();
            FragmentHelper.toFragment(MyApplication.currentActivity, new DriverTripSupportFragment()).replace();
        });

        binding.btnDeActivate.setOnClickListener(view -> {
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
        });

        binding.btnActivate.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            new GeneralDialog()
                    .title("هشدار")
                    .cancelable(false)
                    .message("مطمئنی میخوای وارد صف بشی؟")
                    .firstButton("مطمئنم", () -> {
                        setActivate(MyApplication.prefManager.getSipNumber());
//                MyApplication.Toast("activated",Toast.LENGTH_SHORT);
                    })
                    .secondButton("نیستم", null)
                    .show();
        });

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());
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
                        MyApplication.prefManager.activeInSupport(true);
                        MyApplication.prefManager.setActivityStatus(2);
                        MyApplication.Toast("شما باموفقیت وارد صف شدید", Toast.LENGTH_SHORT);
                        if (binding.btnActivate != null)
                            binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                        MyApplication.prefManager.activeInSupport(true);
                        if (binding.btnDeActivate != null) {
                            binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                            binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
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
                } catch (Exception e) {
                    LoadingDialog.dismiss();
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, setActivate method ");
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
            AvaCrashReporter.send(e, TAG + " class, setDeActivate method");
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
                        Intent broadcastIntent2 = new Intent(KEY_NEW_MISTAKE_COUNT);
                        broadcastIntent2.putExtra(NEW_MISTAKE_COUNT, 0);
                        broadcaster.sendBroadcast(broadcastIntent2);

                        MyApplication.prefManager.activeInSupport(false);
                        MyApplication.prefManager.setActivityStatus(0);
                        MyApplication.Toast("شما باموفقیت از صف خارج شدید", Toast.LENGTH_SHORT);
                        MyApplication.prefManager.activeInSupport(false);
                        if (binding.btnActivate != null)
                            binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                        if (binding.btnDeActivate != null) {
                            binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
                            binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
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
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDialog.dismiss();
                    AvaCrashReporter.send(e, TAG + " class, setDeActivate method ");
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
        binding.txtCallerNum.setText(address.getUsername());
        binding.rlNewInComingCall.setVisibility(View.VISIBLE);
        binding.llActionBar.setVisibility(View.GONE);
    }

    private void showTitleBar() {
        mRipplePulseLayout.stopRippleAnimation();
        binding.rlNewInComingCall.setVisibility(View.GONE);
        binding.llActionBar.setVisibility(View.VISIBLE);
    }

    //receive userStatus from local broadcast
    BroadcastReceiver userStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean userStatus = intent.getBooleanExtra(Keys.KEY_USER_STATUS, false);
            if (!userStatus) {
                binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
                binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                MyApplication.prefManager.activeInSupport(false);
            } else {
                binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
                MyApplication.prefManager.activeInSupport(true);
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
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            } else if (state == Call.State.Connected) {
                Address address = call.getRemoteAddress();
                showTitleBar();
            } else if (state == Call.State.Error) {
                showTitleBar();
            } else if (state == Call.State.End) {
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
            if (binding.vpSupport != null) {
                new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(supportViewPagerAdapter.getTabView(position, mistakeCountNew, dataBase.getMistakesCount()))).attach();
            }
        }
    };

    BroadcastReceiver counterReceiverPending = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mistakeCountPending = intent.getIntExtra(PENDING_MISTAKE_COUNT, 0);
            if (binding.vpSupport != null) {
                new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(supportViewPagerAdapter.getTabView(position, mistakeCountNew, mistakeCountPending))).attach();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);
        showTitleBar();
        if (MyApplication.prefManager.getConnectedCall()) {
//            startCallQuality();
            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call != null && call.getState() == Call.State.StreamsRunning) {
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
            Intent intent = new Intent(MyApplication.currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void getMistakeReason() {
        RequestHelper.builder(EndPoints.GET_REASON_OPERATOR_MISTAKE)
                .listener(getMistakeReasonsListener)
                .get();
    }

    RequestHelper.Callback getMistakeReasonsListener = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject rawContent = new JSONObject(args[0].toString());
                    boolean status = rawContent.getBoolean("success");
                    String message = rawContent.getString("message");

                    if (status) {
                        String JArr = rawContent.getJSONArray("data").toString();
                        MyApplication.prefManager.setMistakeReason(JArr);
                        Log.i("TAF", MyApplication.prefManager.getMistakeReason());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, getMistakeReasonsListener method ");
                }
            });
        }
    };
}