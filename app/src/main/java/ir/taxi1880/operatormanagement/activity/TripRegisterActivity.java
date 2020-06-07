package ir.taxi1880.operatormanagement.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
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
import androidx.core.app.NotificationCompat;
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
import ir.taxi1880.operatormanagement.dialog.OptionDialog;
import ir.taxi1880.operatormanagement.dialog.SearchLocationDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.SoundHelper;
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
  //  View view;
  private String cityName = "";
  private String cityLatinName = "";
  private String normalDescription = "";
  private int cityCode;
  private String stationName = " ";
  private int originStationCode = -1;
  private int serviceType;
  private int serviceCount;
  private boolean isEnableView = false;
  private boolean isOriginValid;
  private boolean isDestinationValid;
  private boolean isTellValidable = false;
  String queue = "0";
  String voipId = "0";
  NotificationManager mNotificationManager;
  int notifManagerId = 0;


  @OnClick(R.id.imgBack)
  void onBack() {
    KeyBoardHelper.hideKeyboard();

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

  @BindView(R.id.edtOrigin)
  EditText edtOrigin;

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

  @BindView(R.id.llSearchOrigin)
  LinearLayout llSearchOrigin;

  @BindView(R.id.llTrafficBg)
  LinearLayout llTrafficBg;

  @BindView(R.id.llAlwaysBg)
  LinearLayout llAlwaysBg;

  @OnClick(R.id.imgStationInfo)
  void onStationInfoPress() {
    String origin = edtOrigin.getText().toString();
    if (origin.isEmpty()) {
      edtOrigin.setError("ایستگاه را وارد کنید");
      edtOrigin.requestFocus();
      return;
    }
    getStationInfo(origin);
  }

  @BindView(R.id.llSearchBg)
  ImageButton llSearchBg;

  @OnClick(R.id.llSearchBg)
  void onSearchPress() {
    new SearchLocationDialog().show(new SearchLocationDialog.Listener() {
      @Override
      public void description(String address, int code) {
        new GeneralDialog()
                .message("انتخاب شود برای")
                .firstButton("مبدا", new Runnable() {
                  @Override
                  public void run() {
                    edtOrigin.setText(code + "");

                  }
                }).secondButton("مقصد", new Runnable() {
          @Override
          public void run() {
            edtDestination.setText(code + "");
          }
        }).show();
      }
    }, "جست و جوی آدرس", cityLatinName);
  }

  //TODO HEAR
//  @OnClick(R.id.llSearchDestination)
//  void onDestination() {
//    edtDestination.requestFocus();
//    new SearchLocationDialog().show(new SearchLocationDialog.Listener() {
//      @Override
//      public void description(String address, int code) {
//
//        stationName = address;
//        Log.i(TAG, "description: " + address);
//      }
//    }, "جست و جوی مقصد", cityLatinName);
//  }

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

  @OnClick(R.id.llSearchAddress)
  void onPressSearchAddress() {
    if (getTellNumber().isEmpty()) {
      MyApplication.Toast("ابتدا شماره تلفن را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    getPassengerAddress(StringHelper.toEnglishDigits(getTellNumber()));
  }

  private String getTellNumber() {
    return edtTell.getText().toString();
  }

  private String getMobileNumber() {
    return edtMobile.getText().toString();
  }

  byte carClass = 0;
  public static boolean isRunning = false;
  byte traffic = 0;
  byte defaultClass = 0;

//  @BindView(R.id.imgCallQuality)
//  ImageView imgCallQuality;

  @OnClick(R.id.btnSubmit)
  void onPressSubmit() {

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
    if (edtOrigin.getText().toString().isEmpty()) {
      edtOrigin.setError(" مبدا را مشخص کنید");
      edtOrigin.requestFocus();
      return;
    }
    if (edtDestination.getText().toString().isEmpty()) {
      edtDestination.setError(" مقصد را مشخص کنید");
      edtDestination.requestFocus();
      return;
    }

    getCheckOriginStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(edtOrigin.getText().toString())));

    //TODO Two line change comment
    /***********************************/
//    getCheckDestStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(edtDestination.getText().toString())));
    isDestinationValid = true;
    /***********************************/
    String mobile = isTellValidable && getMobileNumber().isEmpty() ? "0" : getMobileNumber();
    String tell = getTellNumber();
    String name = edtFamily.getText().toString();
    String address = edtAddress.getText().toString();
    String fixedComment = txtDescription.getText().toString();
    int stationCode = Integer.parseInt(edtOrigin.getText().toString());
    int destinationStation = Integer.parseInt(edtDestination.getText().toString());

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

    if (isOriginValid && isDestinationValid) {
      new GeneralDialog()
              .title("ثبت اطلاعات")
              .message("آیا از ثبت اطلاعات اطمینان دارید؟")
              .firstButton("بله", () ->
                      insertService(MyApplication.prefManager.getUserCode(), serviceCount, tell, mobile, cityCode, stationCode,
                              name, address, fixedComment, destinationStation,
                              stationName, serviceType, carClass, normalDescription, traffic, defaultClass))
              .secondButton("خیر", new Runnable() {
                @Override
                public void run() {
                }
              })
              .show();
    }
  }

  @OnClick(R.id.btnOptions)
  void onPressOptions() {
    KeyBoardHelper.hideKeyboard();
    new OptionDialog().show(new OptionDialog.Listener() {
      @Override
      public void onClose(boolean b) {
        if (b) {
          clearData();
        }
      }
    }, getMobileNumber(), edtFamily.getText().toString(), cityCode);
  }

  @BindView(R.id.edtDestination)
  EditText edtDestination;

  @BindView(R.id.vfPassengerAddress)
  ViewFlipper vfPassengerAddress;

  @BindView(R.id.txtSpError)
  TextView txtSpError;

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

  @OnClick(R.id.llDownload)
  void onPressDownload() {
    if (getTellNumber().isEmpty()) {
      edtTell.setError("شماره تلفن را وارد نمایید");
      edtTell.requestFocus();
      return;
    }
    if (getMobileNumber().isEmpty() && !isTellValidable) {
      edtMobile.setError("شماره تلفن همراه را وارد نمایید");
      edtMobile.requestFocus();
      return;
    }

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
        core.addListener(mCoreListener);
      }

      @Override
      public void onCallReceived() {
        showCallIncoming();
      }
    });

