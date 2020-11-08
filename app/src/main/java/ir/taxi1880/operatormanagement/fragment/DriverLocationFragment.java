package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class DriverLocationFragment extends Fragment implements OnMapReadyCallback {
  Unbinder unbinder;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.map)
  MapView map;

  GoogleMap myGoogleMap;
  Marker myLocationMarker;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_driver_location, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);
    map.onCreate(savedInstanceState);
    MapsInitializer.initialize(getActivity().getApplicationContext());
    map.getMapAsync(this);

    return view;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    myGoogleMap = googleMap;

    LatLng myLocation = new LatLng(36.256924, 59.619964);

    CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(myLocation)
            .zoom(16)
            .build();

    myGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    if (myGoogleMap != null)
      myLocationMarker = myGoogleMap.addMarker(
              new MarkerOptions()
//                      .icon(BitmapDescriptorFactory.fromResource(R.mipmap.turbo))
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                      .position(myLocation));

  }

  private void getLastLocation(String searchText) {

    RequestHelper.builder(EndPoints.LAST_DRIVER_POSITION)
            .addParam("taxiCode", 1)
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
            Log.i("DriverLocationFragment", "run: "+args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {

      });
    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (map != null)
      map.onDestroy();
    unbinder.unbind();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (map != null)
      map.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (map != null)
      map.onResume();
  }
}