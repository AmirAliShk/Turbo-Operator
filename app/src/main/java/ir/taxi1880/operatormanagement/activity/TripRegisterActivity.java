package ir.taxi1880.operatormanagement.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.MultiRowsRadioGroup;
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.OptionDialog;
import ir.taxi1880.operatormanagement.dialog.SearchLocationDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;

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

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
//    KeyBoardHelper.hideKeyboard();
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

  @BindView(R.id.spServiceType)
  Spinner spServiceType;

  @BindView(R.id.edtOrigin)
  EditText edtOrigin;

  @BindView(R.id.edtDiscount)
  EditText edtDiscount;

  @BindView(R.id.edtTell)
  EditText edtTell;

  @BindView(R.id.edtMobile)
  EditText edtMobile;

  @BindView(R.id.edtFamily)
  EditText edtFamily;

  @BindView(R.id.edtAddress)
  EditText edtAddress;

  @BindView(R.id.llSearchOrigin)
  LinearLayout llSearchOrigin;

  @BindView(R.id.llSearchDestination)
  LinearLayout llSearchDestination;

  @OnClick(R.id.llSearchOrigin)
  void onOrigin() {
    new SearchLocationDialog().show(new SearchLocationDialog.Listener() {
      @Override
      public void description(String address, int code) {
        edtOrigin.setText(code + "");
      }
    }, "جست و جوی مبدا", cityLatinName);
  }

  @OnClick(R.id.llSearchDestination)
  void onDestination() {
    new SearchLocationDialog().show(new SearchLocationDialog.Listener() {
      @Override
      public void description(String address, int code) {
        edtDestination.setText(code + "");
        stationName = address;
        Log.i(TAG, "description: " + address);
      }
    }, "جست و جوی مقصد", cityLatinName);
  }

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
    if (edtMobile.getText().toString().isEmpty()) {
      MyApplication.Toast("ابتدا شماره موبایل را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    getPassengerAddress(StringHelper.toEnglishDigits(edtMobile.getText().toString()));
  }

  byte carClass = 0;
  byte traffic = 0;
  byte defaultClass = 0;
  byte activateStatus = -1;

  @OnClick(R.id.btnSubmit)
  void onPressSubmit() {

    if (edtTell.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره تلفن را وارد کنید", Toast.LENGTH_SHORT);
      edtTell.requestFocus();
      return;
    }
    if (edtMobile.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره همراه را وارد کنید", Toast.LENGTH_SHORT);
      edtMobile.requestFocus();
      return;
    }
    if (edtFamily.getText().toString().isEmpty()) {
      MyApplication.Toast(" نام مسافر را مشخص کنید", Toast.LENGTH_SHORT);
      edtFamily.requestFocus();
      return;
    }
    if (edtAddress.getText().toString().isEmpty()) {
      MyApplication.Toast("آدرس را مشخص کنید", Toast.LENGTH_SHORT);
      edtAddress.requestFocus();
      return;
    }
    if (edtOrigin.getText().toString().isEmpty()) {
      MyApplication.Toast(" مبدا را مشخص کنید", Toast.LENGTH_SHORT);
      edtOrigin.requestFocus();
      return;
    }
    if (edtDestination.getText().toString().isEmpty()) {
      MyApplication.Toast(" مقصد را مشخص کنید", Toast.LENGTH_SHORT);
      edtDestination.requestFocus();
      return;
    }

    getCheckOriginStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(edtOrigin.getText().toString())));

    getCheckDestStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(edtDestination.getText().toString())));

    String mobile;

    if (edtMobile.getText().toString().startsWith("0")) {
      mobile = edtMobile.getText().toString().substring(1, 10);
    } else {
      mobile = edtMobile.getText().toString();
    }

    String tell = edtTell.getText().toString();
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
                              stationName, serviceType, carClass, normalDescription, traffic, 1, defaultClass))
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
    new OptionDialog().show();
  }

  @BindView(R.id.edtDestination)
  EditText edtDestination;

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

  @OnClick(R.id.llDownload)
  void onPressDownload() {
    if (edtTell.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره تلفن را وارد نمایید", Toast.LENGTH_SHORT);
      edtTell.requestFocus();
      return;
    }
    if (edtMobile.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره تلفن همراه را وارد نمایید", Toast.LENGTH_SHORT);
      edtMobile.requestFocus();
      return;
    }
    getPassengerInfo(StringHelper.toEnglishDigits(edtTell.getText().toString()), StringHelper.toEnglishDigits(edtMobile.getText().toString()), StringHelper.toEnglishDigits("1880"));
  }

  @OnClick(R.id.llEndCall)
  void onPressEndCall() {

    KeyBoardHelper.hideKeyboard();
    new CallDialog().show(new CallDialog.Listener() {
      @Override
      public void onClose(boolean b) {
        if (b) {
          clearData();
        }
      }
    });
//    Call call = LinphoneService.getCore().getCurrentCall();
//    call.terminate();
  }

  @BindView(R.id.swActivateStatus)
  Switch swActivateStatus;

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

