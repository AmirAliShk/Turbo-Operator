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
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class RangeFragment extends Fragment {
  String TAG = RangeFragment.class.getSimpleName();
  Unbinder unbinder;
  boolean isOriginSelected = false;
  ArrayList<StationInfoModel> stationInfoModels;

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
    if (!MyApplication.prefManager.getActivateStatus()) {
      MyApplication.Toast("لطفا فعال شوید", Toast.LENGTH_SHORT);
      return;
    }
    if (txtStation.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
      return;
    }
    if (counter < tripModels.size()) {
      MyApplication.Toast("registered", Toast.LENGTH_SHORT);
      //TODO set API here
      registerStation(counter);
      counter = counter + 1;
      txtStation.setText("");
    }
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
    tripDataBase.deleteAllData();
    getAddress(addressList);
    //TODO correct bellow code
    tripModels = tripDataBase.getTripRow();
    address = tripModels.get(0).getOriginText();
    txtAddress.setText(address);
    txtRemainingAddress.setText(tripModels.size() - 1 + " آدرس ");
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
    View view = inflater.inflate(R.layout.fragment_range, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    tripDataBase = new TripDataBase(MyApplication.context);
    tripModels = new ArrayList<>();
    tripModelsNewData = new ArrayList<>();

    changeStatus(MyApplication.prefManager.getActivateStatus());

    if (MyApplication.prefManager.getActivateStatus()) {
      //TODO Do I need to check if it is empty? or use Handler.postDeley
      tripModels = tripDataBase.getTripRow();
      address = tripModels.get(0).getOriginText();
      txtAddress.setText(address);
      txtRemainingAddress.setText(tripModels.size() - 1 + " آدرس ");
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

  ArrayList<TripModel> tripModels;
  ArrayList<TripModel> tripModelsNewData;
  TripDataBase tripDataBase;
  Timer addressTimer;
  String address;
  String addressList = " {\"success\":true,\"message\":\"\",\"data\":[" +
          "{\"tripId\":1,\"originText\":\"مبدا 1 فداییان اسلام\",\"originStation\":14,\"city\":\"مشهد\"}," +
          "{\"tripId\":2,\"originText\":\"مبدا 2 هاشمیه 6\",\"originStation\":14,\"city\":\"مشهد\"}," +
          "{\"tripId\":3,\"originText\":\"مبدا 3 بیمارستان امام رضا\",\"originStation\":14,\"city\":\"مشهد\"}," +
          "{\"tripId\":4,\"originText\":\"مبدا 4 قاسم آباد-یوسفی 7\",\"originStation\":14,\"city\":\"مشهد\"}," +
          "{\"tripId\":5,\"originText\":\"مبدا 5 کوهسنگی 44\",\"originStation\":14,\"city\":\"مشهد\"}]}";

  private void getAddress(String addressList) {
    try {
      //TODO set loader
      JSONObject objAddress = new JSONObject(addressList);
      tripModelsNewData.clear();

      boolean success = objAddress.getBoolean("success");
      String message = objAddress.getString("message");
      JSONArray arrAddress = objAddress.getJSONArray("data");
      if (success) {
        for (int i = 0; i < arrAddress.length(); i++) {
          JSONObject address = arrAddress.getJSONObject(i);
          TripModel tripModel = new TripModel();
          tripModel.setId(address.getInt("tripId"));
          tripModel.setOriginText(address.getString("originText"));
          tripModel.setOriginStation(address.getInt("originStation"));
          tripModel.setCity(address.getString("city"));
          tripModelsNewData.add(tripModel);
          tripDataBase.insertTripRow(tripModel);
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  int counter = 0;

  TimerTask addressTt = new TimerTask() {
    @Override
    public void run() {
      //TODO call API every 10 sec
      getAddress(addressList);
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

  private void changeStatus(boolean status) {
    if (status) {
      startGetAddressTimer();
      MyApplication.prefManager.setActivateStatus(true);
      if (btnActivate != null)
        btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        btnDeActivate.setTextColor(Color.parseColor("#000000"));
      }
      //TODO is this correct?
      tripModels = tripDataBase.getTripRow();
      address = tripModels.get(0).getOriginText();
      txtAddress.setText(address);
      txtRemainingAddress.setText(tripModels.size() - 1 + " آدرس ");

    } else {
      stopGetAddressTimer();
      getAddress(addressList);
      MyApplication.prefManager.setActivateStatus(false);
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

          LoadingDialog.dismissCancelableDialog();
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