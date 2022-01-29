package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentDriverLocationBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverLocationFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = DriverLocationFragment.class.getSimpleName();
    FragmentDriverLocationBinding binding;
    double lat = 0;
    double lng = 0;
    String carCode;
    String time;
    boolean isFromDriverSupport;
    GoogleMap myGoogleMap;
    Marker myLocationMarker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverLocationBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        binding.map.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity().getApplicationContext());
        binding.map.getMapAsync(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");
            binding.txtLastTime.setText(bundle.getString("time"));
            bundle.getString("date");
            carCode = bundle.getString("taxiCode");
            isFromDriverSupport = bundle.getBoolean("isFromDriverSupport");
            if (isFromDriverSupport) {
                getLastLocation();
            }
        }

        binding.imgRefresh.setOnClickListener(view -> {
            binding.imgRefresh.startAnimation(AnimationUtils.loadAnimation(MyApplication.context, R.anim.rotate));
            getLastLocation();
        });

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myGoogleMap = googleMap;
        animateToLocation(lat, lng);
    }

    private void animateToLocation(final double latitude, final double longitude) {

        if ((lat == 0 || lng == 0) && !isFromDriverSupport) {
            MyApplication.Toast("موقعیت راننده در دسترس نمیباشد", Toast.LENGTH_SHORT);
            return;
        }

        LatLng latlng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latlng)
                .zoom(14)
                .build();

        if (myGoogleMap != null)
            myGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.pin);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 100, false);

        if (myGoogleMap != null) {
            myGoogleMap.clear();
            myLocationMarker = myGoogleMap.addMarker(
                    new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            .position(latlng));
        }
    }

    private void getLastLocation() {
        RequestHelper.builder(EndPoints.LAST_DRIVER_POSITION)
                .addParam("taxiCode", carCode)
                .listener(onGetLastLocation)
                .post();
    }

    RequestHelper.Callback onGetLastLocation = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"lat":"35.2510216","lon":"60.6222999","r_time":"11:24:11","r_date":"1399/08/24","bearing":2.03999}}
                    if (binding.imgRefresh != null)
                        binding.imgRefresh.clearAnimation();
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    JSONObject dataObj = object.getJSONObject("data");

                    if (success) {
                        lat = dataObj.getDouble("lat");
                        lng = dataObj.getDouble("long");
                        time = dataObj.getString("r_time");
                        if (binding.txtLastTime != null) {
                            binding.txtLastTime.setText(time);
                        }
                        animateToLocation(lat, lng);
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", () -> MyApplication.currentActivity.onBackPressed())
                                .show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetLastLocation method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.imgRefresh != null)
                    binding.imgRefresh.clearAnimation();
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding.map != null)
            binding.map.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.map != null)
            binding.map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.map != null)
            binding.map.onResume();
    }
}