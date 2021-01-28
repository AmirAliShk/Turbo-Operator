package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class EditPassengerAddressDialog {

    private static final String TAG = EditPassengerAddressDialog.class.getSimpleName();

    public interface EditationCallBack {
        void onEdited(boolean success,String message);
    }

    EditationCallBack editationCallBack;
    private Spinner spCity;
    ArrayList<CityModel> cityModels;
    private String cityName = "";
    private String cityLatinName = "";
    private int cityCode;
    int type;
    ViewFlipper vfLoader;

    static Dialog dialog;

    public void show(int cityId, String address, int tripId, EditationCallBack callBack) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_edit_passenger_address);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);
        this.editationCallBack = callBack;

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        EditText edtStation = dialog.findViewById(R.id.edtStation);
        EditText edtAddress = dialog.findViewById(R.id.edtAddress);
        LinearLayout llParent = dialog.findViewById(R.id.llParent);
        spCity = dialog.findViewById(R.id.spCity);
        vfLoader = dialog.findViewById(R.id.vfLoader);

        initSpinner(cityId);

        edtAddress.setText(address);

        imgClose.setOnClickListener(view -> dismiss());

        llParent.setOnClickListener(view -> KeyBoardHelper.hideKeyboard());

        btnSubmit.setOnClickListener(view -> {
            String stationCode = edtStation.getText().toString();
            String addressText = edtAddress.getText().toString();

            if (cityCode == -1) {
                MyApplication.Toast("لطفا شهر را انتخاب کنید.", Toast.LENGTH_SHORT);
                return;
            }

            if (addressText.isEmpty()) {
                MyApplication.Toast("لطفا آدرس را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }

            if (stationCode.isEmpty()) {
                MyApplication.Toast("لطفا ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }

            new GeneralDialog()
                    .title("هشدار")
                    .message("ایا از انجام عملیات فوق اطمینان دارید؟")
                    .firstButton("بله", () -> {
                        Log.i(TAG, "onEdit:tripId " + tripId);
                        Log.i(TAG, "onEdit:stationCode " + stationCode);
                        Log.i(TAG, "onEdit:cityCode " + cityCode);
                        Log.i(TAG, "onEdit:addressText " + addressText);
                        editStation(cityCode, addressText, tripId + "", stationCode);
                    })
                    .secondButton("خیر", null)
                    .cancelable(false)
                    .show();
            KeyBoardHelper.hideKeyboard();
        });

        dialog.show();
    }

    private void initSpinner(int pos) {
        cityModels = new ArrayList<>();
        ArrayList<String> cityList = new ArrayList<String>();
        try {
            JSONArray cityArr = new JSONArray(MyApplication.prefManager.getCity());
            cityList.add(0, "انتخاب نشده");
            for (int i = 0; i < cityArr.length(); i++) {
                JSONObject cityObj = cityArr.getJSONObject(i);
                CityModel cityModel = new CityModel();
                cityModel.setCity(cityObj.getString("cityname"));
                cityModel.setId(cityObj.getInt("cityid"));
                cityModel.setCityLatin(cityObj.getString("latinName"));
                cityModels.add(cityModel);
                cityList.add(i + 1, cityObj.getString("cityname"));
            }
            if (spCity == null) return;
            spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
            spCity.setSelection(pos);
            spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        cityName = null;
                        cityLatinName = null;
                        cityCode = -1;
                        return;
                    }
                    cityName = cityModels.get(position - 1).getCity();
                    cityLatinName = cityModels.get(position - 1).getCityLatin();
                    cityCode = cityModels.get(position - 1).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "EditPassengerAddressDialog class, initCitySpinner method");
        }
    }

    private void editStation(int cityCode, String address, String serviceId, String stationCode) {

        if (vfLoader != null) {
            vfLoader.setDisplayedChild(1);
        }

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.EDIT_STATION)
                .addParam("cityCode", cityCode)
                .addParam("adrs", address)
                .addParam("tripId", serviceId)
                .addParam("stationCode", stationCode)
                .listener(onEditStation)
                .put();
    }

    RequestHelper.Callback onEditStation = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    {"success":true,"message":"کد ایستگاه در این شهر وجود ندارد","data":{"status":false}}
//                    {"success":true,"message":"با موفقیت انجام شد","data":{"status":true}}
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    JSONObject dataArr = obj.getJSONObject("data");
                    boolean status = dataArr.getBoolean("status");

                    if (success) {
                        editationCallBack.onEdited(true,"");
//                        if (status) {
                        dismiss();
//                        }
                    } else {
                        editationCallBack.onEdited(false,message);
                    }

                    if (vfLoader != null) {
                        vfLoader.setDisplayedChild(0);
                    }

                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDialog.dismissCancelableDialog();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
            });
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "EditPassengerAddressDialog class, dismiss method");
        }
        dialog = null;
    }

}
