package ir.taxi1880.operatormanagement.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

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
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.SearchLocationDialog;
import ir.taxi1880.operatormanagement.fragment.HireDriverFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
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
  public static InputMethodManager inputMethodManager;


  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
//    KeyBoardHelper.hideKeyboard();
  }

  @BindView(R.id.spCity)
  Spinner spCity;

  @BindView(R.id.edtDescription)
  EditText edtDescription;

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

  @OnClick(R.id.llDescription)
  void onPressllDescription() {
    edtDescription.requestFocus();
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
  MultiLineRadioGroup rgCarClass;
//  MultiRadioGroup rgCarClass;

  @OnClick(R.id.llDescriptionDetail)
  void onPressLlDescriptionDetail() {
    new DescriptionDialog().show(new DescriptionDialog.Listener() {
      @Override
      public void description(String description) {
        normalDescription = description;
      }

      @Override
      public void fixedDescription(String fixedDescription) {
        edtDescription.setText(fixedDescription);
      }
    }, edtDescription.getText().toString(), normalDescription);
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
      return;
    }
    if (edtOrigin.getText().toString().isEmpty()) {
      MyApplication.Toast(" مبدا را مشخص کنید", Toast.LENGTH_SHORT);
      return;
    }
    if (edtDestination.getText().toString().isEmpty()) {
      MyApplication.Toast(" مقصد را مشخص کنید", Toast.LENGTH_SHORT);
      return;
    }
    if (edtFamily.getText().toString().isEmpty()) {
      MyApplication.Toast(" نام مسافر را مشخص کنید", Toast.LENGTH_SHORT);
      return;
    }
    if (edtAddress.getText().toString().isEmpty()) {
      MyApplication.Toast("آدرس را مشخص کنید", Toast.LENGTH_SHORT);
      return;
    }
    String mobile;

    if (edtMobile.getText().toString().startsWith("0")) {
      mobile = edtMobile.getText().toString().substring(1, 10);
    } else {
      mobile = edtMobile.getText().toString();
    }

    String tell = edtTell.getText().toString();
    String name = edtFamily.getText().toString();
    String address = edtAddress.getText().toString();
    String fixedComment = edtDescription.getText().toString();
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
                Log.i(TAG, "run: " + carClass);
              }
            })
            .show();
  }

  @OnClick(R.id.btnOptions)
  void onPressOptions() {

    FragmentHelper
            .toFragment(MyApplication.currentActivity, new HireDriverFragment())
            .replace();
  }

  @BindView(R.id.edtDestination)
  EditText edtDestination;

  @BindView(R.id.vfPassengerAddress)
  ViewFlipper vfPassengerAddress;

  @BindView(R.id.vfPassengerInfo)
  ViewFlipper vfPassengerInfo;

  @OnClick(R.id.llClear)
  void onClear() {
    MyApplication.Toast("میدونم صفحه باید خالی بشه ولی به زودی درستش میکنم :))", Toast.LENGTH_SHORT);

//    new GeneralDialog()
//            .title("هشدار")
//            .message("آیا از پاک کردن اطلاعات اطمینان دارید؟")
//            .firstButton("بله", new Runnable() {
//              @Override
//              public void run() {
//                //TODO check value
//                new CheckEmptyView().setText("empty").setCheck(2).setValue(view);
//                Toast.makeText(MyApplication.context, "dont work currently for now :((", Toast.LENGTH_LONG).show();
//              }
//            }).secondButton("خیر", null)
//            .show();
  }

  @OnClick(R.id.llDownload)
  void onPressDownload() {
    if (edtTell.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره تلفن را وارد نمایید", Toast.LENGTH_SHORT);
      return;
    }
    if (edtMobile.getText().toString().isEmpty()) {
      MyApplication.Toast("شماره تلفن همراه را وارد نمایید", Toast.LENGTH_SHORT);
      return;
    }
    getPassengerInfo(StringHelper.toEnglishDigits(edtTell.getText().toString()), StringHelper.toEnglishDigits(edtMobile.getText().toString()));
  }

  @OnClick(R.id.llEndCall)
  void onPressEndCall() {

    new CallDialog().show();
//    Call call = LinphoneService.getCore().getCurrentCall();
//    call.terminate();
  }

  @BindView(R.id.rgStatus)
  RadioGroup rgStatus;

  @BindView(R.id.rbActivate)
  RadioButton rbActivate;

  @BindView(R.id.rbDeActivate)
  RadioButton rbDeActivate;

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

    initCitySpinner();
    initServiceTypeSpinner();
    initServiceCountSpinner();

    rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {

      }
    });

    edtTell.requestFocus();

    edtTell.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
          spCity.setSelection(0);
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
        Log.i(TAG, "afterTextChanged:" + editable.toString());
        if (PhoneNumberValidation.isValid(editable.toString())) {
          edtMobile.setText(editable.toString());
          edtTell.setNextFocusDownId(R.id.edtFamily);
        } else {
          edtMobile.setText("");
          edtFamily.setText("");
          edtAddress.setText("");
          edtOrigin.setText("");
          edtDescription.setText("");
          rgCarClass.clearCheck();
          rbUnknow.setChecked(true);
          chbAlways.setChecked(false);
          edtTell.setNextFocusDownId(R.id.edtMobile);
        }
      }
    });

    edtOrigin.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
        if ((!editable.toString().equals(""))) {
          getCheckStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(editable.toString())));
        }
      }
    });

    edtDestination.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
        if ((!editable.toString().equals(""))) {
          getCheckStation(cityCode, Integer.parseInt(StringHelper.toEnglishDigits(editable.toString())));
        }
      }
    });

    rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {

        switch (i) {
          case R.id.rbActivate:
            MyApplication.Toast("rbActivate", Toast.LENGTH_SHORT);

            break;

          case R.id.rbDeActivate:
            MyApplication.Toast("rbDeActivate", Toast.LENGTH_SHORT);

            break;
        }
      }
    });

    rbActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

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

  private void getPassengerInfo(String phoneNumber, String mobile) {
    vfPassengerInfo.setDisplayedChild(1);

    RequestHelper.builder(EndPoints.PASSENGER_INFO + "/" + phoneNumber + "/" + mobile)
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

            if (!success) {
              new GeneralDialog()
                      .title("هشدار")
                      .message(message)
                      .firstButton("", null)
                      .secondButton("", null)
                      .show();
            } else {
              edtFamily.setText(name);
              edtAddress.setText(address);
              if (staion==0){
                edtOrigin.setText("") ;
              }else {
                edtOrigin.setText(staion + "");
              }
              edtDescription.setText(permanentDesc + "");

              rgCarClass.clearCheck();

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


          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      vfPassengerInfo.setDisplayedChild(0);
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

  private void getCheckStation(int cityCode, int stationCode) {
    RequestHelper.builder(EndPoints.CHECK_STATION + "/" + cityCode + "/" + stationCode)
            .method(RequestHelper.GET)
            .params(new JSONObject())
            .listener(getCheckStation)
            .request();

  }

  RequestHelper.Callback getCheckStation = new RequestHelper.Callback() {
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
            String descriptionStatus = dataObj.getString("descriptionStatus");

            if (status != 0) {
              MyApplication.Toast(descriptionStatus, Toast.LENGTH_SHORT);
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
                        //TODO check value
//                              new CheckEmptyView().setText("empty").setCheck(2).setValue(view);
                        MyApplication.Toast("میدونم صفحه باید خالی بشه ولی به زودی درستش میکنم :))", Toast.LENGTH_SHORT);
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
//                        String fixedComment = edtDescription.getText().toString();
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
