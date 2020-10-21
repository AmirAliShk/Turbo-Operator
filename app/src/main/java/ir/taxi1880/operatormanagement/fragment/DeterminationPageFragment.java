package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DeterminationPageFragment extends Fragment {
  String TAG = DeterminationPageFragment.class.getSimpleName();
  Unbinder unbinder;
  boolean isOriginSelected = false;
  ArrayList<StationInfoModel> stationInfoModels;
  ArrayList<TripModel> tripModels;
  TripDataBase tripDataBase;
  Timer addressTimer;
  String address;
  int counter = 0;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.gridNumber)
  GridLayout gridNumber;

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
    if (!MyApplication.prefManager.isStartGettingAddress()) {
      MyApplication.Toast("لطفا فعال شوید", Toast.LENGTH_SHORT);
      return;
    }
    if (txtStation.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
      setStationCode(MyApplication.prefManager.getUserCode(), tripModels.get(counter).getId(), tripModels.get(counter).getOriginStation());

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
    tripDataBase.deleteAllData();
    getAddressList();
    //TODO correct bellow code
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

    tripDataBase = new TripDataBase(MyApplication.context);
    tripModels = new ArrayList<>();

    changeStatus(MyApplication.prefManager.isStartGettingAddress());

    if (MyApplication.prefManager.isStartGettingAddress()) {
      getAddressList();
      //TODO Do I need to check if it is empty? or use Handler.postDeley
//      tripModels = tripDataBase.getTripRow();
//      address = tripModels.get(0).getOriginText();
//      txtAddress.setText(address);
//      txtRemainingAddress.setText(tripModels.size() - 1 + " آدرس ");
    } else {
      txtAddress.setText("برای مشاهده آدرس ها فعال شوید");
      txtRemainingAddress.setText("");
    }

    for (int numberCount = 0; numberCount < 10; numberCount++) {
      View grid = (View) gridNumber.getChildAt(numberCount);
      int count = numberCount;
      grid.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (count == 9) {
            setNumber("0");
          } else {
            setNumber(count + 1 + "");
          }
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

                tripDataBase.insertTripRow(tripModel);
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

  private void changeStatus(boolean status) {
    if (status) {
      startGetAddressTimer();
      MyApplication.prefManager.setStartGettingAddress(true);
      if (btnActivate != null)
        btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        btnDeActivate.setTextColor(Color.parseColor("#000000"));
      }
      //TODO is this correct?
//      tripModels = tripDataBase.getTripRow();
//      address = tripModels.get(0).getOriginText();
//      txtAddress.setText(address);
//      txtRemainingAddress.setText(tripModels.size() - 1 + " آدرس ");

    } else {
      stopGetAddressTimer();
      getAddressList();
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
    // TODO this loader is better or viewFlipper??
    LoadingDialog.makeCancelableLoader();
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
          LoadingDialog.dismissCancelableDialog();
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
              new StationInfoDialog().show(stationInfoModels, "کد ایستگاه : " + txtStation.getText().toString(), isCountrySide);
            } else {
              new StationInfoDialog().show(stationInfoModels, stationName + " \n " + "کد ایستگاه : " + txtStation.getText().toString(), isCountrySide);
            }
          }

        } catch (JSONException e) {
          e.printStackTrace();
          AvaCrashReporter.send(e, "TripRegisterActivity class, getStationInfo onResponse method");
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          LoadingDialog.dismissCancelableDialog();
        }
      });
    }
  };

  private void setStationCode(int userId, int tripId, int stationCode) {
    // TODO this loader is better or viewFlipper??
    LoadingDialog.makeCancelableLoader();
    RequestHelper.builder(EndPoints.UPDATE_TRIP_STATION)
            .addParam("userId", userId)
            .addParam("tripId", StringHelper.toEnglishDigits(tripId + ""))
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
          JSONObject obj = new JSONObject(args[0].toString());
          boolean success = obj.getBoolean("success");
          String message = obj.getString("message");
          JSONObject dataArr = obj.getJSONObject("data");
          boolean status = dataArr.getBoolean("status");
          if (status) {
            //TODO delete row, show next address
//            counter = counter + 1;
//            txtStation.setText("");
            new GeneralDialog()
                    .title("ثبت شد")
                    .message(message)
                    .cancelable(false)
                    .firstButton("باشه", null)
                    .show();
          }else {
            new GeneralDialog()
                    .title("خطا")
                    .message(message)
                    .cancelable(false)
                    .firstButton("باشه", null)
                    .show();
          }

        } catch (JSONException e) {
          e.printStackTrace();
          AvaCrashReporter.send(e, "TripRegisterActivity class, getStationInfo onResponse method");
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          LoadingDialog.dismissCancelableDialog();
        }
      });
    }
  };

  TimerTask addressTt = new TimerTask() {
    @Override
    public void run() {
      getAddressList();
    }
  };

  private void startGetAddressTimer() {
    if (addressTimer == null) {
      addressTimer = new Timer();
    }
    addressTimer.scheduleAtFixedRate(addressTt, 0, 10000);

    MyApplication.Toast("timer started", Toast.LENGTH_SHORT);
  }

  private void stopGetAddressTimer() {
    if (addressTimer != null) {
      addressTimer.cancel();
    }
    //TODO in this status what shown be in txtAddress and station?
    MyApplication.Toast("timer stopped", Toast.LENGTH_SHORT);
  }

  @SuppressLint("SetTextI18n")
  private void registerStation(int id) {
    isOriginSelected = false;
    //TODO if status is true, delete record.
    tripDataBase.deleteRow(tripModels.get(id).getId());
    Log.i(TAG, "registerStation: " + tripModels.get(id).getOriginText());
    if (id + 1 < tripModels.size()) {
      address = tripModels.get(id + 1).getOriginText(); //show next origin
      txtAddress.setText(address);
      txtRemainingAddress.setText(tripModels.size() - (id + 2) + " آدرس ");
    } else {
      txtAddress.setText("آدرسی موجود نیست...");
      MyApplication.Toast("درحال حاضر آدرسی موجود نیست....", Toast.LENGTH_SHORT);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    //TODO it is right?
    if (addressTimer != null) {
      addressTimer.cancel();
    }
  }

}