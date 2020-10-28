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
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.PinEntryEditText;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dataBase.TripModel;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class DeterminationPageFragment2 extends Fragment {
  String TAG = DeterminationPageFragment2.class.getSimpleName();
  Unbinder unbinder;
  boolean pressedRefresh = false;
  boolean isEnable = false;
  boolean isFinished = false;
  boolean isFragmentOpen = false;
  boolean pressSubmit = false;
  ArrayList<StationInfoModel> stationInfoModels;
  DataBase dataBase;
  Timer timer;

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

  @OnLongClick(R.id.btnSubmit)
  public boolean onLongClick() {
    if (txtStation.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
      return false;
    }
    if (dataBase.getRemainingAddress() == 0) {
      MyApplication.Toast("آدرسی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
      txtStation.setText("");
      return false;
    }

    int cityCode = dataBase.getCityName(dataBase.getTopAddress().getCity()).getId();
    String code = StringHelper.toEnglishDigits(txtStation.getText().toString());
    setStationCode(MyApplication.prefManager.getUserCode(), dataBase.getTopAddress().getId(), code, cityCode);

    return true;
  }

//  @OnClick(R.id.btnSubmit)
//  void onSubmit() {
//    if (txtStation.getText().toString().isEmpty()) {
//      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
//      return;
//    }
//    if (dataBase.getRemainingAddress() == 0) {
//      MyApplication.Toast("آدرسی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
//      txtStation.setText("");
//      return;
//    }
//
//    if (pressSubmit) {
//      int cityCode = dataBase.getCityName(dataBase.getTopAddress().getCity()).getId();
//      String code = StringHelper.toEnglishDigits(txtStation.getText().toString());
//      setStationCode(MyApplication.prefManager.getUserCode(), dataBase.getTopAddress().getId(), code, cityCode);
//    } else {
//      this.pressSubmit = true;
//      MyApplication.handler.postDelayed(() -> pressSubmit = false, 300);
//    }
//  }

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

  @OnClick(R.id.btnPlayVoice)
  void onPressPlayVoice() {
    if (dataBase.getRemainingAddress() == 0) {
      //TODO what is the text?
      MyApplication.Toast("آدرسی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
      txtStation.setText("");
      return;
    }

//    new PlayLastConversationDialog().show(tripDataBase.getTopAddress().getId(), "");
  }

  @BindView(R.id.btnDeActivate)
  Button btnDeActivate;

  @BindView(R.id.imgNextAddress)
  ImageView imgNextAddress;

  @OnClick(R.id.imgNextAddress)
  void onNextAddress() {
//    if (tripDataBase.getRemainingAddress() > 0) {
//    if (tripDataBase.goToNextRecord()) {
//      MyApplication.Toast("نمایش رکورد بعدی", Toast.LENGTH_SHORT);
//    } else {
//      MyApplication.Toast("موردی برای نمایش موجود نیست", Toast.LENGTH_SHORT);
//    }
  }

  @OnClick(R.id.llRefresh)
  void onPressRefresh() {
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
    changeStatus(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_determination_page, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    dataBase = new DataBase(MyApplication.context);

    changeStatus(MyApplication.prefManager.isStartGettingAddress());

    for (int numberCount = 0; numberCount < 10; numberCount++) {
      View grid = gridNumber.getChildAt(numberCount);
      int count = numberCount;
      grid.setOnClickListener(view1 -> {
        if (count == 9) {
          if (txtStation.getText().toString().isEmpty()) return;
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
//      txtStation.setText(StringHelper.toPersianDigits(temp.substring(0, 2) + c));
//      if (txtStation.getText().toString().indexOf(0)==0)return;
      if (c.equals("0")) {
        txtStation.setText("");
      } else {
        txtStation.setText(StringHelper.toPersianDigits(c));
      }
    } else {
//      if (txtStation.getText().toString().indexOf(0)==0)return;
      txtStation.setText(StringHelper.toPersianDigits(temp + c));
    }
  }

  private void getAddressList() {
    RequestHelper.builder(EndPoints.GET_TRIP_WITH_ZERO_STATION)
            .listener(getAddressList)
            .hideNetworkError(true)
            .get();
  }

  RequestHelper.Callback getAddressList = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @SuppressLint("SetTextI18n")
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
                if (imgRefresh != null)
                  imgRefresh.clearAnimation();
                dataBase.deleteAllData();
              }
              if (dataArr.length() == 0) {
                isFinished = true;
                dataBase.deleteAllData();
                setAddress();
              } else {
                if (dataBase.getRemainingAddress() > 1)
                  dataBase.deleteRemainingRecord(dataBase.getTopAddress().getId());
                for (int i = 0; i < dataArr.length(); i++) {
                  JSONObject dataObj = dataArr.getJSONObject(i);
                  TripModel tripModel = new TripModel();
                  tripModel.setId(dataObj.getInt("Id"));
                  tripModel.setOriginStation(dataObj.getInt("OriginStation"));

                  String content = dataObj.getString("Content");
                  JSONObject contentObj = new JSONObject(content);

                  tripModel.setCity(contentObj.getInt("cityCode"));
                  tripModel.setOriginText(contentObj.getString("address"));
                  tripModel.setSaveDate(dataObj.getString("SaveDate"));

                  dataBase.insertTripRow(tripModel);
                }

                if (txtRemainingAddress != null)
                  txtRemainingAddress.setText("تعداد آدرس های ثبت نشده : " + dataBase.getRemainingAddress());

                Log.i(TAG, "run:tripDataBase.getRemainingAddress = " + dataBase.getRemainingAddress());

                if (isEnable) {
                  setAddress();
                  isEnable = false;
                }

                if (isFinished) {
                  setAddress();
                  isFinished = false;
                }

                if (pressedRefresh) {
                  setAddress();
                  pressedRefresh = false;
                }

              }
            } else {
              if (isFragmentOpen) {
                new GeneralDialog()
                        .title("خطا")
                        .message(message)
                        .cancelable(false)
                        .firstButton("باشه", null)
                        .show();
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
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          isFinished = false;
          if (imgRefresh != null)
            imgRefresh.clearAnimation();

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
      dataBase.deleteAllData();
      if (isEnable) {
        getAddressList();
      }
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

  private void setStationCode(int userId, int tripId, String stationCode, int cityCode) {
    //TODO comment zero numbers...
    Log.e(TAG, "setStationCode: getOriginText = " + dataBase.getTopAddress().getOriginText() + ", stationCode = " + stationCode + ", tripId = " + tripId + ", cityCode = " + cityCode);

    RequestHelper.builder(EndPoints.UPDATE_TRIP_STATION)
            .addParam("userId", userId)
//            .addParam("tripId", StringHelper.toEnglishDigits(tripId + ""))
//            .addParam("stationCode", StringHelper.toEnglishDigits(stationCode + ""))
//            .addParam("cityCode", StringHelper.toEnglishDigits(cityCode + ""))
            .addParam("tripId", 0)
            .addParam("stationCode", 0)
            .addParam("cityCode", 0)
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
            dataBase.deleteRow(dataBase.getTopAddress().getId());
          } else {
            dataBase.insertSendDate(dataBase.getTopAddress().getId(), DateHelper.getCurrentGregorianDate().toString());
          }
          setAddress();
          txtStation.setText("");

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
  private void setAddress() {
    if (txtAddress == null) return;
    if (txtRemainingAddress == null) return;
    if (dataBase.getRemainingAddress() > 0) {
      String cityName = dataBase.getCityName(dataBase.getTopAddress().getCity()).getCity();
      txtAddress.setText(cityName + " , " + dataBase.getTopAddress().getOriginText());
      txtRemainingAddress.setText("تعداد آدرس های ثبت نشده : " + dataBase.getRemainingAddress());
    } else {
      if (!MyApplication.prefManager.isStartGettingAddress()) {
        txtAddress.setText("برای مشاهده آدرس ها فعال شوید");
      } else {
        txtAddress.setText("آدرسی موجود نیست...");
      }
      txtRemainingAddress.setText("");
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

  @Override
  public void onResume() {
    super.onResume();
    isFragmentOpen = true;
    Log.i(TAG, "onResume: ");
  }

  @Override
  public void onPause() {
    super.onPause();
    isFragmentOpen = false;
    Log.i(TAG, "onPause: ");
  }
}