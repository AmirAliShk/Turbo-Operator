package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.PinEntryEditText;
import ir.taxi1880.operatormanagement.dataBase.TripDataBase;
import ir.taxi1880.operatormanagement.dataBase.TripModel;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.NetworkUtil;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class DeterminationPageFragment extends Fragment {
  String TAG = DeterminationPageFragment.class.getSimpleName();
  Unbinder unbinder;
  boolean pressedRefresh = false;
  boolean isEnable = false;
  ArrayList<StationInfoModel> stationInfoModels;
  ArrayList<TripModel> tripModels;
  TripDataBase tripDataBase;
  Timer timer;
  String address;
  int counter = 0;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.gridNumber)
  GridLayout gridNumber;

  @BindView(R.id.vfStationInfo)
  ViewFlipper vfStationInfo;

  @BindView(R.id.imgRefresh)
  ImageView imgRefresh;

  @BindView(R.id.txtStation)
  PinEntryEditText txtStation;

  @BindView(R.id.txtAddress)
  TextView txtAddress;

  @BindView(R.id.txtRemainingAddress)
  TextView txtRemainingAddress;

  @OnClick(R.id.btnDelete)
  void onDelete() {
    txtStation.setText("");
  }

  @OnClick(R.id.btnSubmit)
  void onSubmit() {
    Log.i(TAG, "onSubmit: " + counter);
    if (txtStation.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    if (tripModels.size() == 0 || tripDataBase.getTripRow().size() == 0) {
      MyApplication.Toast("آدرسی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
      txtStation.setText("");
      return;
    }

    setStationCode(MyApplication.prefManager.getUserCode(), tripModels.get(counter).getId(), Integer.parseInt(txtStation.getText().toString()));

  }

  @OnClick(R.id.btnHelp)
  void onHelp() {
    String origin = txtStation.getText().toString();
    if (origin.isEmpty()) {
      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    getStationInfo(origin);
  }

  @BindView(R.id.btnActivate)
  Button btnActivate;

  @BindView(R.id.btnDeActivate)
  Button btnDeActivate;

  @OnClick(R.id.llRefresh)
  void onMenu() {
    if (!MyApplication.prefManager.isStartGettingAddress()) {
      MyApplication.Toast("لطفا فعال شوید", Toast.LENGTH_SHORT);
      return;
    }
    pressedRefresh = true;
    txtStation.setText("");
    imgRefresh.startAnimation(AnimationUtils.loadAnimation(MyApplication.context, R.anim.rotate));
    MyApplication.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        getAddressList();
      }
    }, 500);
  }

  @OnClick(R.id.btnActivate)
  void onActivePress() {
    changeStatus(true);
  }

  @OnClick(R.id.btnDeActivate)
  void onDeActivePress() {

    getAddressList();
    changeStatus(false);
    //TODO show address
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_determination_page, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    tripDataBase = new TripDataBase(MyApplication.context);
    tripModels = new ArrayList<>();

    changeStatus(MyApplication.prefManager.isStartGettingAddress());

    for (int numberCount = 0; numberCount < 10; numberCount++) {
      View grid = (View) gridNumber.getChildAt(numberCount);
      int count = numberCount;
      grid.setOnClickListener(view1 -> {
        if (count == 9) {
          setNumber("0");
        } else {
          setNumber(count + 1 + "");
        }
      });
    }

    return view;
  }

  @SuppressLint("SetTextI18n")
  private void setNumber(String c) {
    String temp = txtStation.getText().toString();
    if (temp.length() == 3) {
      txtStation.setText(StringHelper.toPersianDigits(temp.substring(0, 2) + c));
    } else {
      txtStation.setText(StringHelper.toPersianDigits(temp + c));
    }
  }

  private void getAddressList() {
    if (NetworkUtil.getConnectivityStatus(MyApplication.context) == NetworkUtil.TYPE_NOT_CONNECTED) {
      imgRefresh.clearAnimation();
      return;
    }

    RequestHelper.builder(EndPoints.GET_TRIP_WITH_ZERO_STATION)
            .listener(getAddressList)
            .get();
  }

  RequestHelper.Callback getAddressList = new RequestHelper.Callback() {
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
            JSONArray dataArr = obj.getJSONArray("data");

            if (success) {

              if (pressedRefresh) {
                imgRefresh.clearAnimation();
                tripDataBase.deleteAllData();
                resetCounter();
              }

              if (dataArr.length() == 0) {
                tripDataBase.deleteAllData();
                tripModels = tripDataBase.getTripRow();
                resetCounter();
                setAddress(counter);
              } else {
                for (int i = 0; i < dataArr.length(); i++) {
                  JSONObject dataObj = dataArr.getJSONObject(i);
                  TripModel tripModel = new TripModel();
                  tripModel.setId(dataObj.getInt("Id"));
                  tripModel.setOriginStation(dataObj.getInt("OriginStation"));

                  String content = dataObj.getString("Content");
                  JSONObject contentObj = new JSONObject(content);

                  JSONArray cityArr = new JSONArray(MyApplication.prefManager.getCity());
                  for (int city = 0; city < cityArr.length(); city++) {
                    JSONObject cityObj = cityArr.getJSONObject(city);
                    if (contentObj.getInt("cityCode") == cityObj.getInt("cityid")) {
                      tripModel.setCity(cityObj.getString("cityname"));
                    }
                  }

                  tripModel.setOriginText(contentObj.getString("address"));
                  tripModel.setSaveDate(dataObj.getString("SaveDate"));
                  tripDataBase.insertTripRow(tripModel);
                }
              }

              if (tripDataBase.getTripRow().size() == 0) {
                txtAddress.setText("آدرسی موجود نیست...");
                resetCounter();
              } else {
                tripModels = tripDataBase.getTripRow();
                setAddress(counter);
              }

              if (isEnable) {
                tripModels = tripDataBase.getTripRow();
                setAddress(counter);
                isEnable = false;
              }

              if (pressedRefresh) {
                tripModels = tripDataBase.getTripRow();
                setAddress(counter);
                pressedRefresh = false;
              }

            } else {
              new GeneralDialog()
                      .title("خطا")
                      .message(message)
                      .cancelable(false)
                      .firstButton("باشه", null)
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
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          pressedRefresh = false;
          imgRefresh.clearAnimation();
          isEnable = false;
        }
      });
    }
  };

  private void changeStatus(boolean status) {
    if (status) {
      isEnable = true;
      txtRemainingAddress.setText("");
      txtAddress.setText("آدرسی موجود نیست...");
      startGetAddressTimer();
      MyApplication.prefManager.setStartGettingAddress(true);
      if (btnActivate != null)
        btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        btnDeActivate.setTextColor(Color.parseColor("#000000"));
      }
    } else {
      isEnable = false;
      txtAddress.setText("برای مشاهده آدرس ها فعال شوید");
      txtRemainingAddress.setText("");
      stopGetAddressTimer();
      MyApplication.prefManager.setStartGettingAddress(false);
      if (btnActivate != null)
        btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
        btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
      }
    }
  }

  private void getStationInfo(String stationCode) {
    if (vfStationInfo != null) {
      vfStationInfo.setDisplayedChild(1);
    }
    RequestHelper.builder(EndPoints.STATION_INFO)
            .addPath(StringHelper.toEnglishDigits(stationCode) + "")
            .listener(getStationInfo)
            .get();

  }

  RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          boolean isCountrySide = false;
          String stationName = "";
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
            if (stationInfoModel.getStreet().isEmpty()) continue;
            stationInfoModels.add(stationInfoModel);
          }
          if (stationInfoModels.size() == 0) {
            MyApplication.Toast("اطلاعاتی موجود نیست", Toast.LENGTH_SHORT);
          } else {
            if (stationName.equals("")) {
              new StationInfoDialog().show(stationInfoModels, "کد ایستگاه : " + txtStation.getText().toString(), isCountrySide);
            } else {
              new StationInfoDialog().show(stationInfoModels, stationName + " \n " + "کد ایستگاه : " + txtStation.getText().toString(), isCountrySide);
            }
          }

          if (vfStationInfo != null) {
            vfStationInfo.setDisplayedChild(0);
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          if (vfStationInfo != null) {
            vfStationInfo.setDisplayedChild(0);
          }
        }
      });
    }
  };

  private void setStationCode(int userId, int tripId, int stationCode) {
    RequestHelper.builder(EndPoints.UPDATE_TRIP_STATION)
            .addParam("userId", userId)
            .addParam("tripId", tripId)
            .addParam("stationCode", StringHelper.toEnglishDigits(stationCode + ""))
            .listener(setStationCode)
            .put();

  }

  RequestHelper.Callback setStationCode = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
