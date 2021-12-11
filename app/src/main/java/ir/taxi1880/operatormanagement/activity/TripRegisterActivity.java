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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;

import java.util.ArrayList;
import java.util.Arrays;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AddressAdapter;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.Keys;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.ActivityTripRegisterBinding;
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.CityDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.TripOptionDialog;
import ir.taxi1880.operatormanagement.fragment.PassengerTripSupportFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.ThemeHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AddressArr;
import ir.taxi1880.operatormanagement.model.AddressesModel;
import ir.taxi1880.operatormanagement.model.CallModel;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class TripRegisterActivity extends AppCompatActivity {
    public static final String TAG = TripRegisterActivity.class.getSimpleName();
    ActivityTripRegisterBinding binding;
    private String cityName = "";
    private String cityLatinName = "";
    private String maxDiscount = "";
    private String percentDiscount = "";
    private int cityCode;
    private String normalDescription = " ";
    private int originStation = 0;
    private int destinationStation = 0;
    private int originAddressLength = 0;
    private int destAddressLength = 0;
    private int originAddressChangeCounter = 0; // this variable count the last edition of edtAddress
    private int destAddressChangeCounter = 0; // this variable count the last edition of edtAddress
    private String originAddressId = "0";
    private String destinationAddressId = "0";
    private String destinationAddress = " ";// It must have a value otherwise it will get an error of 422
    private String originAddress = " ";// It must have a value otherwise it will get an error of 422
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
    int stopTime = 0;
    String queue = "0";
    String voipId = "0";
    String permanentDesc = "";
    private String[] countService = new String[6];
    private Runnable mCallQualityUpdater = null;
    private int mDisplayedQuality = -1;
    String passengerId;

    ArrayList<AddressesModel> originAddresses;
    ArrayList<AddressesModel> destinationAddresses;

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

        binding = ActivityTripRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TypefaceUtil.overrideFonts(binding.getRoot());
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        if (MyApplication.prefManager.getActivateStatus()) {
            binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        } else {
            binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        }

        disableViews();

        MyApplication.handler.postDelayed(() -> {
            initCitySpinner();
            initServiceTypeSpinner();
            initServiceCountSpinner();
            initWaitingTimeSpinner();
        }, 500);

        binding.edtTell.requestFocus();

        binding.edtTell.addTextChangedListener(edtTellTextWatcher);

        binding.edtMobile.addTextChangedListener(edtMobileTW);

        binding.edtOriginAddress.addTextChangedListener(originAddressTW);

        binding.edtDestinationAddress.addTextChangedListener(destAddressTW);

        binding.rgCarClass.setOnCheckedChangeListener((group, i) -> binding.chbAlways.setChecked(false));

        MyApplication.handler.postDelayed(() -> KeyBoardHelper.showKeyboard(MyApplication.context), 300);

        setCursorEnd(getWindow().getDecorView().getRootView());

        binding.imgBack.setOnClickListener(v -> MyApplication.currentActivity.onBackPressed());
        binding.llCity.setOnClickListener(v -> binding.spCity.performClick());
        binding.llWaitingTime.setOnClickListener(v -> binding.spWaitingTime.performClick());
        binding.llServiceType.setOnClickListener(v -> binding.spServiceType.performClick());
        binding.llServiceCount.setOnClickListener(v -> binding.spServiceCount.performClick());

        binding.llTell.setOnClickListener(v -> {
            binding.edtTell.requestFocus();
            KeyBoardHelper.showKeyboard(MyApplication.context);
        });

        binding.llDiscount.setOnClickListener(v -> {
            binding.edtDiscount.requestFocus();
            KeyBoardHelper.showKeyboard(MyApplication.context);
        });

        binding.llMobile.setOnClickListener(v -> {
            binding.edtMobile.requestFocus();
            KeyBoardHelper.showKeyboard(MyApplication.context);
        });

        binding.llFamily.setOnClickListener(v -> {
            binding.edtFamily.requestFocus();
            KeyBoardHelper.showKeyboard(MyApplication.context);
        });

        binding.llAddress.setOnClickListener(v -> {
            binding.edtOriginAddress.requestFocus();
            KeyBoardHelper.showKeyboard(MyApplication.context);
        });

        binding.llTraffic.setOnClickListener(v -> binding.chbTraffic.setChecked(!binding.chbTraffic.isChecked()));

        binding.llAlways.setOnClickListener(v -> binding.chbAlways.setChecked(!binding.chbAlways.isChecked()));

        binding.imgPassengerInfo.setOnClickListener(v -> onPressDownload());

        binding.llDescriptionDetail.setOnClickListener(v -> new DescriptionDialog().show(new DescriptionDialog.Listener() {
            @Override
            public void description(String description) {
                normalDescription = description;
            }

            @Override
            public void fixedDescription(String fixedDescription) {
                binding.txtDescription.setText(fixedDescription);
            }
        }, binding.txtDescription.getText().toString(), normalDescription));

        binding.txtPassengerAddress.setOnClickListener(v -> {
            if (getTellNumber().isEmpty()) {
                MyApplication.Toast("ابتدا شماره تلفن را وارد کنید", Toast.LENGTH_SHORT);
                return;
            }
            KeyBoardHelper.hideKeyboard();
            getPassengerOriginAddress();
        });

        binding.txtPassengerDestAddress.setOnClickListener(v -> {
            if (getTellNumber().isEmpty()) {
                MyApplication.Toast("ابتدا شماره تلفن را وارد کنید", Toast.LENGTH_SHORT);
                return;
            }
            KeyBoardHelper.hideKeyboard();
            getPassengerDestAddress();
        });

        binding.imgAccept.setOnClickListener(v -> {
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
        });

        binding.imgReject.setOnClickListener(v -> {
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

        binding.btnSubmit.setOnClickListener(v -> {

            int addressPercent = originAddressLength * 50 / 100;
            if (originAddressChangeCounter >= addressPercent) {
                originStation = 0;
                originAddressId = "0";
            }
            int destAddressPercent = destAddressLength * 50 / 100;
            if (destAddressChangeCounter >= destAddressPercent) {
                destinationStation = 0;
                destinationAddressId = "0";
            }

//            Log.i("TAF", "onPressSubmit: address length " + destAddressLength);
//            Log.i("TAF", "onPressSubmit: address percent " + destAddressPercent);
//            Log.i("TAF", "onPressSubmit: address change counter " + destAddressChangeCounter);
//            Log.i("TAF", "onPressSubmit: originStation " + destAddressPercent);

            if (cityCode == -1 || cityCode == 0) {
                MyApplication.Toast("شهر را وارد نمایید", Toast.LENGTH_SHORT);
                binding.spCity.performClick();
                return;
            }
            if (getTellNumber().isEmpty()) {
                binding.edtTell.setError("شماره تلفن را وارد کنید");
                binding.edtTell.requestFocus();
                return;
            }
            if (getMobileNumber().isEmpty() && !isTellValidable) {
                binding.edtMobile.setError("شماره همراه را وارد کنید");
                binding.edtMobile.requestFocus();
                return;
            }
            if (binding.edtFamily.getText().toString().isEmpty()) {
                binding.edtFamily.setError(" نام مسافر را مشخص کنید");
                binding.edtFamily.requestFocus();
                return;
            }
            if (binding.edtOriginAddress.getText().toString().trim().isEmpty()) {
                binding.edtOriginAddress.setError("آدرس مبدا را مشخص کنید");
                binding.edtOriginAddress.requestFocus();
                return;
            }

            if (binding.edtDestinationAddress.getText().toString().trim().isEmpty()) {
                binding.edtDestinationAddress.setError("آدرس مقصد را مشخص کنید");
                binding.edtDestinationAddress.requestFocus();
                return;
            }
//        if (serviceType == 1 && stopTime == 0) {
//            MyApplication.Toast("لطفا مقدار توقف را مشخص کنید.", Toast.LENGTH_SHORT);
//            binding.spWaitingTime.requestFocus();
//            return;
//        }
            binding.vfSubmit.setDisplayedChild(1);
            callInsertService();
        });

        binding.btnOptions.setOnClickListener(v -> {
            KeyBoardHelper.hideKeyboard();
            new TripOptionDialog().show(b -> {
                if (b) {
                    clearData();
                }
            }, getMobileNumber(), binding.edtFamily.getText().toString(), cityCode);
        });

        binding.llClear.setOnClickListener(v ->
                new GeneralDialog()
                        .title("هشدار")
                        .message("آیا از پاک کردن اطلاعات اطمینان دارید؟")
                        .firstButton("بله", this::clearData).secondButton("خیر", null)
                        .show());

        binding.llEndCall.setOnClickListener(v -> {
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

//            Call call = LinphoneService.getCore().getCurrentCall();
//            call.terminate();
        });

        binding.btnActivate.setOnClickListener(v -> {
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

        binding.clearOriginAddress.setOnClickListener(v -> {
            binding.edtOriginAddress.getText().clear();
            originStation = 0;
            originAddressLength = 0;
            originAddressChangeCounter = 0;
            originAddressId = "0";
//            destAddressLength = 0;
//            destAddressChangeCounter = 0;
        });

        binding.clearDestinationAddress.setOnClickListener(v -> {
            binding.edtDestinationAddress.getText().clear();
            destinationStation = 0;
            destAddressLength = 0;
            destAddressChangeCounter = 0;
            destinationAddressId = "0";
        });

        binding.btnDeActivate.setOnClickListener(v -> {
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

        binding.edtTell.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (getTellNumber().trim().isEmpty()) {
                    try {
                        Core core = LinphoneService.getCore();
                        Call[] calls = core.getCalls();
                        for (Call callList : calls) {
                            if (callList.getState() == Call.State.Connected) {
                                call = core.getCurrentCall();
                                Address address = call.getRemoteAddress();
                                binding.edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AvaCrashReporter.send(e, "callIncomingActivity");
                    }
                }
            }
        });
    }

    private void onPressDownload() {
        if (getTellNumber().isEmpty()) {
            binding.edtTell.setError("شماره تلفن را وارد نمایید");
            binding.edtTell.requestFocus();
            return;
        }

//    if (getMobileNumber().isEmpty() && !isTellValidable && binding.edtMobile != null) {
//      binding.edtMobile.setError("شماره تلفن همراه را وارد نمایید");
//      binding.edtMobile.requestFocus();
//      return;
//    }

        String mobile = isTellValidable && getMobileNumber().isEmpty() ? "0" : getMobileNumber();

        getPassengerInfo(StringHelper.toEnglishDigits(getTellNumber()), StringHelper.toEnglishDigits(mobile), StringHelper.toEnglishDigits(queue));
    }

    private String getTellNumber() {
        String txtTell = binding.edtTell.getText().toString();
        if (txtTell == null)
            return "";
        else
            return txtTell;
    }

    private String getMobileNumber() {
        String mobileNo = binding.edtMobile.getText().toString();
        if (mobileNo == null)
            return "";
        else
            return mobileNo;
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
                binding.edtMobile.setText(PhoneNumberValidation.removePrefix(editable.toString()));

        }
    };

    TextWatcher edtTellTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (charSequence.toString().isEmpty()) {
//        binding.spCity.setSelection(0);
            isEnableView = false;
            disableViews();
            binding.spCity.setSelection(0);
//            MyApplication.handler.postDelayed(() -> {
            initCitySpinner();
            initServiceTypeSpinner();
            initServiceCountSpinner();
            initWaitingTimeSpinner();
//            }, 200);
//        }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (PhoneNumberValidation.havePrefix(editable.toString()))
                binding.edtTell.setText(PhoneNumberValidation.removePrefix(editable.toString()));

            if (PhoneNumberValidation.isValid(editable.toString())) {
                isTellValidable = false;
                binding.edtMobile.setText(editable.toString());
            } else {
//          clearData();
//          binding.edtMobile.setText("");
                isTellValidable = true;
                binding.edtFamily.setText("");
                originAddressChangeCounter = 0;
                binding.edtOriginAddress.setText("");
                binding.txtDescription.setText("");
                binding.rgCarClass.clearCheck();
                binding.txtLockPassenger.setVisibility(View.GONE);
                binding.txtNewPassenger.setVisibility(View.GONE);
                binding.rbUnknow.setChecked(true);
                binding.chbAlways.setChecked(false);
                binding.edtTell.setNextFocusDownId(R.id.edtMobile);
                binding.edtMobile.setNextFocusDownId(R.id.edtMobile);
            }
        }
    };

    TextWatcher originAddressTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            originAddressChangeCounter = originAddressChangeCounter + 1;
//            if (binding.edtOriginAddress.isFocused()) {
//                originAddressId = "0";
//            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                originStation = 0;
                originAddressLength = 0;
                binding.edtOriginAddress.getText().clear();
            }

//            String result = editable.toString().replaceAll("  ", " ");
//            if (!editable.toString().equals(result)) {
//                binding.edtOriginAddress.setText(result);
////                ed.setSelection(result.length());
//            }
        }
    };

    TextWatcher destAddressTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            destAddressChangeCounter = destAddressChangeCounter + 1;
