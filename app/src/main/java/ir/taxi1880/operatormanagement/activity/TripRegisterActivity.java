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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Call;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.SearchLocationDialog;
import ir.taxi1880.operatormanagement.helper.CheckEmptyView;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class TripRegisterActivity extends AppCompatActivity {

  public static final String TAG = TripRegisterActivity.class.getSimpleName();
  Unbinder unbinder;
  //  View view;
  private String cityName;
  private String ServiceType;
  private String ServiceCount;
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
      public void description(String address) {
        edtOrigin.setText(address);
      }
    }, "جست و جوی مبدا");
  }

  @OnClick(R.id.llSearchDestination)
  void onDestination() {
    new SearchLocationDialog().show(new SearchLocationDialog.Listener() {
      @Override
      public void description(String address) {
        edtDestination.setText(address);
      }
    }, "جست و جوی مقصد");
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

  @OnClick(R.id.llDescriptionDetail)
  void onPressLlDescriptionDetail() {
    new DescriptionDialog().show(new DescriptionDialog.Listener() {
      @Override
      public void description(String description) {
        edtDescription.setText(description);
      }
    }, edtDescription.getText().toString());
  }

  @OnClick(R.id.llSearchAddress)
  void onPressSearchAddress() {
    if (edtMobile.getText().toString().isEmpty()) {
      MyApplication.Toast("ابتدا شماره موبایل را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    getPassengerAddress(edtMobile.getText().toString());
  }

  @OnClick(R.id.btnSubmit)
  void onPressSubmit() {
//
//    if (edtMobile.getText().toString().isEmpty()){
//      MyApplication.Toast("شماره همراه را وارد کنید", Toast.LENGTH_SHORT);
//      return;
//    }
//    if (txtOrigin.getText().toString().isEmpty()){
//      MyApplication.Toast(" مبدا را مشخص کنید",Toast.LENGTH_SHORT);
//      return;
//    }
//    if (txtDestination.getText().toString().isEmpty()){
//      MyApplication.Toast(" مقصد را مشخص کنید",Toast.LENGTH_SHORT);
//      return;
//    }
//    if (edtAddress.getText().toString().isEmpty()){
//      MyApplication.Toast("آدرس را مشخص کنید",Toast.LENGTH_SHORT);
//      return;
//    }
    new GeneralDialog()
            .title("ثبت اطلاعات")
            .message("آیا از ثبت اطلاعات اطمینان دارید؟")
            .firstButton("بله", () ->
                    new GeneralDialog()
                            .title("ثبت شد")
                            .message("اطلاعات با موفقیت ثبت شد")
                            .firstButton("باشه", () -> {
                              //TODO check value
//                              new CheckEmptyView().setText("empty").setCheck(2).setValue(view);
                              KeyBoardHelper.hideKeyboard();
                            })
                            .show())
            .secondButton("خیر", null)
            .show();
  }

  @OnClick(R.id.btnOptions)
  void onPressOptions() {
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
                //TODO check value
                new CheckEmptyView().setText("empty").setCheck(2).setValue(view);
                Toast.makeText(MyApplication.context, "dont work currently for now :((", Toast.LENGTH_LONG).show();
              }
            }).secondButton("خیر", null)
            .show();
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
    getPassengerInfo(edtTell.getText().toString(), edtMobile.getText().toString());
  }

  @OnClick(R.id.llEndCall)
  void onPressEndCall() {
    Toast.makeText(MyApplication.context, "this item is not visible(this is test)", Toast.LENGTH_LONG).show();
    Call call = LinphoneService.getCore().getCurrentCall();
    call.terminate();

  }

  @BindView(R.id.rgStatus)
  RadioGroup rgStatus;

  @BindView(R.id.rbEnable)
  RadioButton rbEnable;

  @BindView(R.id.rbNotEnable)
  RadioButton rbNotEnable;

  @OnCheckedChanged({R.id.rbEnable, R.id.rbNotEnable})
  public void onRadioCheck(CompoundButton view, boolean ischanged) {
    switch (view.getId()) {
      case R.id.rbEnable:
        new GeneralDialog()
            .title("هشدار")
            .message("آیا از تغییر وضعیت تست اطمینان دارید؟")
            .firstButton("بله", new Runnable() {
              @Override
              public void run() {
                rbEnable.setChecked(ischanged);
              }
            })
            .secondButton("خیر",null)
            .show();

//        if (ischanged) {
//          MyApplication.Toast("rbEnable", Toast.LENGTH_SHORT);
//        }
        break;
      case R.id.rbNotEnable:
        if (ischanged) {
          MyApplication.Toast("rbNotEnable", Toast.LENGTH_SHORT);
        }
        break;
      default:
        break;
    }
  }

  private boolean serviceTypeFlag = false;
  private boolean cityFlag = false;
  private boolean serviceCountFlag = false;

  private String city = "[{\"name\":\"انتخاب شهر\"},{\"name\":\"مشهد\"},{\"name\":\"نیشابور\"},{\"name\":\"حیدریه\"},{\"name\":\"جام\"},{\"name\":\"گناباد\"}," +
          "{\"name\":\"کاشمر\"},{\"name\":\"تایباد\"}]";

  private String serviceType = "[{\"name\":\"سرویس\"},{\"name\":\"دراختیار\"},{\"name\":\"بانوان\"}]";

  private String serviceCount = "[{\"name\":\"1\"},{\"name\":\"2\"},{\"name\":\"3\"},{\"name\":\"4\"},{\"name\":\"5\"}]";
  View view;

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

    edtTell.requestFocus();
    KeyBoardHelper.showKeyboard(MyApplication.context);

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
        Log.i(TAG, "afterTextChanged: Hiiiiiiiiiiiii" + editable.toString());
        if (PhoneNumberValidation.isValid(editable.toString())) {
          edtMobile.setText(editable.toString());
          edtTell.setNextFocusDownId(R.id.edtFamily);
        } else {
          edtMobile.setText("");
          edtTell.setNextFocusDownId(R.id.edtMobile);
        }
      }
    });

  }

  private void initServiceCountSpinner() {
    ArrayList<String> serviceCountList = new ArrayList<String>();
    try {
      JSONArray serviceCountArr = new JSONArray(serviceCount);
      for (int i = 0; i < serviceCountArr.length(); i++) {
        JSONObject serviceCountObj = serviceCountArr.getJSONObject(i);
        serviceCountList.add(serviceCountObj.getString("name"));
      }
      spServiceCount.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceCountList));
      spServiceCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          ServiceCount = spServiceCount.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void initServiceTypeSpinner() {
    ArrayList<String> serviceList = new ArrayList<String>();
    try {
      JSONArray serviceArr = new JSONArray(serviceType);
      for (int i = 0; i < serviceArr.length(); i++) {
        JSONObject serviceObj = serviceArr.getJSONObject(i);
        serviceList.add(serviceObj.getString("name"));
      }
      spServiceType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

      spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//          if (!serviceTypeFlag) {
          ServiceType = spServiceType.getSelectedItem().toString();
//            serviceTypeFlag = true;
//          } else {
//            openKeyBoaredAuto();
//            edtFamily.requestFocus();
//            spServiceType.clearFocus();
//          }
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
    ArrayList<String> cityList = new ArrayList<String>();
    try {
      JSONArray cityArr = new JSONArray(city);
      for (int i = 0; i < cityArr.length(); i++) {
        JSONObject cityObj = cityArr.getJSONObject(i);
        cityList.add(cityObj.getString("name"));
      }
      spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
      spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//          if (!cityFlag) {
          cityName = spCity.getSelectedItem().toString();
//            cityFlag = true;
//          } else {
//            openKeyBoaredAuto();
//            edtTell.requestFocus();
//            spCity.clearFocus();
//          }
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

  private void getPassengerAddress(String phoneNumber) {
    vfPassengerAddress.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.PASSENGER_ADDRESS + "/" + phoneNumber)
            .method(RequestHelper.GET)
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
            for (int i = 0; i <= dataArr.length(); i++) {
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
                public void description(String address) {
                  edtAddress.setText(address);
                }
              }, passengerAddressModels);
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

  private void getPassengerInfo(String phoneNumber, String mobile) {
    vfPassengerInfo.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.PASSENGER_INFO + "/" + phoneNumber + "/" + mobile)
            .method(RequestHelper.GET)
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
            String description = passengerInfoObj.getString("description");
            String discountCode = passengerInfoObj.getString("discountCode");
            int discountId = passengerInfoObj.getInt("discountId");

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

  private void getCheckStation(int stationCode) {
    vfPassengerInfo.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.CHECK_STATION + "/" + stationCode)
            .method(RequestHelper.GET)
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
            vfPassengerInfo.setDisplayedChild(0);
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject dataObj = obj.getJSONObject("data");
            int status = dataObj.getInt("status");
            String descriptionStatus = dataObj.getString("descriptionStatus");

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
    vfPassengerInfo.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.STATION_INFO + "/" + cityCode)
            .method(RequestHelper.GET)
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
            vfPassengerInfo.setDisplayedChild(0);
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