//    Call call = LinphoneService.getCore().getCurrentCall();
//    call.terminate();
  }

  @BindView(R.id.rlNewInComingCall)
  RelativeLayout rlNewInComingCall;

  @BindView(R.id.rlActionBar)
  RelativeLayout rlActionBar;

  @BindView(R.id.llDownload)
  LinearLayout llDownload;

  @BindView(R.id.llSearchAddress)
  LinearLayout llSearchAddress;

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
                setActivate(MyApplication.prefManager.getUserCode(), MyApplication.prefManager.getSipNumber());
//                MyApplication.Toast("activated",Toast.LENGTH_SHORT);
              }
            })
            .secondButton("نیستم", null)
            .show();

  }

  @OnClick(R.id.clearAddress)
  void onCLearAddress() {
    edtAddress.getText().clear();
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
                setDeActivate(MyApplication.prefManager.getUserCode(), MyApplication.prefManager.getSipNumber());
              }
            })
            .secondButton("نیستم", null)
            .show();
  }

  String permanentDesc = "";

  View view;
  private String[] countService = new String[6];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trip_register);
    view = getWindow().getDecorView();
    getSupportActionBar().hide();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
      window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    if (MyApplication.prefManager.getActivateStatus()) {
      btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
      btnDeActivate.setTextColor(Color.parseColor("#000000"));
    } else {
      btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
      btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
      btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
    }

    disableViews();

    Log.i(TAG, "AMIRREZA=> onCreate register: ");

    MyApplication.handler.postDelayed(() -> {
      initCitySpinner();
      initServiceTypeSpinner();
      initServiceCountSpinner();
    }, 200);


    edtTell.requestFocus();

    edtTell.addTextChangedListener(edtTellTextWather);

    edtMobile.addTextChangedListener(edtMobileTW);

    rgCarClass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int i) {
        CompoundButton cb = (CompoundButton) group.findViewById(i);
        chbAlways.setChecked(false);
      }
    });

    MyApplication.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        KeyBoardHelper.showKeyboard(MyApplication.context);
      }
    }, 300);


    RipplePulseLayout mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);
    mRipplePulseLayout.startRippleAnimation();
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
      if (editable.toString().length() == 1 && editable.toString().startsWith("0")) {
        editable.clear();
      }
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
        edtMobile.setText(editable.toString());
        edtMobile.setNextFocusDownId(R.id.edtMobile);
      } else {
//          clearData();
//          edtMobile.setText("");
        isTellValidable = true;
        edtFamily.setText("");
        edtAddress.setText("");
        edtOrigin.setText("");
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

  private void initServiceCountSpinner() {
    try {
      ArrayList<String> countServices = new ArrayList<>();
      for (int i = 1; i < countService.length; i++) {
        countService[i] = i + "";
        countServices.add(countService[i]);
      }
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
    }

    edtAddress.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.toString().isEmpty()) {
          edtOrigin.setText("");
        }
      }
    });
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
    }
  }

  private void initCitySpinner() {
    ArrayList<CityModel> cityModels = new ArrayList<>();
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
    }
  }

  ArrayList<PassengerAddressModel> passengerAddressModels;

  private void getPassengerInfo(String phoneNumber, String mobile, String queue) {
    if (vfPassengerInfo != null)
      vfPassengerInfo.setDisplayedChild(1);

    RequestHelper.builder(EndPoints.PASSENGER_INFO)
            .addPath(phoneNumber)
            .addPath(mobile)
            .addPath(queue)
            .listener(getPassengerInfo)
            .get();

  }

  RequestHelper.Callback getPassengerInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          if (queue.trim().equals("1817")) {
            MyApplication.handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                spServiceType.setSelection(2, true);
              }
            }, 500);
          }

          Log.i(TAG, "AMIRREZA ER: " + args[0].toString());
          JSONObject obj = new JSONObject(args[0].toString());
          boolean success = obj.getBoolean("success");
          String message = obj.getString("message");

          JSONObject dataObj = obj.getJSONObject("data");

          JSONObject statusObj = dataObj.getJSONObject("status");
          int status = statusObj.getInt("status");
          String descriptionStatus = statusObj.getString("descriptionStatus");

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

          if (success) {
            edtTell.setNextFocusDownId(R.id.edtFamily);
            isEnableView = true;
            initServiceCountSpinner();
            initServiceTypeSpinner();
            enableViews();
            spCity.setSelection(cityCode, true);
            if (cityCode == 0) {
              KeyBoardHelper.hideKeyboard();
              new CityDialog().show(new CityDialog.Listener() {
                @Override
                public void selectedCity(int position) {
                  spCity.setSelection(position + 1);
                }
              });
            }
            if (callerCode == 0) {
              txtNewPassenger.setVisibility(View.VISIBLE);
              txtLockPassenger.setVisibility(View.GONE);
              edtFamily.requestFocus();
            } else {
              switch (status) {
                case 0:
                  txtNewPassenger.setVisibility(View.GONE);
                  txtLockPassenger.setVisibility(View.GONE);
                  break;
                case 1:
                  txtNewPassenger.setVisibility(View.GONE);
                  txtLockPassenger.setVisibility(View.VISIBLE);
                  break;
              }
              edtFamily.setText(name);
              edtAddress.setText(address);
              edtOrigin.setText(staion + "");
              txtDescription.setText(permanentDesc + "");
              rgCarClass.clearCheck();
              edtDiscount.setText(discountCode);
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

          if (vfPassengerInfo != null)
            vfPassengerInfo.setDisplayedChild(0);

        } catch (JSONException e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
        if (vfPassengerInfo != null) vfPassengerInfo.setDisplayedChild(0);
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
            JSONArray dataArr = obj.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); i++) {
              JSONObject dataObj = dataArr.getJSONObject(i);
              PassengerAddressModel addressModel = new PassengerAddressModel();
              addressModel.setPhoneNumber(dataObj.getString("phoneNumber"));
              addressModel.setAddress(dataObj.getString("address"));
              addressModel.setStation(dataObj.getInt("station"));
              addressModel.setStatus(dataObj.getInt("status"));
              passengerAddressModels.add(addressModel);
            }
            if (passengerAddressModels.size() == 0) {
              if (vfPassengerAddress != null)
                vfPassengerAddress.setDisplayedChild(0);
              MyApplication.Toast("آدرسی موجود نیست", Toast.LENGTH_SHORT);
            } else {
              new AddressListDialog().show(new AddressListDialog.Listener() {
                @Override
                public void description(String address, int stationCode) {
                  edtAddress.setText(address);
                  originStationCode = stationCode;
                  edtOrigin.setText(stationCode + "");

                }
              }, passengerAddressModels);
              if (vfPassengerAddress != null)
                vfPassengerAddress.setDisplayedChild(0);
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
    }
  };

  private void getCheckOriginStation(int cityCode, int stationCode) {
    RequestHelper.builder(EndPoints.CHECK_STATION)
            .addPath(cityCode + "")
            .addPath(stationCode + "")
            .listener(getCheckOriginStation)
            .get();

  }

  RequestHelper.Callback getCheckOriginStation = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject dataObj = obj.getJSONObject("data");
            int status = dataObj.getInt("status");
            String desc = dataObj.getString("descriptionStatus");

            if (status != 0) {
              new GeneralDialog()
                      .title("منطقه مبدا")
                      .message(desc)
                      .firstButton("اصلاح میکنم", new Runnable() {
                        @Override
                        public void run() {
                          edtOrigin.requestFocus();
                        }
                      })
                      .show();
              return;
            }
            isOriginValid = true;


          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  private void getCheckDestStation(int cityCode, int stationCode) {
    RequestHelper.builder(EndPoints.CHECK_STATION)
            .addPath(cityCode + "")
            .addPath(stationCode + "")
            .listener(getCheckDestStation)
            .get();

  }

  RequestHelper.Callback getCheckDestStation = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject dataObj = obj.getJSONObject("data");
            int status = dataObj.getInt("status");
            String desc = dataObj.getString("descriptionStatus");

            if (status != 0) {
              new GeneralDialog()
                      .title("منطقه مقصد")
                      .message(desc)
                      .firstButton("اصلاح میکنم", new Runnable() {
                        @Override
                        public void run() {
                          edtDestination.requestFocus();
                        }
                      })
                      .show();
              return;
            }
            isDestinationValid = true;

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  private void getStationInfo(String stationCode) {
    LoadingDialog.makeLoader();
    RequestHelper.builder(EndPoints.STATION_INFO)
            .addPath(stationCode + "")
            .listener(getStationInfo)
            .get();

  }

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
              edtTell.setText(address.getUsername());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          AvaCrashReporter.send(e, "callIncomingActivity");
        }
      }
    }
  }

  ArrayList<StationInfoModel> stationInfoModels;

  RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            boolean isCountrySide = false;
            String stationName = "";
            LoadingDialog.dismiss();
            Log.i(TAG, "onResponse: " + args[0].toString());
            stationInfoModels = new ArrayList<>();
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONArray dataArr = obj.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); i++) {
              JSONObject dataObj = dataArr.getJSONObject(i);
              StationInfoModel stationInfoModel = new StationInfoModel();
              stationInfoModel.setStcode(dataObj.getInt("stcode"));
              stationInfoModel.setStreet(dataObj.getString("street"));
              stationInfoModel.setOdd(dataObj.getString("odd"));
              stationInfoModel.setEven(dataObj.getString("even"));
              stationInfoModel.setStationName(dataObj.getString("stationName"));
              stationInfoModel.setCountrySide(dataObj.getInt("countrySide"));
              if (dataObj.getInt("countrySide") == 1) {
                isCountrySide = true;
              } else {
                isCountrySide = false;
              }

              if (!dataObj.getString("stationName").equals("")) {
                stationName = dataObj.getString("stationName");
              }
              stationInfoModels.add(stationInfoModel);
            }
            if (stationInfoModels.size() == 0) {
              MyApplication.Toast("اطلاعاتی موجود نیست", Toast.LENGTH_SHORT);
            } else {
              if (stationName.equals("")) {
                new StationInfoDialog().show(stationInfoModels, "کد ایستگاه : " + edtOrigin.getText().toString(), isCountrySide);
              } else {
                new StationInfoDialog().show(stationInfoModels, stationName + " \n " + "کد ایستگاه : " + edtOrigin.getText().toString(), isCountrySide);
              }
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(LoadingDialog::dismiss);
    }
  };

  private void setActivate(int userId, int sipNumber) {

    LoadingDialog.makeLoader();
    RequestHelper.builder(EndPoints.ACTIVATE)
            .addParam("userId", userId)
            .addParam("sipNumber", sipNumber)
            .listener(setActivate)
            .post();

  }

  RequestHelper.Callback setActivate = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");

            if (success) {
              MyApplication.Toast("شما باموفقیت وارد صف شدید", Toast.LENGTH_SHORT);
              btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
              MyApplication.prefManager.setActivateStatus(true);
              btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
              btnDeActivate.setTextColor(Color.parseColor("#000000"));
            } else {
              new GeneralDialog()
                      .title("هشدار")
                      .message(message)
                      .firstButton("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
                          setActivate(MyApplication.prefManager.getUserCode(), MyApplication.prefManager.getSipNumber());
                        }
                      })
                      .secondButton("بعدا امتحان میکنم", null)
                      .show();
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
          LoadingDialog.dismiss();

        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(LoadingDialog::dismiss);
    }
  };

  private void setDeActivate(int userId, int sipNumber) {

    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
      params.put("sipNumber", sipNumber);

      Log.i(TAG, "setDeActivate: " + params);

      LoadingDialog.makeLoader();
      RequestHelper.builder(EndPoints.DEACTIVATE)
              .addParam("userId", userId)
              .addParam("sipNumber", sipNumber)
              .listener(setDeActivate)
              .post();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  RequestHelper.Callback setDeActivate = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");

            if (success) {
              MyApplication.Toast("شما باموفقیت از صف خارج شدید", Toast.LENGTH_SHORT);
              MyApplication.prefManager.setActivateStatus(false);
              btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
              btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
              btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
            } else {
              new GeneralDialog()
                      .title("هشدار")
                      .message(message)
                      .firstButton("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
                          setDeActivate(MyApplication.prefManager.getUserCode(), MyApplication.prefManager.getSipNumber());
                        }
                      })
                      .secondButton("بعدا امتحان میکنم", null)
                      .show();
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
          LoadingDialog.dismiss();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(LoadingDialog::dismiss);

    }
  };

  private void insertService(int userId, int count, String phoneNumber, String mobile, int cityCode, int stationCode, String callerName,
                             String address, String fixedComment, int destinationStation, String destination, int typeService,
                             int classType, String description, int TrafficPlan, int defaultClass) {


    LoadingDialog.makeLoader();
    RequestHelper.builder(EndPoints.INSERT)
            .addParam("userId", userId)
            .addParam("count", count)
            .addParam("phoneNumber", phoneNumber)
            .addParam("mobile", mobile)
            .addParam("cityCode", cityCode)
            .addParam("stationCode", stationCode)
            .addParam("callerName", callerName)
            .addParam("address", address)
            .addParam("fixedComment", fixedComment)
            .addParam("destinationStation", destinationStation)
            .addParam("destination", destination)
            .addParam("typeService", typeService)
            .addParam("classType", classType)
            .addParam("description", description)
            .addParam("TrafficPlan", TrafficPlan)
            .addParam("voipId", voipId)
            .addParam("defaultClass", defaultClass)
            .addParam("queue", queue)
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

            Log.i(TAG, "onResponse: " + args[0].toString());
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            if (success) {

              new GeneralDialog()
                      .title("ثبت شد")
                      .message("اطلاعات با موفقیت ثبت شد")
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
              svTripRegister.scrollTo(0, 0);
            } else {
              new GeneralDialog()
                      .title("خطا")
                      .message(message)
                      .firstButton("تلاش مجدد", null)
//
//                        String tell = edtTell.getText().toString();
//                        String mobile = edtMobile.getText().toString();
//                        String name = edtFamily.getText().toString();
//                        String address = edtAddress.getText().toString();
//                        String fixedComment = txtDescription.getText().toString();
//                        int stationCode = Integer.parseInt(edtOrigin.getText().toString());
//                        int destinationStation = Integer.parseInt(edtDestination.getText().toString());
//
//                        insertService(MyApplication.prefManager.getUserCode(), serviceCount, tell, mobile, cityCode, stationCode,
//                                name, address, fixedComment, destinationStation,
//                                stationName, serviceType, carClass, normalDescription, traffic, 1, defaultClass);

                      .secondButton("بستن", null)
                      .show();
            }
            LoadingDialog.dismiss();

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(LoadingDialog::dismiss);
    }
  };

  private void clearData() {
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
    edtOrigin.setText("");
    edtDestination.setText("");
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
    edtOrigin.setEnabled(true);
    edtDestination.setEnabled(true);
    chbTraffic.setEnabled(true);
    llTrafficBg.setEnabled(true);
    chbAlways.setEnabled(true);
    llSearchAddress.setEnabled(true);
    llSearchOrigin.setEnabled(true);
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
    llSearchBg.setEnabled(true);
    spServiceType.setEnabled(true);

  }

  private void disableViews() {
    edtFamily.setEnabled(false);
//    edtDiscount.setEnabled(false);
    edtAddress.setEnabled(false);
    edtOrigin.setEnabled(false);
    edtDestination.setEnabled(false);
    txtDescription.setEnabled(false);
    chbTraffic.setEnabled(false);
    llTrafficBg.setEnabled(false);
    chbAlways.setEnabled(false);
    llSearchAddress.setEnabled(false);
    llSearchOrigin.setEnabled(false);
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
    llSearchBg.setEnabled(false);
    rgCarClass.setEnabled(false);
    for (int i = 0; i < rgCarClass.getChildCount(); i++) {
      rgCarClass.getChildAt(i).setEnabled(false);
    }


  }

  @Override
  protected void onResume() {
    super.onResume();
    MyApplication.currentActivity = this;
    showTitleBar();
    if (MyApplication.prefManager.getConnectedCall()) {
      startCallQuality();
      imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);

      Call[] calls = core.getCalls();
      for (Call call : calls) {
        if (call != null && call.getState() == Call.State.StreamsRunning) {
          if (voipId.equals("0")) {
            Address address = call.getRemoteAddress();
            edtTell.setText(address.getUsername());
          }
        }
      }
    }
    ;

