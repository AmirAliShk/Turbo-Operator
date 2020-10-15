package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.NumberPadAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.TripDataBase;
import ir.taxi1880.operatormanagement.dataBase.TripModel;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class RangeFragment extends Fragment {
  String TAG = RangeFragment.class.getSimpleName();
  Unbinder unbinder;
  boolean status = false;
  boolean isRegistered = false;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.gridNumber)
  GridView gridNumber;

  @BindView(R.id.txtStation)
  TextView txtStation;

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
    isRegistered = true;
    if (counter != 0) counter = counter - 1;
    MyApplication.Toast("submit", Toast.LENGTH_SHORT);
  }

  @OnClick(R.id.btnHelp)
  void onHelp() {
    MyApplication.Toast("Help", Toast.LENGTH_SHORT);
  }

  @BindView(R.id.btnActivate)
  Button btnActivate;

  @BindView(R.id.btnDeActivate)
  Button btnDeActivate;

  @OnClick(R.id.llMenu)
  void onMenu() {
    MyApplication.Toast("Menu", Toast.LENGTH_SHORT);
  }

  @OnClick(R.id.btnActivate)
  void onActivePress() {
    changeStatus();
  }

  @OnClick(R.id.btnDeActivate)
  void onDeActivePress() {
    changeStatus();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_range, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    tripDataBase = new TripDataBase(MyApplication.context);
    tripModels = new ArrayList<>();
    tripModels = tripDataBase.getTripRow();

    changeStatus();

    getAddress(addressList);

    gridNumber.setAdapter(new NumberPadAdapter(MyApplication.context, new NumberPadAdapter.NumberListener() {
      @Override
      public void onResult(String character) {
        switch (character) {
          case "0":
            setNumber("0");
            break;
          default:
            setNumber(character);
        }

      }
    }));

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
  TripDataBase tripDataBase;
  Timer addressTimer;
  String addressList = " {\"success\":true,\"message\":\"\",\"data\":[" +
          "{\"tripId\":1,\"originText\":\"مبدا فداییان اسلام\",\"destinationText\":\"مقصد سیدرضی55\",\"originStation\":14,\"destinationStation\":18,\"city\":\"مشهد\"}," +
          "{\"tripId\":2,\"originText\":\"مبدا هاشمیه 6\",\"destinationText\":\"مقصد لادن 12\",\"originStation\":14,\"destinationStation\":18,\"city\":\"مشهد\"}," +
          "{\"tripId\":3,\"originText\":\"مبدا بیمارستان امام رضا\",\"destinationText\":\"مقصد وکیل آباد 24\",\"originStation\":14,\"destinationStation\":18,\"city\":\"مشهد\"}," +
          "{\"tripId\":4,\"originText\":\"مبدا قاسم آباد-یوسفی 7\",\"destinationText\":\"مقصد خیابان امام رضا14\",\"originStation\":14,\"destinationStation\":18,\"city\":\"مشهد\"}," +
          "{\"tripId\":5,\"originText\":\"مبدا کوهسنگی 44\",\"destinationText\":\"مقصد وکیل آباد-عارف3\",\"originStation\":14,\"destinationStation\":18,\"city\":\"مشهد\"}]}";

  private void getAddress(String addressList) {
    try {
      JSONObject objAddress = new JSONObject(addressList);

      boolean success = objAddress.getBoolean("success");
      String message = objAddress.getString("message");
      JSONArray arrAddress = objAddress.getJSONArray("data");
      if (success) {
        for (int i = 0; i < arrAddress.length(); i++) {
          JSONObject address = arrAddress.getJSONObject(i);
          TripModel tripModel = new TripModel();
          tripModel.setId(address.getInt("tripId"));
          tripModel.setOriginText(address.getString("originText"));
          tripModel.setDestinationText(address.getString("destinationText"));
          tripModel.setOriginStation(address.getInt("originStation"));
          tripModel.setDestinationStation(address.getInt("destinationStation"));
          tripModel.setCity(address.getString("city"));

          tripDataBase.insertTripRow(tripModel);
        }
      }

      showAddress();

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  int counter = 0;

  TimerTask addressTt = new TimerTask() {
    @Override
    public void run() {
      String address;
      if (counter < tripModels.size()) {
        if (isRegistered) {
          address = tripModels.get(counter).getDestinationText();
        } else {
          address = tripModels.get(counter).getOriginText();
        }
        txtAddress.setText(address);
        txtRemainingAddress.setText(tripModels.size() - counter + " آدرس ");
        counter = counter + 1;
      } else {
        counter = 0;
        //TODO here call API again????
      }
    }
  };

  private void showAddress() {
    if (addressTimer == null) {
      addressTimer = new Timer();
    }
    //TODO set period to 15000
    addressTimer.scheduleAtFixedRate(addressTt, 500, 5000);
  }

  private void changeStatus() {
    if (status) {
      status = false;
      MyApplication.Toast("شما خارج شدید", Toast.LENGTH_SHORT);
      MyApplication.prefManager.setActivateStatus(false);
      if (btnActivate != null)
        btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
        btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
      }
    } else {
      status = true;
      MyApplication.Toast("شما وارد شدید", Toast.LENGTH_SHORT);
      if (btnActivate != null)
        btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      MyApplication.prefManager.setActivateStatus(true);
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        btnDeActivate.setTextColor(Color.parseColor("#000000"));
      }
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