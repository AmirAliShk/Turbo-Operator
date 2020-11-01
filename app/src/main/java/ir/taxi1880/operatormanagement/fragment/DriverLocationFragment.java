package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
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
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class DriverLocationFragment extends Fragment implements OnMapReadyCallback {
  Unbinder unbinder;

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
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    myGoogleMap = googleMap;
    myGoogleMap.getUiSettings().setMapToolbarEnabled(false);
    myGoogleMap.getUiSettings().setZoomControlsEnabled(false);
    myGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
    LatLng myLocation = new LatLng(36.256924, 59.619964);

    CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(myLocation)
            .zoom(12)
            .build();

    myGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    if (map != null)
      myLocationMarker = myGoogleMap.addMarker(
              new MarkerOptions()
                      .icon(BitmapDescriptorFactory.fromResource(R.mipmap.turbo))
                      .position(myLocation));

  }
}