//    Call call = core.getCurrentCall();
//    if (call != null) {
//      startCallQuality();
//    }
  }

  //receive push notification from local broadcast
  BroadcastReceiver pushReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String result = intent.getStringExtra(Keys.KEY_MESSAGE);
      handleCallerInfo(parseNotification(result));
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
        edtTell.setText(participant);
      }
    } catch (Exception e) {
      e.printStackTrace();
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
      JSONObject message = new JSONObject(strMessage);
      CallModel callModel = new CallModel();
      callModel.setType(message.getString("type"));
      callModel.setExten(message.getInt("exten"));
      callModel.setParticipant(message.getString("participant"));
      callModel.setQueue(message.getString("queue"));
      callModel.setVoipId(message.getString("voipId"));
      return callModel;
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  private Runnable mCallQualityUpdater = null;
  private int mDisplayedQuality = -1;

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
    } else { // Worst quality
      imageRes = (R.drawable.ic_quality_0);
    }
    try {
      imgCallQuality.setVisibility(View.VISIBLE);
      imgCallQuality.setImageResource(imageRes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mDisplayedQuality = iQuality;
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (pushReceiver != null)
      unregisterReceiver(pushReceiver);

  }

  @Override
  protected void onPause() {
    super.onPause();
    KeyBoardHelper.hideKeyboard();
    isRunning = false;
  }

  @BindView(R.id.imgEndCall)
  ImageView imgEndCall;


  CoreListenerStub mCoreListener = new CoreListenerStub() {
    @Override
    public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
      TripRegisterActivity.this.call = call;

      if (state == Call.State.IncomingReceived) {
        showCallIncoming();
      }
      else if (state == Call.State.Released) {
        imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
        showTitleBar();
        if (mCallQualityUpdater != null) {
          LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
          mCallQualityUpdater = null;
        }
      }
      else if (state == Call.State.Connected) {
        startCallQuality();
        imgEndCall.setColorFilter(ContextCompat.getColor(MyApplication.context, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
        Address address = call.getRemoteAddress();
        if (voipId.equals("0"))
          edtTell.setText(address.getUsername());
        showTitleBar();
      }
      else if (state == Call.State.Error) {
        showTitleBar();
      }
      else if (state == Call.State.End) {
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
    LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((pushReceiver),
            new IntentFilter(Keys.KEY_BROADCAST_PUSH));

    core = LinphoneService.getCore();
    core.addListener(mCoreListener);
    isRunning = true;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    core.removeListener(mCoreListener);

  }

  @Override
  public void onBackPressed() {
    KeyBoardHelper.hideKeyboard();
    new GeneralDialog()
            .title("خروج")
            .message("آیا از خروج خود اطمینان دارید؟")
            .firstButton("بله", new Runnable() {
              @Override
              public void run() {
                try {
                  Call[] calls = core.getCalls();
                  for (Call call : calls) {
                    if (call != null && call.getState() == Call.State.IncomingReceived) {
                      createNotification();
                    }
                  }
                  startActivity(new Intent(MyApplication.context, MainActivity.class));
                  finish();
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            })
            .secondButton("خیر", null)
            .show();
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
    mNotificationManager.notify(notifManagerId, mBuilder.build());

  }

  @OnClick(R.id.imgAccept)
  void onAcceptPress() {
    call = core.getCurrentCall();
    Call[] calls = core.getCalls();
    int i = calls.length;
    Log.i(TAG, "onRejectPress: " + i);
    if (call != null)
      call.accept();
    else if (calls.length > 0) {
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

  Core core;
  Call call;

  private void showCallIncoming() {
    call = core.getCurrentCall();
    Address address = call.getRemoteAddress();
    txtCallerNum.setText(address.getUsername());
    rlNewInComingCall.setVisibility(View.VISIBLE);
    rlActionBar.setVisibility(View.GONE);
  }

  private void showTitleBar() {
    rlNewInComingCall.setVisibility(View.GONE);
    rlActionBar.setVisibility(View.VISIBLE);
    SoundHelper.stop();
  }

}
