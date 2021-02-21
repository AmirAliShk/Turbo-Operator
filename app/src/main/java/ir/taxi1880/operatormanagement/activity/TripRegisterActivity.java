package ir.taxi1880.operatormanagement.activity;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.Keys;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.MultiRowsRadioGroup;
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.CityDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.TripOptionDialog;
import ir.taxi1880.operatormanagement.fragment.TripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CallModel;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class TripRegisterActivity extends AppCompatActivity {

    public static final String TAG = TripRegisterActivity.class.getSimpleName();
    Unbinder unbinder;
    private String cityName = "";
    private String cityLatinName = "";
    private int cityCode;
    private String normalDescription = " ";
    private int originStation = 0;
    private int addressLength = 0;
    private String stationName = " ";// It must have a value otherwise it will get an error of 422
    private int serviceType;
    private int serviceCount;
    private boolean isEnableView = false;
    private boolean isTellValidable = false; // it means the entered number is a telephone number(5133710000) not mobile number
    RipplePulseLayout mRipplePulseLayout;
    ArrayList<CityModel> cityModels;
    Core core;
    Call call;
    byte carClass = 0;
    public static boolean isRunning = false;
    byte traffic = 0;
    byte defaultClass = 0;
    String queue = "0";
    String voipId = "0";
    String permanentDesc = "";
    private String[] countService = new String[6];
    private Runnable mCallQualityUpdater = null;
    private int mDisplayedQuality = -1;
    int addressChangeCounter = 0; // this variable count the last edition of edtAddress

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.spCity)
    Spinner spCity;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtNewPassenger)
    TextView txtNewPassenger;

    @BindView(R.id.txtLockPassenger)
    TextView txtLockPassenger;

    @BindView(R.id.spServiceCount)
    Spinner spServiceCount;

    @BindView(R.id.imgCallQuality)
    ImageView imgCallQuality;

    @BindView(R.id.spServiceType)
    Spinner spServiceType;

    @BindView(R.id.edtDiscount)
    EditText edtDiscount;

    @BindView(R.id.edtTell)
    EditText edtTell;

    @BindView(R.id.svTripRegister)
    ScrollView svTripRegister;

    @BindView(R.id.edtMobile)
    EditText edtMobile;

    @BindView(R.id.edtFamily)
    EditText edtFamily;

    @BindView(R.id.edtAddress)
    EditText edtAddress;

    @BindView(R.id.llTrafficBg)
    LinearLayout llTrafficBg;

    @BindView(R.id.llAlwaysBg)
    LinearLayout llAlwaysBg;

    @OnClick(R.id.llCity)
    void onPressllCity() {
        spCity.performClick();
    }

    @OnClick(R.id.llServiceType)
    void onPressllServiceType() {
        spServiceType.performClick();
    }

    @BindView(R.id.llServiceType)
    LinearLayout llServiceType;

    @OnClick(R.id.llServiceCount)
    void onPressllServiceCount() {
        spServiceCount.performClick();
    }

    @OnClick(R.id.llTell)
    void onPressllTell() {
        edtTell.requestFocus();
        KeyBoardHelper.showKeyboard(MyApplication.context);
    }

    @OnClick(R.id.llDiscount)
    void onPressllDiscount() {
        edtDiscount.requestFocus();
        KeyBoardHelper.showKeyboard(MyApplication.context);
    }

    @OnClick(R.id.llMobile)
    void onPressllMobile() {
        edtMobile.requestFocus();
        KeyBoardHelper.showKeyboard(MyApplication.context);
    }

    @OnClick(R.id.llFamily)
    void onPressllFamily() {
        edtFamily.requestFocus();
        KeyBoardHelper.showKeyboard(MyApplication.context);
    }

    @OnClick(R.id.llAddress)
    void onPressllAddress() {
        edtAddress.requestFocus();
        KeyBoardHelper.showKeyboard(MyApplication.context);
    }

    @OnClick(R.id.llTraffic)
    void onPressllTraffic() {
        chbTraffic.setChecked(!chbTraffic.isChecked());
    }

    @OnClick(R.id.llAlways)
    void onPressllAlways() {
        chbAlways.setChecked(!chbAlways.isChecked());
    }

    @BindView(R.id.chbTraffic)
    CheckBox chbTraffic;

    @BindView(R.id.chbAlways)
    CheckBox chbAlways;

    @BindView(R.id.rbUnknow)
    RadioButton rbUnknow;

    @BindView(R.id.rbTaxi)
    RadioButton rbTaxi;

    @BindView(R.id.rbPrivilage)
    RadioButton rbPrivilage;

    @BindView(R.id.rbEconomical)
    RadioButton rbEconomical;

    @BindView(R.id.rbFormality)
    RadioButton rbFormality;

    @BindView(R.id.rgCarClass)
    MultiRowsRadioGroup rgCarClass;

    @BindView(R.id.txtPassengerAddress)
    TextView txtPassengerAddress;

    @OnClick(R.id.llDescriptionDetail)
    void onPressLlDescriptionDetail() {
        new DescriptionDialog().show(new DescriptionDialog.Listener() {
            @Override
            public void description(String description) {
                normalDescription = description;
            }

            @Override
            public void fixedDescription(String fixedDescription) {
                txtDescription.setText(fixedDescription);
            }
        }, txtDescription.getText().toString(), normalDescription);
    }

    @OnClick(R.id.txtPassengerAddress)
    void txtPassengerAddress() {
        if (getTellNumber().isEmpty()) {
            MyApplication.Toast("ابتدا شماره تلفن را وارد کنید", Toast.LENGTH_SHORT);
            return;
        }
        KeyBoardHelper.hideKeyboard();
        getPassengerAddress(StringHelper.toEnglishDigits(getTellNumber()));
    }

    private String getTellNumber() {
        if (edtTell == null)
            return "";
        String txtTell = edtTell.getText().toString();
        if (txtTell == null)
            return "";
        else
            return txtTell;
    }

    private String getMobileNumber() {
        if (edtMobile == null)
            return "";
        String mobileNo = edtMobile.getText().toString();
        if (mobileNo == null)
            return "";
        else
            return mobileNo;
    }

    @OnClick(R.id.imgAccept)
    void onAcceptPress() {
        call = core.getCurrentCall();
        Call[] calls = core.getCalls();
        int i = calls.length;
        Log.i(TAG, "onRejectPress: " + i);
        if (call != null) {
            call.accept();
//      if (getMobileNumber().isEmpty() && isTellValidable)
//        MyApplication.handler.postDelayed(() -> onPressDownload(), 400);
        } else if (calls.length > 0) {
            calls[0].accept();
        }
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

    @BindView(R.id.txtCallerNum)
    TextView txtCallerNum;

    @OnClick(R.id.btnSubmit)
    void onPressSubmit() {

        int addressPercent = addressLength * 50 / 100;
        if (addressChangeCounter >= addressPercent) {
            originStation = 0;
        }

        Log.i(TAG, "onPressSubmit: address length " + addressLength);
        Log.i(TAG, "onPressSubmit: address percent " + addressPercent);
        Log.i(TAG, "onPressSubmit: address change counter " + addressChangeCounter);
        Log.i(TAG, "onPressSubmit: originStation " + originStation);

        if (cityCode == -1) {
            MyApplication.Toast("شهر را وارد نمایید", Toast.LENGTH_SHORT);
            spCity.performClick();
            return;
        }
        if (getTellNumber().isEmpty()) {
            edtTell.setError("شماره تلفن را وارد کنید");
            edtTell.requestFocus();
            return;
        }
        if (getMobileNumber().isEmpty() && !isTellValidable) {
            edtMobile.setError("شماره همراه را وارد کنید");
            edtMobile.requestFocus();
            return;
        }
        if (edtFamily.getText().toString().isEmpty()) {
            edtFamily.setError(" نام مسافر را مشخص کنید");
            edtFamily.requestFocus();
            return;
        }
        if (edtAddress.getText().toString().isEmpty()) {
            edtAddress.setError("آدرس را مشخص کنید");
            edtAddress.requestFocus();
            return;
        }

        if (vfSubmit != null)
            vfSubmit.setDisplayedChild(1);

        callInsertService();

    }

    @OnClick(R.id.btnOptions)
    void onPressOptions() {
        KeyBoardHelper.hideKeyboard();
        new TripOptionDialog().show(new TripOptionDialog.Listener() {
            @Override
            public void onClose(boolean b) {
                if (b) {
                    clearData();
                }
            }
        }, getMobileNumber(), edtFamily.getText().toString(), cityCode);
    }

    @BindView(R.id.vfPassengerAddress)
    ViewFlipper vfPassengerAddress;

    @BindView(R.id.vfPassengerInfo)
    ViewFlipper vfPassengerInfo;

    @OnClick(R.id.llClear)
    void onClear() {
        new GeneralDialog()
                .title("هشدار")
                .message("آیا از پاک کردن اطلاعات اطمینان دارید؟")
                .firstButton("بله", new Runnable() {
                    @Override
                    public void run() {
                        clearData();
                    }
                }).secondButton("خیر", null)
                .show();
    }

    @OnClick(R.id.imgPassengerInfo)
    void onPressDownload() {
        if (getTellNumber().isEmpty() && edtTell != null) {
            edtTell.setError("شماره تلفن را وارد نمایید");
            edtTell.requestFocus();
            return;
        }

//    if (getMobileNumber().isEmpty() && !isTellValidable && edtMobile != null) {
//      edtMobile.setError("شماره تلفن همراه را وارد نمایید");
//      edtMobile.requestFocus();
//      return;
//    }

        String mobile = isTellValidable && getMobileNumber().isEmpty() ? "0" : getMobileNumber();

        getPassengerInfo(StringHelper.toEnglishDigits(getTellNumber()), StringHelper.toEnglishDigits(mobile), StringHelper.toEnglishDigits(queue));
    }

    @OnClick(R.id.llEndCall)
    void onPressEndCall() {

        KeyBoardHelper.hideKeyboard();
        core.removeListener(mCoreListener);
        new CallDialog().show(new CallDialog.CallBack() {
            @Override
            public void onDismiss() {
                if (mCoreListener != null && core != null) {
                    core.addListener(mCoreListener);
                }
            }

            @Override
            public void onCallReceived() {
                showCallIncoming();
            }

            @Override
            public void onCallTransferred() {
                MyApplication.handler.postDelayed(() -> clearData(), 100);
            }

            @Override
            public void onCallEnded() {

            }

        }, false);

//    Call call = LinphoneService.getCore().getCurrentCall();
//    call.terminate();
    }

    @BindView(R.id.rlNewInComingCall)
    RelativeLayout rlNewInComingCall;

    @BindView(R.id.rlActionBar)
    RelativeLayout rlActionBar;

    @BindView(R.id.llDescriptionDetail)
    LinearLayout llDescriptionDetail;

    @BindView(R.id.llTraffic)
    LinearLayout llTraffic;

    @BindView(R.id.llAlways)
    LinearLayout llAlways;

    @BindView(R.id.llServiceCount)
    LinearLayout llServiceCount;

    @BindView(R.id.llFamily)
    LinearLayout llFamily;

    @BindView(R.id.llDiscount)
    LinearLayout llDiscount;

    @BindView(R.id.llAddress)
    LinearLayout llAddress;

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

    @OnClick(R.id.clearAddress)
    void onCLearAddress() {
        edtAddress.getText().clear();
        originStation = 0;
        addressLength = 0;
        addressChangeCounter = 0;
    }

    @OnClick(R.id.btnDeActivate)
    void onDeActivePress() {
        KeyBoardHelper.hideKeyboard();
        new GeneralDialog()
                .title("هشدار")
                .cancelable(false)
                .message("مطمئنی میخوای خارج بشی؟")
                .firstButton("مطمئنم", new Runnable() {
                    @Override
                    public void run() {
                        if (MyApplication.prefManager.isCallIncoming()) {
                            MyApplication.Toast(getString(R.string.exit), Toast.LENGTH_SHORT);
                        } else {
                            setDeActivate( MyApplication.prefManager.getSipNumber());
                        }
                    }
                })
                .secondButton("نیستم", null)
                .show();
    }

    @BindView(R.id.vfSubmit)
    ViewFlipper vfSubmit;

    @OnFocusChange(R.id.edtTell)
    void onChangeFocus(boolean v) {
        if (v) {
            if (getTellNumber().trim().isEmpty()) {
                try {
                    Core core = LinphoneService.getCore();
                    Call[] calls = core.getCalls();
                    for (Call callList : calls) {
                        if (callList.getState() == Call.State.Connected) {
                            call = core.getCurrentCall();
                            Address address = call.getRemoteAddress();
                            edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "callIncomingActivity");
                }
            }
        }
    }

    @BindView(R.id.imgEndCall)
    ImageView imgEndCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        setContentView(R.layout.activity_trip_register);
        view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        if (MyApplication.prefManager.getActivateStatus()) {
            btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        }

        disableViews();

        MyApplication.handler.postDelayed(() -> {
            initCitySpinner();
            initServiceTypeSpinner();
            initServiceCountSpinner();
        }, 200);

        edtTell.requestFocus();

        edtTell.addTextChangedListener(edtTellTextWather);

        edtMobile.addTextChangedListener(edtMobileTW);

        edtAddress.addTextChangedListener(addressTW);

        rgCarClass.setOnCheckedChangeListener((group, i) -> {
            chbAlways.setChecked(false);
        });

        MyApplication.handler.postDelayed(() -> KeyBoardHelper.showKeyboard(MyApplication.context), 300);

        setCursorEnd(getWindow().getDecorView().getRootView());

    }

    public static void setCursorEnd(final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setCursorEnd(child);
                }
            } else if (v instanceof EditText) {
                EditText e = (EditText) v;
                e.setOnFocusChangeListener((view, b) -> {
                    if (b)
                        MyApplication.handler.postDelayed(() -> e.setSelection(e.getText().length()), 200);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, setCursorEnd method");
            // ignore
        }
    }

    TextWatcher edtMobileTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (PhoneNumberValidation.havePrefix(editable.toString()))
                edtMobile.setText(PhoneNumberValidation.removePrefix(editable.toString()));

        }
    };

    TextWatcher edtTellTextWather = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (charSequence.toString().isEmpty()) {
//        spCity.setSelection(0);
            isEnableView = false;
            disableViews();
            spCity.setSelection(0);
            initServiceCountSpinner();
            initServiceTypeSpinner();
//        }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (PhoneNumberValidation.havePrefix(editable.toString()))
                edtTell.setText(PhoneNumberValidation.removePrefix(editable.toString()));

            if (PhoneNumberValidation.isValid(editable.toString())) {
                isTellValidable = false;
                edtMobile.setText(editable.toString());
            } else {
//          clearData();
//          edtMobile.setText("");
                isTellValidable = true;
                edtFamily.setText("");
                addressChangeCounter = 0;
                edtAddress.setText("");
                txtDescription.setText("");
                rgCarClass.clearCheck();
                txtLockPassenger.setVisibility(View.GONE);
                txtNewPassenger.setVisibility(View.GONE);
                rbUnknow.setChecked(true);
                chbAlways.setChecked(false);
                edtTell.setNextFocusDownId(R.id.edtMobile);
                edtMobile.setNextFocusDownId(R.id.edtMobile);
            }
        }
    };

    TextWatcher addressTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            addressChangeCounter = addressChangeCounter + 1;
            Log.i(TAG, "onTextChanged: counter " + addressChangeCounter);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                originStation = 0;
                addressLength = 0;
                edtAddress.getText().clear();
            }
        }
    };

    private void initServiceCountSpinner() {
        try {
            ArrayList<String> countServices = new ArrayList<>();
            for (int i = 1; i < countService.length; i++) {
                countService[i] = i + "";
                countServices.add(countService[i]);
            }

            if (spServiceCount == null)
                return;

            if (isEnableView) {
                spServiceCount.setEnabled(true);
            } else {
                spServiceCount.setEnabled(false);
            }
            spServiceCount.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, countServices));
            spServiceCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    serviceCount = Integer.parseInt(spServiceCount.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, initServiceCountSpinner method");
        }

    }

    private void initServiceTypeSpinner() {
        ArrayList<TypeServiceModel> typeServiceModels = new ArrayList<>();
        ArrayList<String> serviceList = new ArrayList<String>();
        try {
            JSONArray serviceArr = new JSONArray(MyApplication.prefManager.getServiceType());
            for (int i = 0; i < serviceArr.length(); i++) {
                JSONObject serviceObj = serviceArr.getJSONObject(i);
                TypeServiceModel typeServiceModel = new TypeServiceModel();
                typeServiceModel.setName(serviceObj.getString("name"));
                typeServiceModel.setId(serviceObj.getInt("id"));
                typeServiceModels.add(typeServiceModel);
                serviceList.add(serviceObj.getString("name"));
            }
            if (spServiceType == null)
                return;

            if (isEnableView) {
                spServiceType.setEnabled(true);
            } else {
                spServiceType.setEnabled(false);
            }
            spServiceType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

            spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    serviceType = typeServiceModels.get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, initServiceTypeSpinner method");
        }
    }

    private void initCitySpinner() {
        cityModels = new ArrayList<>();
        ArrayList<String> cityList = new ArrayList<String>();
        try {
            JSONArray cityArr = new JSONArray(MyApplication.prefManager.getCity());
            cityList.add(0, "انتخاب نشده");
            for (int i = 0; i < cityArr.length(); i++) {
                JSONObject cityObj = cityArr.getJSONObject(i);
                CityModel cityModel = new CityModel();
                cityModel.setCity(cityObj.getString("cityname"));
                cityModel.setId(cityObj.getInt("cityid"));
                cityModel.setCityLatin(cityObj.getString("latinName"));
                cityModels.add(cityModel);
                cityList.add(i + 1, cityObj.getString("cityname"));
            }
            if (spCity == null) return;
            spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        cityName = null;
                        cityLatinName = null;
                        cityCode = -1;
                        return;
                    }
                    cityName = cityModels.get(position - 1).getCity();
                    cityLatinName = cityModels.get(position - 1).getCityLatin();
                    cityCode = cityModels.get(position - 1).getId();
                    Log.i(TAG, "onItemSelected: " + cityName);
                    Log.i(TAG, "onItemSelected: " + cityLatinName);
                    Log.i(TAG, "onItemSelected: " + cityCode);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, initCitySpinner method");
        }
    }

    ArrayList<PassengerAddressModel> passengerAddressModels;

    private void getPassengerInfo(String phoneNumber, String mobile, String queue) {
        if (vfPassengerInfo != null)
            vfPassengerInfo.setDisplayedChild(1);

        RequestHelper.builder(EndPoints.PASSENGER_INFO)
                .addPath(MyApplication.prefManager.getCustomerSupport() + "")
                .addPath(StringHelper.extractTheNumber(phoneNumber))
                .addPath(StringHelper.extractTheNumber(mobile))
                .addPath(queue)
                .connectionTimeout(10)
                .readTimeout(10)
                .writeTimeout(10)
                .listener(getPassengerInfo)
                .get();

    }

    RequestHelper.Callback getPassengerInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (queue.trim().equals("1817")) {
                        MyApplication.handler.postDelayed(() -> {
                            if (spServiceType != null)
                                spServiceType.setSelection(2, true);
                        }, 500);
                    }

                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    JSONObject dataObj = obj.getJSONObject("data");
                    JSONObject statusObj = dataObj.getJSONObject("status");
                    int status = statusObj.getInt("status");
                    String descriptionStatus = statusObj.getString("descriptionStatus");
                    String tripState = statusObj.getString("tripState");
                    int callTimeInterval = statusObj.getInt("callTimeInterval");

                    JSONObject passengerInfoObj = dataObj.getJSONObject("passengerInfo");
                    int callerCode = passengerInfoObj.getInt("callerCode");
                    String address = passengerInfoObj.getString("address");
                    String name = passengerInfoObj.getString("name");
                    int staion = passengerInfoObj.getInt("staion");
                    permanentDesc = passengerInfoObj.getString("description");
                    String discountCode = passengerInfoObj.getString("discountCode");
                    int discountId = passengerInfoObj.getInt("discountId");
                    int carType = passengerInfoObj.getInt("carType");
                    int cityCode = passengerInfoObj.getInt("cityCode");

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("passengerTell", getTellNumber());
                    clipboard.setPrimaryClip(clip);
                    MyApplication.prefManager.setLastCallerId(getTellNumber().startsWith("51") ? getTellNumber().substring(2) : getTellNumber());

                    if (success) {
                        if (status == 2) {
                            String msg = " مسافر " + callTimeInterval + " دقیقه پیش سفری درخواست داده است " + "\n" + " وضعیت سفر : " + tripState;
                            if (vfPassengerInfo != null)
                                vfPassengerInfo.setDisplayedChild(0);
                            new GeneralDialog()
                                    .message(msg)
                                    .cancelable(false)
                                    .firstButton("بستن", null)
                                    .secondButton("پشتیبانی", () -> {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("tellNumber", getTellNumber());
                                        FragmentHelper.toFragment(MyApplication.currentActivity, new TripSupportFragment()).setArguments(bundle).replace();
                                    })
                                    .show();
                        }
                        if (edtTell == null)
                            return;
                        edtTell.setNextFocusDownId(R.id.edtFamily);
                        isEnableView = true;
                        initServiceCountSpinner();
                        initServiceTypeSpinner();
                        enableViews();
                        for (int i = 0; i < cityModels.size(); i++) {
                            if (cityModels.get(i).getId() == cityCode)
                                spCity.setSelection(i + 1, true);
                        }

                        if (cityCode == 0) {
                            KeyBoardHelper.hideKeyboard();
                            new CityDialog().show(position -> {
                                if (spCity != null)
                                    spCity.setSelection(position + 1);
                            });
                        }
                        if (callerCode == 0) {
                            if (txtNewPassenger != null)
                                txtNewPassenger.setVisibility(View.VISIBLE);
                            if (txtLockPassenger != null)
                                txtLockPassenger.setVisibility(View.GONE);
                            if (edtTell != null)
                                edtFamily.requestFocus();
                        } else {
                            switch (status) {
                                case 0:
                                    if (txtNewPassenger != null)
                                        txtNewPassenger.setVisibility(View.GONE);
                                    if (txtLockPassenger != null)
                                        txtLockPassenger.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    if (txtNewPassenger != null)
                                        txtNewPassenger.setVisibility(View.GONE);
                                    if (txtLockPassenger != null)
                                        txtLockPassenger.setVisibility(View.VISIBLE);
                                    break;
                            }
                            if (edtFamily != null)
                                edtFamily.setText(name);
                            if (edtAddress != null) {
                                edtAddress.setText(address);
                                addressLength = address.length();
                                addressChangeCounter = 0;
                            }
                            if (txtDescription != null)
                                txtDescription.setText(permanentDesc + "");
                            if (rgCarClass != null)
                                rgCarClass.clearCheck();
                            if (edtDiscount != null)
                                edtDiscount.setText(discountCode);
                            if (rgCarClass != null) {
                                switch (carType) {
                                    case 0:
                                        rbUnknow.setChecked(true);
                                        break;
                                    case 1:
                                        rbEconomical.setChecked(true);
                                        chbAlways.setChecked(true);
                                        break;
                                    case 2:
                                        rbPrivilage.setChecked(true);
                                        chbAlways.setChecked(true);
                                        break;
                                    case 3:
                                        rbFormality.setChecked(true);
                                        chbAlways.setChecked(true);
                                        break;
                                    case 4:
                                        rbTaxi.setChecked(true);
                                        chbAlways.setChecked(true);
                                        break;
                                }
                            }

                        }
                        originStation = staion;
                    }

                    if (vfPassengerInfo == null)
                        return;
                    MyApplication.handler.postDelayed(() -> {
                        if (vfPassengerInfo != null)
                            vfPassengerInfo.setDisplayedChild(0);
                    }, 500);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (vfPassengerInfo != null)
                        vfPassengerInfo.setDisplayedChild(0);
                    AvaCrashReporter.send(e, "TripRegisterActivity class, getPassengerInfo onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfPassengerInfo != null)
                    vfPassengerInfo.setDisplayedChild(0);
            });
        }
    };

    private void getPassengerAddress(String phoneNumber) {
        if (vfPassengerAddress != null)
            vfPassengerAddress.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.PASSENGER_ADDRESS)
                .addPath(phoneNumber)
                .listener(getPassengerAddress)
                .get();

    }

    RequestHelper.Callback getPassengerAddress = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "onResponse: " + args[0].toString());
                        passengerAddressModels = new ArrayList<>();
                        JSONObject obj = new JSONObject(args[0].toString());
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        if (success) {
                            JSONArray dataArr = obj.getJSONArray("data");
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dataObj = dataArr.getJSONObject(i);
                                PassengerAddressModel addressModel = new PassengerAddressModel();
                                addressModel.setPhoneNumber(dataObj.getString("phoneNumber"));
                                addressModel.setMobile(dataObj.getString("mobile"));
                                addressModel.setAddress(dataObj.getString("address"));
                                addressModel.setStation(dataObj.getInt("station"));
                                addressModel.setStatus(dataObj.getInt("status"));
                                passengerAddressModels.add(addressModel);
                            }
                            if (passengerAddressModels.size() == 0) {
                                MyApplication.Toast("آدرسی موجود نیست", Toast.LENGTH_SHORT);
                            } else {
                                new AddressListDialog().show((address, stationCode) -> {
                                    if (edtAddress != null) {
                                        edtAddress.setText(address);
                                        addressLength = address.length();
                                        addressChangeCounter = 0;
                                    }
                                    originStation = stationCode;
                                    Log.i(TAG, "run: " + originStation);

                                }, passengerAddressModels);
                            }
                        } else {
                            new GeneralDialog()
                                    .title("هشدار")
                                    .message(message)
                                    .secondButton("باشه", null)
                                    .cancelable(false)
                                    .show();
                        }

                        MyApplication.handler.postDelayed(() -> {
                            if (vfPassengerAddress != null)
                                vfPassengerAddress.setDisplayedChild(0);
                        }, 500);

                    } catch (JSONException e) {
                        MyApplication.handler.postDelayed(() -> {
                            if (vfPassengerAddress != null)
                                vfPassengerAddress.setDisplayedChild(0);
                        }, 500);
                        e.printStackTrace();
                        AvaCrashReporter.send(e, "TripRegisterActivity class, getPassengerAddress onResponse method");
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    MyApplication.handler.postDelayed(() -> {
                        if (vfPassengerAddress != null)
                            vfPassengerAddress.setDisplayedChild(0);
                    }, 500);
                }
            });
        }
    };

    private void callInsertService() {
        String mobile = isTellValidable && getMobileNumber().isEmpty() ? "0" : getMobileNumber();
        String tell = getTellNumber();
        String name = edtFamily.getText().toString();
        String address = edtAddress.getText().toString();
        String fixedComment = txtDescription.getText().toString();

        if (chbTraffic.isChecked())
            traffic = 1;
        else
            traffic = 0;

        if (chbAlways.isChecked())
            defaultClass = 1;
        else
            defaultClass = 0;

        switch (rgCarClass.getCheckedRadioButtonId()) {
            case R.id.rbUnknow:
                carClass = 0;
                break;
            case R.id.rbTaxi:
                carClass = 4;
                break;
            case R.id.rbPrivilage:
                carClass = 2;
                break;
            case R.id.rbEconomical:
                carClass = 1;
                break;
            case R.id.rbFormality:
                carClass = 3;
                break;
        }

        new GeneralDialog()
                .title("ثبت اطلاعات")
                .cancelable(false)
                .message("آیا از ثبت اطلاعات اطمینان دارید؟")
                .firstButton("بله", () ->
                        insertService(serviceCount, tell, mobile, cityCode,
                                name, address, fixedComment, stationName, serviceType, carClass, normalDescription, traffic, defaultClass))
                .secondButton("خیر", () -> {
                    if (vfSubmit != null)
                        vfSubmit.setDisplayedChild(0);
                })
                .show();
    }

    ArrayList<StationInfoModel> stationInfoModels;

    private void setActivate(int sipNumber) {

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.ACTIVATE)
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
                    Log.i(TAG, "onResponse: " + args[0].toString());
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
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
                    AvaCrashReporter.send(e, "TripRegisterActivity class, setActivate onResponse method");
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
            RequestHelper.builder(EndPoints.DEACTIVATE)
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
                    Log.i(TAG, "onResponse: " + args[0].toString());
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
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
                    AvaCrashReporter.send(e, "TripRegisterActivity class, setDeActivate onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismiss);

        }
    };

    private void insertService(int count, String phoneNumber, String mobile, int cityCode, String callerName,
                               String address, String fixedComment, String destination, int typeService,
                               int classType, String description, int TrafficPlan, int defaultClass) {

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.INSERT_TRIP_SENDING_QUEUE)
                .addParam("phoneNumber", phoneNumber)
                .addParam("mobile", mobile)
                .addParam("callerName", callerName)
                .addParam("fixedComment", fixedComment)
                .addParam("address", address)
                .addParam("stationCode", originStation)
                .addParam("destinationStation", 0)
                .addParam("destination", destination)
                .addParam("cityCode", cityCode)
                .addParam("typeService", typeService)
                .addParam("classType", classType)
                .addParam("description", description)
                .addParam("TrafficPlan", TrafficPlan)
                .addParam("voipId", voipId)
                .addParam("defaultClass", defaultClass)
                .addParam("count", count)
                .addParam("queue", queue)
                .addParam("senderClient", 0)
                .listener(insertService)
                .post();

    }

    RequestHelper.Callback insertService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (vfSubmit != null)
                            vfSubmit.setDisplayedChild(0);
                        LoadingDialog.dismissCancelableDialog();
                        Log.i(TAG, "run: " + args[0].toString());
                        JSONObject obj = new JSONObject(args[0].toString());
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        if (success) {

                            new GeneralDialog()
                                    .title("ثبت شد")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {
                                        String tempVoipId = voipId;
                                        clearData();
                                        CallModel callModel = parseNotification(MyApplication.prefManager.getLastNotification());
                                        if (callModel != null)
                                            if (!callModel.getVoipId().equals(tempVoipId)) {
                                                MyApplication.prefManager.setLastNotification(null);
                                                handleCallerInfo(callModel);
                                            }

                                    })

                                    .show();
                            if (svTripRegister != null)
                                svTripRegister.scrollTo(0, 0);
                        } else {
                            new GeneralDialog()
                                    .title("خطا")
                                    .message(message)
                                    .secondButton("بستن", null)
                                    .show();
                        }
                        LoadingDialog.dismissCancelableDialog();

                    } catch (JSONException e) {
                        LoadingDialog.dismissCancelableDialog();
                        e.printStackTrace();
                        AvaCrashReporter.send(e, "TripRegisterActivity class, insertService onResponse method");
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                if (vfSubmit != null)
                    vfSubmit.setDisplayedChild(0);
            });
        }

        @Override
        public void onReloadPress(boolean v) {

            super.onReloadPress(v);
            try {
                if (v)
                    MyApplication.handler.post(LoadingDialog::makeCancelableLoader);
                else
                    MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);

            } catch (Exception e) {
                e.printStackTrace();
                AvaCrashReporter.send(e, "TripRegisterActivity class, onReloadPress method");
            }
        }
    };

    private void clearData() {
        if (edtTell == null) return;
        originStation = 0;
        addressLength = 0;
        isEnableView = false;
        isTellValidable = false;
        edtTell.requestFocus();
        txtLockPassenger.setVisibility(View.GONE);
        txtNewPassenger.setVisibility(View.GONE);
        edtTell.setText("");
        edtMobile.setText("");
        edtDiscount.setText("");
        edtFamily.setText("");
        edtAddress.setText("");
        addressChangeCounter = 0;
        chbTraffic.setChecked(false);
        txtDescription.setText("");
        chbAlways.setChecked(false);
        rgCarClass.clearCheck();
        rbUnknow.setChecked(true);
        voipId = "0";
        queue = "0";
        normalDescription = "";
    }

    private void enableViews() {
        edtFamily.setEnabled(true);
//    edtDiscount.setEnabled(true);
        edtAddress.setEnabled(true);
        txtDescription.setEnabled(true);
        chbTraffic.setEnabled(true);
        llTrafficBg.setEnabled(true);
        chbAlways.setEnabled(true);
        txtPassengerAddress.setEnabled(true);
        llDescriptionDetail.setEnabled(true);
        llServiceType.setEnabled(true);
        llTraffic.setEnabled(true);
        llAlwaysBg.setEnabled(true);
        llAlways.setEnabled(true);
        llServiceCount.setEnabled(true);
        rgCarClass.setEnabled(true);
        llDiscount.setEnabled(true);
        llFamily.setEnabled(true);
        llAddress.setEnabled(true);
        spServiceCount.setEnabled(true);
        spServiceType.setEnabled(true);

    }

    private void disableViews() {
        edtFamily.setEnabled(false);
//    edtDiscount.setEnabled(false);
        edtAddress.setEnabled(false);
        txtDescription.setEnabled(false);
        chbTraffic.setEnabled(false);
        llTrafficBg.setEnabled(false);
        chbAlways.setEnabled(false);
        txtPassengerAddress.setEnabled(false);
        llDescriptionDetail.setEnabled(false);
        llServiceType.setEnabled(false);
        llTraffic.setEnabled(false);
        llAlwaysBg.setEnabled(false);
        llAlways.setEnabled(false);
        llServiceCount.setEnabled(false);
        llDiscount.setEnabled(false);
        llFamily.setEnabled(false);
        llAddress.setEnabled(false);
        spServiceCount.setEnabled(false);
        spServiceType.setEnabled(false);
        rgCarClass.setEnabled(false);
        for (int i = 0; i < rgCarClass.getChildCount(); i++) {
            rgCarClass.getChildAt(i).setEnabled(false);
        }


    }

    //receive push notification from local broadcast
    BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Keys.KEY_MESSAGE);
            handleCallerInfo(parseNotification(result));
        }
    };

    //receive userStatus from local broadcast
    BroadcastReceiver userStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageUserStatus = intent.getStringExtra(Keys.KEY_MESSAGE_USER_STATUS);
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

    private void handleCallerInfo(CallModel callModel) {
        try {
            if (voipId.equals("0")) {
                //show CallerId
                if (callModel == null) {
                    return;
                }
                String participant = PhoneNumberValidation.removePrefix(callModel.getParticipant());
                queue = callModel.getQueue();
                voipId = callModel.getVoipId();
                if (edtTell == null) return;
                if (participant == null) return;
                edtTell.setText(participant);
                MyApplication.handler.postDelayed(this::onPressDownload, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, handleCallerInfo method");
        }
    }

    /**
     * @param info have below format
     *             sample : {"type":"callerInfo","exten":"456","participant":"404356734579","queue":"999","voipId":"1584260434.9922480"}
     */
    private CallModel parseNotification(String info) {
        if (info == null) return null;
        try {
            JSONObject object = new JSONObject(info);
            String strMessage = object.getString("message");
            JSONObject messages = new JSONObject(strMessage);
            String typee = messages.getString("type");

            if (typee.equals("callerInfo")) {
                JSONObject message = new JSONObject(strMessage);
                CallModel callModel = new CallModel();
                callModel.setType(message.getString("type"));
                callModel.setExten(message.getInt("exten"));
                callModel.setParticipant(message.getString("participant"));
                callModel.setQueue(message.getString("queue"));
                callModel.setVoipId(message.getString("voipId"));
                return callModel;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, parseNotification method ,info : " + info);
            return null;
        }
        return null;
    }

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

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            TripRegisterActivity.this.call = call;

            if (state == Call.State.IncomingReceived) {
                showCallIncoming();
            } else if (state == Call.State.Released) {
                if (imgEndCall != null)
                    imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            } else if (state == Call.State.Connected) {
                startCallQuality();
                if (imgEndCall != null)
                    imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                Address address = call.getRemoteAddress();
                if (voipId.equals("0")) {
                    edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                }
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

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.currentActivity = this;
        registerReceiver(pushReceiver, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((pushReceiver), new IntentFilter(Keys.KEY_BROADCAST_PUSH));
        registerReceiver(userStatusReceiver, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((userStatusReceiver), new IntentFilter(Keys.KEY_REFRESH_USER_STATUS));

        core = LinphoneService.getCore();
        core.addListener(mCoreListener);
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);
        showTitleBar();
        if (MyApplication.prefManager.getConnectedCall()) {
            startCallQuality();
            imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);

            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call != null && call.getState() == Call.State.StreamsRunning) {
                    if (voipId.equals("0")) {
                        Address address = call.getRemoteAddress();
                        edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                        MyApplication.handler.postDelayed(() -> onPressDownload(), 600);
                    }
                }
            }
        }

//    Call call = core.getCurrentCall();
//    if (call != null) {
//      startCallQuality();
//    }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (pushReceiver != null) {
            unregisterReceiver(pushReceiver);
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).unregisterReceiver(pushReceiver);
        }

        if (userStatusReceiver != null) {
            unregisterReceiver(userStatusReceiver);
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).unregisterReceiver(userStatusReceiver);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyBoardHelper.hideKeyboard();
        MyApplication.prefManager.setAppRun(false);
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        core.removeListener(mCoreListener);
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
        core = null;

    }

    @Override
    public void onBackPressed() {
        try {
            KeyBoardHelper.hideKeyboard();
            if (getFragmentManager().getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                new GeneralDialog()
                        .title("خروج")
                        .message("آیا از خروج خود اطمینان دارید؟")
                        .firstButton("بله", new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Intent intent=new Intent(MyApplication.context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    AvaCrashReporter.send(e, "TripRegisterActivity class, onBackPressed method");
                                }
                            }
                        })
                        .secondButton("خیر", null)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegister class, onBackPressed method");
        }
    }

    private void showCallIncoming() {
        mRipplePulseLayout.startRippleAnimation();
        call = core.getCurrentCall();
        Address address = call.getRemoteAddress();
        txtCallerNum.setText(address.getUsername());
        rlNewInComingCall.setVisibility(View.VISIBLE);
        rlActionBar.setVisibility(View.GONE);
    }

    private void showTitleBar() {
        mRipplePulseLayout.stopRippleAnimation();
        rlNewInComingCall.setVisibility(View.GONE);
        rlActionBar.setVisibility(View.VISIBLE);
    }

}
