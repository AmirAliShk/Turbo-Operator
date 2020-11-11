package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import ir.taxi1880.operatormanagement.dialog.ExtendedTimeDialog;
import ir.taxi1880.operatormanagement.dialog.SearchFilterDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TripModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class SupportFragment extends Fragment {
  Unbinder unbinder;
  ArrayList<TripModel> tripModels;
  TripAdapter tripAdapter;
  int searchCase = 2;
  int extendedTime = 1;
  String searchText;

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.vfTrip)
  ViewFlipper vfTrip;

  @BindView(R.id.imgSearchType)
  ImageView imgSearchType;

  @BindView(R.id.imgExtendedTime)
  ImageView imgExtendedTime;

  @BindView(R.id.txtExtendTime)
  TextView txtExtendTime;

  @OnClick(R.id.imgSearch)
  void onSearchPress() {
    searchText = StringHelper.toEnglishDigits(edtSearchTrip.getText().toString());
    searchService(searchText,searchCase);
  }

  @OnClick(R.id.imgClear)
  void onClearPress() {
    edtSearchTrip.setText("");
  }

  @OnClick(R.id.imgSearchType)
  void onSearchTypePress() {
    new SearchFilterDialog().show(searchCase -> {
      int imageType = R.drawable.ic_call;
      switch (searchCase) {
        case 1:
          imageType = R.drawable.ic_user;
          break;
        case 2:
          imageType = R.drawable.ic_call;
          break;
        case 3:
          imageType = R.drawable.ic_gps;
          break;
        case 4:
          imageType = R.drawable.ic_taxi;
          break;
        case 5:
          imageType = R.drawable.ic_code;
          break;
      }
      imgSearchType.setImageResource(imageType);
      this.searchCase = searchCase;
    });
  }

  @OnClick(R.id.llExtendedTime)
  void onExtendedTimePress() {
    new ExtendedTimeDialog().show((type, title, icon) -> {
      extendedTime = type;
      txtExtendTime.setText(title);
      imgExtendedTime.setImageResource(icon);
    });
  }

  @BindView(R.id.edtSearchTrip)
  EditText edtSearchTrip;

  @BindView(R.id.recycleTrip)
  RecyclerView recycleTrip;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_support, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);
    TypefaceUtil.overrideFonts(edtSearchTrip,MyApplication.IraSanSMedume);

    String tellNumber;
    Bundle bundle = getArguments();
    if (bundle != null) {
      tellNumber = bundle.getString("tellNumber");
      edtSearchTrip.setText(tellNumber);
    }

    searchText = StringHelper.toEnglishDigits(edtSearchTrip.getText().toString());

    searchService(searchText,0);

    return view;
  }

  private void searchService(String searchText, int searchCase) {
    if (vfTrip != null) {
      vfTrip.setDisplayedChild(0);
    }

    switch (searchCase) {

      case 0:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", 0)
                .addParam("name", 0)
                .addParam("address", 0)
                .addParam("taxiCode", 0)
                .addParam("stationCode", 0)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;


      case 1:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", 0)
                .addParam("name", searchText)
                .addParam("address", 0)
                .addParam("taxiCode", 0)
                .addParam("stationCode", 0)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;

      case 2:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", searchText)
                .addParam("name", 0)
                .addParam("address", 0)
                .addParam("taxiCode", 0)
                .addParam("stationCode", 0)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;

      case 3:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", 0)
                .addParam("name", 0)
                .addParam("address", searchText)
                .addParam("taxiCode", 0)
                .addParam("stationCode", 0)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;

      case 4:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", 0)
                .addParam("name", 0)
                .addParam("address", 0)
                .addParam("taxiCode", searchText)
                .addParam("stationCode", 0)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;

      case 5:
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .addParam("phonenumber", 0)
                .addParam("name", 0)
                .addParam("address", 0)
                .addParam("taxiCode", 0)
                .addParam("stationCode", searchText)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
        break;
    }

  }

  RequestHelper.Callback onGetTripList = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TAG", "run: "+args[0].toString());
            tripModels = new ArrayList<>();
            JSONObject tripObject = new JSONObject(args[0].toString());
            Boolean success = tripObject.getBoolean("success");
            String message = tripObject.getString("message");
            JSONArray data = tripObject.getJSONArray("data");

            if (success) {
              for (int i = 0; i < data.length(); i++) {
                JSONObject dataObj = data.getJSONObject(i);
                TripModel tripModel = new TripModel();
                tripModel.setServiceId(dataObj.getString("serviceId"));
                tripModel.setStatus(dataObj.getInt("Status"));
                tripModel.setCallDate(dataObj.getString("ContDate"));
                tripModel.setCallTime(dataObj.getString("ContTime"));
                tripModel.setSendTime(dataObj.getString("SendTime"));
                tripModel.setSendDate(dataObj.getString("SendDate"));
                tripModel.setStationCode(dataObj.getInt("stcode"));
                tripModel.setCustomerName(dataObj.getString("MoshName"));
                tripModel.setCustomerTell(dataObj.getString("MoshTel"));
                tripModel.setCustomerMob(dataObj.getString("MoshZone"));
                tripModel.setAddress(dataObj.getString("MoshAddr"));
                tripModel.setCity(dataObj.getString("cityName"));
                tripModel.setCarType(dataObj.getString("CarType2"));
                tripModel.setDriverMobile(dataObj.getString("MobCar"));
                tripModel.setFinished(dataObj.getInt("Finished"));
                tripModels.add(tripModel);
              }

              tripAdapter = new TripAdapter(tripModels);
              if (recycleTrip != null)
                recycleTrip.setAdapter(tripAdapter);

              if (tripModels.size() == 0) {
                if (vfTrip != null)
                  vfTrip.setDisplayedChild(2);
              }else {
                if (vfTrip != null)
                  vfTrip.setDisplayedChild(1);
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