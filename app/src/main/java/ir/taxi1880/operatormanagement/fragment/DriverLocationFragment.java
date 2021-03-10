package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

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
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class DriverLocationFragment extends Fragment implements OnMapReadyCallback {
    Unbinder unbinder;
    double lat = 0;
    double lng = 0;
    String carCode;
    String time;
    boolean isFromDriverSupport;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @OnClick(R.id.imgRefresh)
    void imgRefresh() {
        imgRefresh.startAnimation(AnimationUtils.loadAnimation(MyApplication.context, R.anim.rotate));
        getLastLocation();
    }

    @BindView(R.id.imgRefresh)
    ImageView imgRefresh;

    @BindView(R.id.map)
    MapView map;

    @BindView(R.id.txtLastTime)
    TextView txtLastTime;

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

        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");
            txtLastTime.setText(bundle.getString("time"));
            bundle.getString("date");
            carCode = bundle.getString("taxiCode");
            isFromDriverSupport = bundle.getBoolean("isFromDriverSupport");
            if (isFromDriverSupport) {
                getLastLocation();
            }
        }

        return view;
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
                    if (imgRefresh != null)
                        imgRefresh.clearAnimation();
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    JSONObject dataObj = object.getJSONObject("data");

                    if (success) {
                        lat = dataObj.getDouble("lat");
                        lng = dataObj.getDouble("long");
                        time = dataObj.getString("r_time");
                        if (txtLastTime != null) {
                            txtLastTime.setText(time);
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
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (imgRefresh != null)
                    imgRefresh.clearAnimation();
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