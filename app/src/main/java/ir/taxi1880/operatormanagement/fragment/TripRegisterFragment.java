package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripRegisterFragment extends Fragment {

  public static final String TAG = TripRegisterFragment.class.getSimpleName();
  Unbinder unbinder;
  String cityName;
  String ServiceType;
  String ServiceCount;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.spCity)
  Spinner spCity;

  @BindView(R.id.spServiceCount)
  Spinner spServiceCount;

  @BindView(R.id.spServiceType)
  Spinner spServiceType;

  @BindView(R.id.txtOrigin)
  TextView txtOrigin;

  @OnClick(R.id.txtOrigin)
  void onPressOrigin() {
  }

  @OnClick(R.id.txtDestination)
  void onPressDestonation() {

  }

  @OnClick(R.id.btnSubmit)
  void onPressSubmit() {
    new GeneralDialog()
            .title("ثبت اطلاعات")
            .message("آیا از ثبت اطلاعات اطمینان دارید؟")
            .firstButton("بله", null)
            .secondButton("خیر", null)
            .show();
  }

  @BindView(R.id.txtDestination)
  TextView txtDestination;

  String city = "[{\"name\":\"مشهد\"},{\"name\":\"نیشابور\"},{\"name\":\"تربت حیدریه\"},{\"name\":\"تربت جام\"},{\"name\":\"گناباد\"}," +
          "{\"name\":\"کاشمر\"},{\"name\":\"تایباد\"}]";

  String serviceType = "[{\"name\":\"سرویس\"},{\"name\":\"دراختیار\"},{\"name\":\"بانوان\"}]";

  String serviceCount = "[{\"name\":\"1\"},{\"name\":\"2\"},{\"name\":\"3\"},{\"name\":\"4\"}]";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_register, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

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
          cityName = spCity.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }

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
          ServiceType = spServiceType.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }

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

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
