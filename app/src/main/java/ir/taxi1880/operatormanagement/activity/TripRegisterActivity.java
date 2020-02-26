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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.AddressListDialog;
import ir.taxi1880.operatormanagement.dialog.DescriptionDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.SearchLocationDialog;
import ir.taxi1880.operatormanagement.helper.CheckEmptyView;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

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
  new AddressListDialog().show(new AddressListDialog.Listener() {
    @Override
    public void description(String address) {
      edtAddress.setText(address);
    }
  });
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
              }
            }).secondButton("خیر", null)
    .show();
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