//          {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
          Log.i(TAG, "onResponse: " + args[0].toString());
          JSONObject obj = new JSONObject(args[0].toString());
          boolean success = obj.getBoolean("success");
          String message = obj.getString("message");
          JSONObject dataArr = obj.getJSONObject("data");
          boolean status = dataArr.getBoolean("status");

          if (status) {
            tripDataBase.deleteRow(tripModels.get(counter).getId());
          } else {
            tripDataBase.insertSendDate(tripModels.get(counter).getId(), DateHelper.getCurrentGregorianDate().toString());
          }

          counter = counter + 1;
          txtStation.setText("");
          setAddress(counter);

        } catch (JSONException e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
        }
      });
    }
  };

  @SuppressLint("SetTextI18n")
  private void setAddress(int counter) {
    if (tripModels.size() > counter) {
      //TODO complete this in next version
//      String sendDate = tripModels.get(counter).getSendDate();
//      if (tripModels.get(counter).getSendDate() == null || (sendDate != null && DateHelper.parseFormat(sendDate, null).toInstant().plusSeconds((30 * 1000)).toEpochMilli() > DateHelper.getCurrentGregorianDate().getTime())) {
//        if ((sendDate != null && DateHelper.parseFormat(sendDate, null).toInstant().plusSeconds((30 * 1000)).toEpochMilli() > DateHelper.getCurrentGregorianDate().getTime())){
//          Log.i(TAG, "setAddress:  uppperrrrrrrrrrrrrrrrrrrrrrrrrrr");
//          Log.i(TAG, "setAddress: "+DateHelper.parseFormat(sendDate, null).toInstant().plusSeconds((30 * 1000)).toEpochMilli());
//        }
//        if (tripModels.get(counter).getSendDate() == null){
//          Log.i(TAG, "setAddress:  nullllllllllllllllllllllllllllll");
//        }
      address = tripModels.get(counter).getOriginText();
      txtAddress.setText(StringHelper.toPersianDigits(tripModels.get(counter).getCity() + " , " + address));
      txtRemainingAddress.setText(StringHelper.toPersianDigits(tripModels.size() - (counter + 1) + " آدرس باقی مانده "));
      Log.i(TAG, "setAddress: " + tripModels.get(counter).getOriginText());
//      }
    } else {
      resetCounter();
      txtAddress.setText("آدرسی موجود نیست...");
      txtRemainingAddress.setText(" ");
    }
  }

  private void startGetAddressTimer() {
    try {
      if (timer != null) {
        return;
      }
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          getAddressList();
        }
      }, 0, 10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void stopGetAddressTimer() {
    try {
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void resetCounter() {
    counter = 0;
  }

  @Override
  public void onDestroy() {
    stopGetAddressTimer();
    super.onDestroy();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}