//  @BindView(R.id.rbActivate)
//  RadioButton rbActivate;
//
//  @BindView(R.id.rbDeActivate)
//  RadioButton rbDeActivate;

  private boolean serviceTypeFlag = false;
  private boolean cityFlag = false;
  private boolean serviceCountFlag = false;
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

    disableViews();

    initCitySpinner();
    initServiceTypeSpinner();
    initServiceCountSpinner();

    edtTell.requestFocus();

//    swActivateStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//      @Override
//      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//        if (b){
//          new GeneralDialog()
//                  .title("هشدار")
//                  .cancelable(false)
//                  .message("مطمئنی میخوای وارد صف بشی؟")
//                  .firstButton("مطمئنم", new Runnable() {
//                    @Override
//                    public void run() {
//                      MyApplication.Toast("شما وارد صف شدید",Toast.LENGTH_SHORT);
//                    }
//                  })
//                  .secondButton("نیستم", new Runnable() {
//                    @Override
//                    public void run() {
//                      swActivateStatus.setChecked(false);
//                    }
//                  })
//                  .show();
//        }else {
//          new GeneralDialog()
//                  .title("هشدار")
//                  .cancelable(false)
//                  .message("مطمئنی میخوای خارج بشی؟")
//                  .firstButton("مطمئنم", new Runnable() {
//                    @Override
//                    public void run() {
//                      MyApplication.Toast("شما خارج شدید",Toast.LENGTH_SHORT);
//                    }
//                  })
//                  .secondButton("نیستم", new Runnable() {
//                    @Override
//                    public void run() {
//                      swActivateStatus.setChecked(true);
//                    }
//                  })
//                  .show();
//        }
//      }
//    });

    edtTell.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (charSequence.toString().isEmpty()) {
//        spCity.setSelection(0);
        isEnableView = false;
        disableViews();
        initServiceCountSpinner();
        initServiceTypeSpinner();
//        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (PhoneNumberValidation.isValid(editable.toString())) {
          edtMobile.setText(editable.toString());
          edtTell.setNextFocusDownId(R.id.edtFamily);
        } else {
          edtMobile.setText("");
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
        }
      }
    });

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

  }

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
      for (int i = 0; i < cityArr.length(); i++) {
        JSONObject cityObj = cityArr.getJSONObject(i);
        CityModel cityModel = new CityModel();
        cityModel.setCity(cityObj.getString("cityname"));
        cityModel.setId(cityObj.getInt("cityid"));
        cityModel.setCityLatin(cityObj.getString("latinName"));
        cityModels.add(cityModel);
        cityList.add(cityObj.getString("cityname"));
      }
      spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
      spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          cityName = cityModels.get(position).getCity();
          cityLatinName = cityModels.get(position).getCityLatin();
          cityCode = cityModels.get(position).getId();
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
    vfPassengerInfo.setDisplayedChild(1);

    RequestHelper.builder(EndPoints.PASSENGER_INFO + "/" + phoneNumber + "/" + mobile + "/" + queue)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getPassengerInfo)
            .request();

  }

  RequestHelper.Callback getPassengerInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            vfPassengerInfo.setDisplayedChild(0);
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

            if (success) {
              isEnableView = true;
              initServiceCountSpinner();
              initServiceTypeSpinner();
              enableViews();
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

  private void getPassengerAddress(String phoneNumber) {
    vfPassengerAddress.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.PASSENGER_ADDRESS + "/" + phoneNumber)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getPassengerAddress)
            .request();

  }

  RequestHelper.Callback getPassengerAddress = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
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
              vfPassengerAddress.setDisplayedChild(0);
            } else {
              new AddressListDialog().show(new AddressListDialog.Listener() {
                @Override
                public void description(String address, int stationCode) {
                  edtAddress.setText(address);
                  originStationCode = stationCode;
                  edtOrigin.setText(stationCode + "");
                }
              }, passengerAddressModels);
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
    RequestHelper.builder(EndPoints.CHECK_STATION + "/" + cityCode + "/" + stationCode)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getCheckOriginStation)
            .request();

  }

  RequestHelper.Callback getCheckOriginStation = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
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
    RequestHelper.builder(EndPoints.CHECK_STATION + "/" + cityCode + "/" + stationCode)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getCheckDestStation)
            .request();

  }

  RequestHelper.Callback getCheckDestStation = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
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

  private void getStationInfo(int cityCode) {
    RequestHelper.builder(EndPoints.STATION_INFO + "/" + cityCode)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getStationInfo)
            .request();

  }

  RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject dataObj = obj.getJSONObject("data");
            int id = dataObj.getInt("id");
            int stcode = dataObj.getInt("stcode");
            String street = dataObj.getString("street");
            String odd = dataObj.getString("odd");
            String even = dataObj.getString("even");
            String stationName = dataObj.getString("stationName");
            long lat = dataObj.getLong("lat");
            long lng = dataObj.getLong("lng");
            int cityCode = dataObj.getInt("cityCode");
            int countrySide = dataObj.getInt("countrySide");

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

  private void setActivate(int userId, int sipNumber) {

    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
      params.put("sipNumber", sipNumber);

      RequestHelper.builder(EndPoints.ACTIVATE)
              .method(RequestHelper.POST)
              .params(new JSONObject())
              .listener(setActivate)
              .request();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  RequestHelper.Callback setActivate = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");

            if (success) {
              MyApplication.Toast("باموفقیت وارد صف شدید", Toast.LENGTH_SHORT);
            } else {
              new GeneralDialog()
                      .title("هشدار")
                      .message(message)
                      .firstButton("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
//                          setActivate(MyApplication.prefManager.getUserCode(),MyApplication.prefManager.getSipNumber());
                        }
                      })
                      .secondButton("بعدا امتحان میکنم", null)
                      .show();
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

  private void setDeActivate(int userId, int sipNumber) {

    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
      params.put("sipNumber", sipNumber);

      RequestHelper.builder(EndPoints.DEACTIVATE)
              .method(RequestHelper.POST)
              .params(new JSONObject())
              .listener(setDeActivate)
              .request();
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
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");

            if (success) {
              MyApplication.Toast("باموفقیت از صف خارج شدید", Toast.LENGTH_SHORT);
            } else {
              new GeneralDialog()
                      .title("هشدار")
                      .message(message)
                      .firstButton("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
//                          setDeActivate(MyApplication.prefManager.getUserCode(),MyApplication.prefManager.getSipNumber());
                        }
                      })
                      .secondButton("بعدا امتحان میکنم", null)
                      .show();
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

  private void insertService(int userId, int count, String phoneNumber, String mobile, int cityCode, int stationCode, String callerName,
                             String address, String fixedComment, int destinationStation, String destination, int typeService,
                             int classType, String description, int TrafficPlan, int voipId, int defaultClass) {
    JSONObject params = new JSONObject();

    try {
      params.put("userId", userId);
      params.put("count", count);
      params.put("phoneNumber", phoneNumber);
      params.put("mobile", mobile);
      params.put("cityCode", cityCode);
      params.put("stationCode", stationCode);
      params.put("callerName", callerName);
      params.put("address", address);
      params.put("fixedComment", fixedComment);
      params.put("destinationStation", destinationStation);
      params.put("destination", destination);
      params.put("typeService", typeService);
      params.put("classType", classType);
      params.put("description", description);
      params.put("TrafficPlan", TrafficPlan);
      params.put("voipId", voipId);
      params.put("defaultClass", defaultClass);

      Log.i(TAG, "insertService: " + params);

      RequestHelper.builder(EndPoints.INSERT)
              .method(RequestHelper.POST)
              .params(params)
              .listener(insertService)
              .request();

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  RequestHelper.Callback insertService = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            if (success) {
              new GeneralDialog()
                      .title("ثبت شد")
                      .message("اطلاعات با موفقیت ثبت شد")
                      .firstButton("باشه", () -> {
                        KeyBoardHelper.hideKeyboard();
                        MyApplication.currentActivity.onBackPressed();
                      })
                      .show();
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

  private void clearData() {
    isEnableView = false;
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
  }

  private void enableViews() {
    edtFamily.setEnabled(true);
    edtDiscount.setEnabled(true);
    edtAddress.setEnabled(true);
    edtOrigin.setEnabled(true);
    edtDestination.setEnabled(true);
    chbTraffic.setEnabled(true);
    chbAlways.setEnabled(true);
    llSearchAddress.setEnabled(true);
    llSearchOrigin.setEnabled(true);
    llSearchDestination.setEnabled(true);
    llDescriptionDetail.setEnabled(true);
    llServiceType.setEnabled(true);
    llTraffic.setEnabled(true);
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
    edtDiscount.setEnabled(false);
    edtAddress.setEnabled(false);
    edtOrigin.setEnabled(false);
    edtDestination.setEnabled(false);
    chbTraffic.setEnabled(false);
    chbAlways.setEnabled(false);
    llSearchAddress.setEnabled(false);
    llSearchOrigin.setEnabled(false);
    llSearchDestination.setEnabled(false);
    llDescriptionDetail.setEnabled(false);
    llServiceType.setEnabled(false);
    llTraffic.setEnabled(false);
    llAlways.setEnabled(false);
    llServiceCount.setEnabled(false);
    llDiscount.setEnabled(false);
    llFamily.setEnabled(false);
    llAddress.setEnabled(false);
    spServiceCount.setEnabled(false);
    spServiceType.setEnabled(false);
    for (int i = 0; i < rgCarClass.getChildCount(); i++) {
      rgCarClass.getChildAt(i).setEnabled(false);
    }


  }

  @Override
  protected void onResume() {
    super.onResume();
    MyApplication.currentActivity = this;
  }

  @Override
  protected void onStart() {
    super.onStart();
    MyApplication.currentActivity = this;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
//    KeyBoardHelper.hideKeyboard();
  }

  @Override
  public void onBackPressed() {
    KeyBoardHelper.hideKeyboard();
    super.onBackPressed();
  }
}
