package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.TripAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.SearchFilterDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TripModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class SupportFragment extends Fragment {
  Unbinder unbinder;
  ArrayList<TripModel> tripModels;
  TripAdapter tripAdapter;
  int searchCase = 2;

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.vfTrip)
  ViewFlipper vfTrip;

  @OnClick(R.id.imgSearch)
  void onSearchPress() {
    String searchText = edtSearchTrip.getText().toString();

    searchService(searchText);
  }

  @OnClick(R.id.imgClear)
  void onClearPress() {
    FragmentHelper.toFragment(MyApplication.currentActivity, new TripDetailsFragment()).replace();
  }

  @OnClick(R.id.imgSearchType)
  void onSearchTypePress() {
    new SearchFilterDialog().show(type -> {
      this.searchCase = type;
    });
  }

  @OnClick(R.id.llExtendedTime)
  void onExtendedTimePress() {
    chbExtendedTime.setChecked(!chbExtendedTime.isChecked());
  }

  @BindView(R.id.chbExtendedTime)
  CheckBox chbExtendedTime;

  @BindView(R.id.edtSearchTrip)
  EditText edtSearchTrip;

  @BindView(R.id.recycleTrip)
  RecyclerView recycleTrip;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_support, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    getList();

    return view;
  }

  String tripList = "{\"success\":true,\"message\":\"\",\"data\":[\n" +
          "{\"callTime\":\"13:52\",\"sendTime\":\"14:00\",\"city\":\"مشهد\",\"customerName\":\"فاطمه نوری\",\"customerTell\":\"09015693808\",\"customerMob\":\"33710834\",\"address\":\"مشهد، فداییان اسلا\",\"carType\":\"تاکسی\",\"driverMobile\":\"09015693808\"},\n" +
          "{\"callTime\":\"14:52\",\"sendTime\":\"15:00\",\"city\":\"نیشابور\",\"customerName\":\"سارانوری\",\"customerTell\":\"09015693806\",\"customerMob\":\"33710836\",\"address\":\"ممشهد، فداییان اسلا\",\"carType\":\"اقتصادی\",\"driverMobile\":\"09015693806\"},\n" +
          "{\"callTime\":\"15:52\",\"sendTime\":\"16:00\",\"city\":\"کاشمر\",\"customerName\":\"فائزه نوری\",\"customerTell\":\"09015693855\",\"customerMob\":\"33710834\",\"address\":\"مشهد، فداییان اسلا\",\"carType\":\"تشریفات\",\"driverMobile\":\"09015693855\"}]}";

  private void getList() {
    try {
      tripModels = new ArrayList<>();
      JSONObject tripObject = new JSONObject(tripList);
      Boolean success = tripObject.getBoolean("success");
      String message = tripObject.getString("message");
      JSONArray data = tripObject.getJSONArray("data");
      for (int i = 0; i < data.length(); i++) {
        JSONObject dataObj = data.getJSONObject(i);
        TripModel tripModel = new TripModel();
        tripModel.setCallTime(dataObj.getString("callTime"));
        tripModel.setSendTime(dataObj.getString("sendTime"));
        tripModel.setCustomerName(dataObj.getString("customerName"));
        tripModel.setCustomerTell(dataObj.getString("customerTell"));
        tripModel.setCustomerMob(dataObj.getString("customerMob"));
        tripModel.setAddress(dataObj.getString("address"));
        tripModel.setCarType(dataObj.getString("carType"));
        tripModel.setCity(dataObj.getString("city"));
        tripModel.setDriverMobile(dataObj.getString("driverMobile"));
        tripModels.add(tripModel);
      }

      tripAdapter = new TripAdapter(tripModels);
      if (recycleTrip != null)
        recycleTrip.setAdapter(tripAdapter);

      vfTrip.setDisplayedChild(1);

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void searchService(String searchText) {
    if (vfTrip != null) {
      vfTrip.setDisplayedChild(0);
    }

    int extendedTime = chbExtendedTime.isChecked() ? 1 : 0;

    RequestHelper.builder(EndPoints.SEARCH_SERVICE)
            .addParam("searchCase", searchCase)
            .addParam("searchText", searchText)
            .addParam("allServices", extendedTime)
            .listener(onGetTripList)
            .post();
  }

  RequestHelper.Callback onGetTripList = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("SupportFragment", "run: " + args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
        if (vfTrip != null) {
          vfTrip.setDisplayedChild(3);
        }
      });
    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}