//            if (binding.edtDestinationAddress.isFocused()) {
//                destinationAddressId = "0";
//            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                destinationStation = 0;
                destAddressLength = 0;
                binding.edtDestinationAddress.getText().clear();
            }

//            String result = editable.toString().replaceAll("  ", " ");
//            if (!editable.toString().equals(result)) {
//                binding.edtDestinationAddress.setText(result);
////                ed.setSelection(result.length());
//            }
        }
    };

    private void initServiceCountSpinner() {
        try {
            ArrayList<String> countServices = new ArrayList<>();
            for (int i = 1; i < countService.length; i++) {
                countService[i] = i + "";
                countServices.add(countService[i]);
            }

            binding.spServiceCount.setEnabled(isEnableView);
            binding.spServiceCount.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, countServices));
            binding.spServiceCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    serviceCount = Integer.parseInt(binding.spServiceCount.getSelectedItem().toString());
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
        ArrayList<String> serviceList = new ArrayList<>();
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
            binding.spServiceType.setEnabled(isEnableView);
            binding.spServiceType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

            binding.spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    serviceType = typeServiceModels.get(position).getId();
//                    if (serviceType == 1) {
//                        new CityDialog()
//                                .setTitle("مدت زمان توقف")
//                                .show(pos -> {
//                                    if (binding.spWaitingTime != null)
//                                        binding.spWaitingTime.setSelection(pos);
//                                }, false);
//                    }
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

    private void initWaitingTimeSpinner() {
        ArrayList<String> waitingTime = new ArrayList<>(Arrays.asList("بدون توقف", "۵ دقیقه", "۱۰ دقیقه", "۲۰ دقیقه", "۳۰ دقیقه", "۴۰ دقیقه", "۵۰ دقیقه", "۱ ساعت", "۱.۵ ساعت", "۲ ساعت", "۲.۵ ساعت", "۳ ساعت"));
        try {
            binding.spWaitingTime.setEnabled(isEnableView);
            binding.spWaitingTime.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, waitingTime));

            binding.spWaitingTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    switch (position) {
                        case 0:
                            stopTime = 0;
                            break;
                        case 1:
                            stopTime = 5;
                            break;
                        case 2:
                            stopTime = 10;
                            break;
                        case 3:
                            stopTime = 20;
                            break;
                        case 4:
                            stopTime = 30;
                            break;
                        case 5:
                            stopTime = 40;
                            break;
                        case 6:
                            stopTime = 50;
                            break;
                        case 7:
                            stopTime = 60;
                            break;
                        case 8:
                            stopTime = 90;
                            break;
                        case 9:
                            stopTime = 120;
                            break;
                        case 10:
                            stopTime = 150;
                            break;
                        case 11:
                            stopTime = 180;
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCitySpinner() {
        cityModels = new ArrayList<>();
        ArrayList<String> cityList = new ArrayList<>();
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
            binding.spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
            binding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);

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

    private void getPassengerInfo(String phoneNumber, String mobile, String queue) {
        binding.vfPassengerInfo.setDisplayedChild(1);

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
                        MyApplication.handler.postDelayed(() -> binding.spServiceType.setSelection(2, true), 500);
                    }

                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");

                        passengerId = dataObj.getString("passengerId");

                        JSONObject lastTripStatus = dataObj.getJSONObject("status");
                        int status = lastTripStatus.getInt("status");
//                    String descriptionStatus = lastTripStatus.getString("descriptionStatus");
                        String tripState = lastTripStatus.getString("tripState");
                        int callTimeInterval = lastTripStatus.getInt("callTimeInterval");

                        JSONObject passengerInfoObj = dataObj.getJSONObject("passengerInfo");
//                    originAddressId = passengerInfoObj.getInt("callerCode");
//                    originAddress = passengerInfoObj.getString("address");
//                    int originStationFromSV = passengerInfoObj.getInt("staion");
                        String name = passengerInfoObj.getString("name");
                        permanentDesc = passengerInfoObj.getString("description");
                        String discountCode = passengerInfoObj.getString("discountCode");
                        int discountId = passengerInfoObj.getInt("discountId");
                        int carType = passengerInfoObj.getInt("carType");
                        int cityCode = passengerInfoObj.getInt("cityCode");
                        maxDiscount = passengerInfoObj.getString("maxDiscount");
                        percentDiscount = passengerInfoObj.getString("percentDiscount");


                        JSONArray originAddressArr = dataObj.getJSONArray("originAddress");
                        JSONArray destinationAddressArr = dataObj.getJSONArray("destinationAddress");

                        originAddresses = new ArrayList<>();
                        ArrayList<AddressArr> originAutoAddresses = new ArrayList<>();
                        for (int i = 0; i < originAddressArr.length(); i++) {
                            JSONObject jsonAddress = originAddressArr.getJSONObject(i);

                            String Address = jsonAddress.getString("text");
                            String AddressId = jsonAddress.getString("_id");
                            int AddressStation = jsonAddress.getInt("station");

                            AddressArr arr  = new AddressArr();
                            arr.address = Address;

                            originAddresses.add(new AddressesModel(Address, AddressStation, AddressId));
                            originAutoAddresses.add(arr);

                            if (i == 0) {
                                originAddress = Address;
                                binding.edtOriginAddress.setText(originAddress);
                                originAddressLength = originAddress.length();
                                originStation = AddressStation;
                                originAddressId = AddressId;
                                originAddressChangeCounter = 0;
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            AddressAdapter originArrayAdapter = new AddressAdapter(MyApplication.context, android.R.layout.simple_dropdown_item_1line, R.id.lbl_address, originAutoAddresses);
                            binding.edtOriginAddress.setAdapter(originArrayAdapter);
                            binding.edtOriginAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                originAddress = binding.edtOriginAddress.getText().toString().trim();
                                    originAddress = originArrayAdapter.getAddress(position).getAddress();
                                    originAddressLength = originAddress.length();
                                    originAddressChangeCounter = 0;
                                    binding.edtOriginAddress.setText(originAddress);

                                    for (int i = 0; i < originAddresses.size(); i++) {
                                        if (originAddresses.get(i).getAddress().equals(originAddress)) {
                                            originStation = originAddresses.get(i).getStation();
                                            originAddressId = originAddresses.get(i).getAddressId();
                                        }
                                    }

                                    Log.i("TAF", "TAF_onItemClick,originStation: " + originStation);
                                    Log.i("TAF", "TAF_onItemClick,addressLength: " + originAddressLength);
                                    Log.i("TAF", "TAF_onItemClick,originAddressId: " + originAddressId);
                                }
                            });
                        }

                        destinationAddresses = new ArrayList<>();
                        ArrayList<AddressArr> destinationAutoAddresses = new ArrayList<>();
                        for (int i = 0; i < destinationAddressArr.length(); i++) {
                            JSONObject jsonAddress = destinationAddressArr.getJSONObject(i);

                            String Address = jsonAddress.getString("text");
                            String AddressId = jsonAddress.getString("_id");
                            int AddressStation = jsonAddress.getInt("station");

                            AddressArr arr  = new AddressArr();
                            arr.address = Address;

                            destinationAddresses.add(new AddressesModel(Address, AddressStation, AddressId));
                            destinationAutoAddresses.add(arr);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            AddressAdapter destinationArrayAdapter = new AddressAdapter(MyApplication.context, android.R.layout.simple_dropdown_item_1line, R.id.lbl_address, destinationAutoAddresses);
                            binding.edtDestinationAddress.setAdapter(destinationArrayAdapter);
                            binding.edtDestinationAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    destinationAddress = destinationArrayAdapter.getAddress(position).getAddress();
                                    destAddressLength = destinationAddress.length();
                                    destAddressChangeCounter = 0;
                                    binding.edtDestinationAddress.setText(destinationAddress);

                                    for (int i = 0; i < destinationAddresses.size(); i++) {
                                        if (destinationAddresses.get(i).getAddress().trim().equals(destinationAddress)) {
                                            destinationStation = destinationAddresses.get(i).getStation();
                                            destinationAddressId = destinationAddresses.get(i).getAddressId();
                                        }
                                    }

                                    Log.i("TAF", "TAF_onItemClick,destinationStation: " + destinationStation);
                                    Log.i("TAF", "TAF_onItemClick,destAddressLength:" + destAddressLength);
                                    Log.i("TAF", "TAF_onItemClick, destinationAddressId:" + destinationAddressId);
                                }
                            });
                        }
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("passengerTell", getTellNumber());
                        clipboard.setPrimaryClip(clip);
                        MyApplication.prefManager.setLastCallerId(getTellNumber().startsWith("51") ? getTellNumber().substring(2) : getTellNumber());


                        if (status == 2) {
                            String msg = " مسافر " + callTimeInterval + " دقیقه پیش سفری درخواست داده است " + "\n" + " وضعیت سفر : " + tripState;
                            binding.vfPassengerInfo.setDisplayedChild(0);
                            new GeneralDialog()
                                    .message(msg)
                                    .cancelable(false)
                                    .secondButton("بستن", null)
                                    .firstButton("پشتیبانی", () -> {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("tellNumber", getTellNumber());
                                        FragmentHelper.toFragment(MyApplication.currentActivity, new PassengerTripSupportFragment()).setArguments(bundle).replace();
                                    })
                                    .show();
                        }

                        binding.edtTell.setNextFocusDownId(R.id.edtFamily);
                        isEnableView = true;
                        initServiceCountSpinner();
                        initServiceTypeSpinner();
                        initWaitingTimeSpinner();
                        enableViews();

                        for (int i = 0; i < cityModels.size(); i++) {
                            if (cityModels.get(i).getId() == cityCode)
                                binding.spCity.setSelection(i + 1, true);
                        }
                        if (cityCode == 0) {
                            KeyBoardHelper.hideKeyboard();
                            new CityDialog().show(position -> binding.spCity.setSelection(position + 1), true);
                        }


                        if (originAddressId.equals("0")) {
                            binding.txtNewPassenger.setVisibility(View.VISIBLE);
                            binding.txtLockPassenger.setVisibility(View.GONE);
                            binding.edtFamily.requestFocus();
                        } else {
                            switch (status) {
                                case 0:
                                    binding.txtNewPassenger.setVisibility(View.GONE);
                                    binding.txtLockPassenger.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    binding.txtNewPassenger.setVisibility(View.GONE);
                                    binding.txtLockPassenger.setVisibility(View.VISIBLE);
                                    break;
                            }
                            binding.edtFamily.setText(name);
                            binding.txtDescription.setText(permanentDesc + "");
                            binding.rgCarClass.clearCheck();
                            binding.edtDiscount.setText(discountCode);
                            switch (carType) {
                                case 0:
                                    binding.rbUnknow.setChecked(true);
                                    break;
                                case 1:
                                    binding.rbEconomical.setChecked(true);
                                    binding.chbAlways.setChecked(true);
                                    break;
                                case 2:
                                    binding.rbPrivilage.setChecked(true);
                                    binding.chbAlways.setChecked(true);
                                    break;
                                case 3:
                                    binding.rbFormality.setChecked(true);
                                    binding.chbAlways.setChecked(true);
                                    break;
                                case 4:
                                    binding.rbTaxi.setChecked(true);
                                    binding.chbAlways.setChecked(true);
                                    break;
                            }


                        }
                    }
                    MyApplication.handler.postDelayed(() -> binding.vfPassengerInfo.setDisplayedChild(0), 500);

                } catch (JSONException e) {
                    e.printStackTrace();
                    binding.vfPassengerInfo.setDisplayedChild(0);
                    AvaCrashReporter.send(e, "TripRegisterActivity class, getPassengerInfo onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> binding.vfPassengerInfo.setDisplayedChild(0));
        }
    };

    private void getPassengerOriginAddress() {
        binding.vfPassengerOriginAddress.setDisplayedChild(1);
        MyApplication.handler.postDelayed(() ->
        {
            if (originAddresses.size() == 0) {
                MyApplication.Toast("آدرسی موجود نیست", Toast.LENGTH_SHORT);
            } else {
                new AddressListDialog().show(true, passengerId, originAddresses, (address, stationCode, addressId) -> {
                    originAddress = address;
                    binding.edtOriginAddress.setText(originAddress);
                    originAddressLength = originAddress.length();
                    originAddressChangeCounter = 0;
                    originStation = stationCode;
                    originAddressId = addressId;

                    Log.i("TAF", "TAF_getPassengerOriginAddress,originStation: " + originStation);
                    Log.i("TAF", "TAF_getPassengerOriginAddress,addressLength: " + originAddressLength);
                    Log.i("TAF", "TAF_getPassengerOriginAddress,originAddressId: " + originAddressId);

                });
            }
            binding.vfPassengerOriginAddress.setDisplayedChild(0);
        }, 500);
    }

    private void getPassengerDestAddress() {
        binding.vfPassengerDestAddress.setDisplayedChild(1);
        MyApplication.handler.postDelayed(() ->
        {
            if (originAddresses.size() == 0) {
                MyApplication.Toast("آدرسی موجود نیست", Toast.LENGTH_SHORT);
            } else {
                new AddressListDialog().show(false, passengerId, destinationAddresses, (address, stationCode, addressId) -> {
                    destinationAddress = address;
                    binding.edtDestinationAddress.setText(destinationAddress);
                    destAddressLength = destinationAddress.length();
                    destAddressChangeCounter = 0;
                    destinationStation = stationCode;
                    destinationAddressId = addressId;

                    Log.i("TAF", "TAF_getPassengerDestinationAddress,destinationStation: " + destinationStation);
                    Log.i("TAF", "TAF_getPassengerDestinationAddress,destAddressLength:" + destAddressLength);
                    Log.i("TAF", "TAF_getPassengerDestinationAddress, destinationAddressId:" + destinationAddressId);
                });
            }

            binding.vfPassengerDestAddress.setDisplayedChild(0);
        }, 500);
    }

    private void callInsertService() {
        String mobile = isTellValidable && getMobileNumber().isEmpty() ? "0" : getMobileNumber();
        String tell = getTellNumber();
        String name = binding.edtFamily.getText().toString().trim();
        originAddress = binding.edtOriginAddress.getText().toString().trim();
        String fixedComment = binding.txtDescription.getText().toString().trim();
        destinationAddress = binding.edtDestinationAddress.getText().toString().trim();


        if (binding.chbTraffic.isChecked())
            traffic = 1;
        else
            traffic = 0;

        if (binding.chbAlways.isChecked())
            defaultClass = 1;
        else
            defaultClass = 0;

        switch (binding.rgCarClass.getCheckedRadioButtonId()) {
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

        Log.i("TAF", "\nserviceCount:" + serviceCount
                + "\ntell: " + tell
                + "\nmobile: " + mobile
                + "\ncityCode: " + cityCode
                + "\nname: " + name
                + "\noriginAddress: " + originAddress
                + "\nfixedComment: " + fixedComment
                + "\ndestinationAddress: " + destinationAddress
                + "\nserviceType: " + serviceType
                + "\ncarClass: " + carClass
                + "\nnormalDescription: " + normalDescription
                + "\ntraffic: " + traffic
                + "\ndefaultClass: " + defaultClass
                + "\nstopTime: " + stopTime
                + "\nqueue: " + queue
                + "\npercentDiscount: " + percentDiscount
                + "\nmaxDiscount: " + maxDiscount
                + "\naddressIdOrigin: " + originAddressId
                + "\naddressIdDestination: " + destinationAddressId
                + "\nstationCode: " + originStation
                + "\ndestinationStation: " + destinationStation);

        new GeneralDialog()
                .title("ثبت اطلاعات")
                .cancelable(false)
                .message("آیا از ثبت اطلاعات اطمینان دارید؟")
                .firstButton("بله", () ->
                        insertService(serviceCount, tell, mobile, cityCode,
                                name, originAddress, fixedComment, destinationAddress, serviceType, carClass, normalDescription, traffic, defaultClass, stopTime))
                .secondButton("خیر", () -> binding.vfSubmit.setDisplayedChild(0))
                .show();
    }

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
                        MyApplication.prefManager.setActivityStatus(1);
                        MyApplication.Toast("شما باموفقیت وارد صف شدید", Toast.LENGTH_SHORT);
                        binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                        MyApplication.prefManager.setActivateStatus(true);
                        binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
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
                        MyApplication.prefManager.setActivityStatus(0);
                        MyApplication.Toast("شما باموفقیت از صف خارج شدید", Toast.LENGTH_SHORT);
                        MyApplication.prefManager.setActivateStatus(false);
                        binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                        binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
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
                               int classType, String description, int TrafficPlan, int defaultClass, int stopTime) {

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.INSERT_TRIP_SENDING_QUEUE)
                .addParam("phoneNumber", phoneNumber)
                .addParam("mobile", mobile)
                .addParam("callerName", callerName)
                .addParam("fixedComment", fixedComment)
                .addParam("address", address)
                .addParam("stationCode", originStation)
                .addParam("destinationStation", destinationStation)
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
                .addParam("percentDiscount", percentDiscount)
                .addParam("maxDiscount", maxDiscount)
                .addParam("senderClient", 0)
                .addParam("stopTime", stopTime)
                .addParam("addressIdOrigin", originAddressId)
                .addParam("addressIdDestination", destinationAddressId)
                .listener(insertService)
                .post();
    }

    RequestHelper.Callback insertService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    binding.vfSubmit.setDisplayedChild(0);
                    LoadingDialog.dismissCancelableDialog();
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
                        binding.svTripRegister.scrollTo(0, 0);
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
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                binding.vfSubmit.setDisplayedChild(0);
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
        originStation = 0;
        destinationStation = 0;
        originAddressLength = 0;
        destAddressLength = 0;
        originAddressId = "0";
        destinationAddressId = "0";
        originAddressChangeCounter = 0;
        destAddressChangeCounter = 0;
        isEnableView = false;
        isTellValidable = false;
        binding.edtTell.requestFocus();
        binding.txtLockPassenger.setVisibility(View.GONE);
        binding.txtNewPassenger.setVisibility(View.GONE);
        binding.edtTell.setText("");
        binding.edtMobile.setText("");
        binding.edtDiscount.setText("");
        binding.edtFamily.setText("");
        binding.edtOriginAddress.setText("");
        binding.edtDestinationAddress.setText("");
        binding.chbTraffic.setChecked(false);
        binding.txtDescription.setText("");
        binding.chbAlways.setChecked(false);
        binding.rgCarClass.clearCheck();
        binding.rbUnknow.setChecked(true);
        voipId = "0";
        queue = "0";
        normalDescription = "";
    }

    private void enableViews() {
        binding.edtFamily.setEnabled(true);
//    binding.edtDiscount.setEnabled(true);
        binding.edtOriginAddress.setEnabled(true);
        binding.edtDestinationAddress.setEnabled(true);
        binding.txtDescription.setEnabled(true);
        binding.chbTraffic.setEnabled(true);
        binding.llTrafficBg.setEnabled(true);
        binding.chbAlways.setEnabled(true);
        binding.txtPassengerAddress.setEnabled(true);
        binding.txtPassengerDestAddress.setEnabled(true);
        binding.llDescriptionDetail.setEnabled(true);
        binding.llServiceType.setEnabled(true);
        binding.llWaitingTime.setEnabled(true);
        binding.llTraffic.setEnabled(true);
        binding.llAlwaysBg.setEnabled(true);
        binding.llAlways.setEnabled(true);
        binding.llServiceCount.setEnabled(true);
        binding.llDiscount.setEnabled(true);
        binding.llFamily.setEnabled(true);
        binding.llAddress.setEnabled(true);
        binding.spServiceCount.setEnabled(true);
        binding.spServiceType.setEnabled(true);
        binding.rgCarClass.setEnabled(true);
        activationRadioButton(binding.rgCarClass, true);

    }

    private void disableViews() {
        binding.edtFamily.setEnabled(false);
//    binding.edtDiscount.setEnabled(false);
        binding.edtOriginAddress.setEnabled(false);
        binding.edtDestinationAddress.setEnabled(false);
        binding.txtDescription.setEnabled(false);
        binding.chbTraffic.setEnabled(false);
        binding.llTrafficBg.setEnabled(false);
        binding.chbAlways.setEnabled(false);
        binding.txtPassengerAddress.setEnabled(false);
        binding.txtPassengerDestAddress.setEnabled(false);
        binding.llDescriptionDetail.setEnabled(false);
        binding.llServiceType.setEnabled(false);
        binding.llWaitingTime.setEnabled(false);
        binding.llTraffic.setEnabled(false);
        binding.llAlwaysBg.setEnabled(false);
        binding.llAlways.setEnabled(false);
        binding.llServiceCount.setEnabled(false);
        binding.llDiscount.setEnabled(false);
        binding.llFamily.setEnabled(false);
        binding.llAddress.setEnabled(false);
        binding.spServiceCount.setEnabled(false);
        binding.spServiceType.setEnabled(false);
        binding.rgCarClass.setEnabled(false);
        activationRadioButton(binding.rgCarClass, false);
    }

    private void activationRadioButton(View view, boolean setEnabled) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View childVg = vg.getChildAt(i);
                view.setEnabled(setEnabled);
                activationRadioButton(childVg, setEnabled);
            }
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setEnabled(setEnabled);
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
                binding.btnDeActivate.setBackgroundResource(R.drawable.bg_bot_left);
                binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                MyApplication.prefManager.setActivateStatus(false);
            } else {
                binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
                binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
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
                DataHolder.getInstance().setVoipId(voipId);
                if (participant == null) return;
                binding.edtTell.setText(participant);
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
        binding.imgCallQuality.setVisibility(View.VISIBLE);
        binding.imgCallQuality.setImageResource(imageRes);
        mDisplayedQuality = iQuality;
    }

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            TripRegisterActivity.this.call = call;

            if (state == Call.State.IncomingReceived) {
                showCallIncoming();
            } else if (state == Call.State.Released) {
                binding.imgEndCall.setImageResource(R.drawable.ic_call_dialog_disable);
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            } else if (state == Call.State.Connected) {
                startCallQuality();
                binding.imgEndCall.setImageResource(R.drawable.ic_call_dialog_enable);
                Address address = call.getRemoteAddress();
                if (voipId.equals("0")) {
                    binding.edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                }
                showTitleBar();
            } else if (state == Call.State.Error) {
                showTitleBar();
            } else if (state == Call.State.End) {
                binding.imgCallQuality.setVisibility(View.INVISIBLE);
                showTitleBar();
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            }
        }
    };
    // باید دوتا لیسنر باشه چون هرکدوم جاهای مختلفی استارت و استوپ میشن
    CoreListenerStub sipStatusListener = new CoreListenerStub() {
        @Override
        public void onRegistrationStateChanged(Core lc, ProxyConfig proxy, RegistrationState state, String message) {
            if (core.getDefaultProxyConfig() != null && core.getDefaultProxyConfig().equals(proxy)) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state));
            } else if (core.getDefaultProxyConfig() == null) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state));
            }

            try {
                binding.imgSipStatus.setOnClickListener(
                        v -> {
                            Core core = LinphoneService.getCore();
                            if (core != null) {
                                core.refreshRegisters();
                            }
                        });
            } catch (IllegalStateException ise) {
                ise.printStackTrace();
            }
        }
    };

    private int getStatusIconResource(RegistrationState state) {
        try {
            Core core = LinphoneService.getCore();
            boolean defaultAccountConnected = (core != null && core.getDefaultProxyConfig() != null && core.getDefaultProxyConfig().getState() == RegistrationState.Ok);
            if (state == RegistrationState.Ok && defaultAccountConnected) {
                return R.drawable.ic_successful;
            } else if (state == RegistrationState.Progress) {
                return R.drawable.ic_pendig;
            } else if (state == RegistrationState.Failed) {
                return R.drawable.ic_error;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.drawable.ic_error;
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.currentActivity = this;
        registerReceiver(pushReceiver, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((pushReceiver), new IntentFilter(Keys.KEY_BROADCAST_PUSH));
        registerReceiver(userStatusReceiver, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((userStatusReceiver), new IntentFilter(Keys.KEY_REFRESH_USER_STATUS));

        core = LinphoneService.getCore();
        if (core != null) {
            core.addListener(mCoreListener);
        }
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);

        if (core != null) {
            core.addListener(sipStatusListener);
            ProxyConfig lpc = core.getDefaultProxyConfig();
            if (lpc != null) {
                sipStatusListener.onRegistrationStateChanged(core, lpc, lpc.getState(), null);
            }
        }

        showTitleBar();
        if (MyApplication.prefManager.getConnectedCall()) {
            startCallQuality();
            binding.imgEndCall.setImageResource(R.drawable.ic_call_dialog_enable);

            Call[] calls = core.getCalls();
            for (Call call : calls) {
                if (call != null && call.getState() == Call.State.StreamsRunning) {
                    if (voipId.equals("0")) {
                        Address address = call.getRemoteAddress();
                        binding.edtTell.setText(PhoneNumberValidation.removePrefix(address.getUsername()));
                        MyApplication.handler.postDelayed(this::onPressDownload, 600);
                    }
                }
            }
        }
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
        core.removeListener(sipStatusListener);
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                        .firstButton("بله", () -> {
                            try {
                                Intent intent = new Intent(MyApplication.context, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                AvaCrashReporter.send(e, "TripRegisterActivity class, onBackPressed method");
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
        binding.txtCallerNum.setText(address.getUsername());
        binding.rlNewInComingCall.setVisibility(View.VISIBLE);
        binding.rlActionBar.setVisibility(View.GONE);
    }

    private void showTitleBar() {
        mRipplePulseLayout.stopRippleAnimation();
        binding.rlNewInComingCall.setVisibility(View.GONE);
        binding.rlActionBar.setVisibility(View.VISIBLE);
    }

}
