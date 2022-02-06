package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DBTripModel;
import ir.taxi1880.operatormanagement.databinding.DialogEditPassengerAddressBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class EditPassengerAddressDialog {

    private static final String TAG = EditPassengerAddressDialog.class.getSimpleName();
    DialogEditPassengerAddressBinding binding;

    public interface EditationCallBack {
        void onEdited(boolean success, String message);
    }

    EditationCallBack editationCallBack;
    private Spinner spCity;
    ArrayList<CityModel> cityModels;
    private String cityName = "";
    private String cityLatinName = "";
    private int cityCode;
    int type;

    static Dialog dialog;

    public void show(DBTripModel tripModel, EditationCallBack callBack) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogEditPassengerAddressBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.editationCallBack = callBack;
        cityCode = tripModel.getCity();

        MyApplication.handler.postDelayed(() -> initSpinner(cityCode), 500);

        if (tripModel.getOriginStation() == 0 && tripModel.getDestinationStation() == 0 && tripModel.getPriceable() != 0) {
            binding.edtDestinationAddress.setText(tripModel.getDestination());
            binding.edtOriginAddress.setText(tripModel.getOriginText());
        } else if (tripModel.getOriginStation() == 0) {
            binding.llDestinationAddress.setVisibility(View.GONE);
            binding.llDestinationStation.setVisibility(View.GONE);
            binding.txtOriginTitle.setText("آدرس");
            binding.txtOriginStTitle.setText("ایستگاه");
            binding.edtOriginAddress.setText(tripModel.getOriginText());
        } else if (tripModel.getDestinationStation() == 0) {
            if (tripModel.getPriceable() == 0) {
                binding.llDestinationAddress.setVisibility(View.GONE);
                binding.llDestinationStation.setVisibility(View.GONE);
                binding.txtOriginTitle.setText("آدرس");
                binding.txtOriginStTitle.setText("ایستگاه");
            } else {
                binding.llOriginAddress.setVisibility(View.GONE);
                binding.llOriginStation.setVisibility(View.GONE);
                binding.txtDestinationTitle.setText("آدرس");
                binding.txtDestinationStTitle.setText("ایستگاه");
                binding.edtDestinationAddress.setText(tripModel.getDestination());
            }
        }

        binding.imgClose.setOnClickListener(view -> dismiss());
        binding.llParent.setOnClickListener(view -> KeyBoardHelper.hideKeyboard());

        binding.btnSubmit.setOnClickListener(view -> {
            String destStationCode;
            String destAddress;
            String originStationCode;
            String originAddress;
            GeneralDialog dialog = new GeneralDialog();
            dialog.title("هشدار");
            dialog.message("ایا از انجام عملیات فوق اطمینان دارید؟");

            if (cityCode == -1) {
                MyApplication.Toast("لطفا شهر را انتخاب کنید.", Toast.LENGTH_SHORT);
                return;
            }
            if (tripModel.getOriginStation() == 0 && tripModel.getDestinationStation() == 0 && tripModel.getPriceable() != 0) {
                destAddress = binding.edtDestinationAddress.getText().toString();
                destStationCode = binding.edtDestinationStation.getText().toString();
                originAddress = binding.edtOriginAddress.getText().toString();
                originStationCode = binding.edtOriginStation.getText().toString();

                if (originAddress.isEmpty()) {
                    MyApplication.Toast("لطفا آدرس مبدا را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                if (originStationCode.isEmpty()) {
                    MyApplication.Toast("لطفا ایستگاه مبدا را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                if (destAddress.isEmpty()) {
                    MyApplication.Toast("لطفا آدرس مقصد را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                if (destStationCode.isEmpty()) {
                    MyApplication.Toast("لطفا ایستگاه مقصد را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                dialog.firstButton("بله", () -> editStation(cityCode, originAddress, destAddress, tripModel.getId() + "",
                        originStationCode, tripModel.getPriceable(), tripModel.getOperatorId(), destStationCode));

            } else if (tripModel.getOriginStation() == 0) {
                originAddress = binding.edtOriginAddress.getText().toString();
                originStationCode = binding.edtOriginStation.getText().toString();
                if (originAddress.isEmpty()) {
                    MyApplication.Toast("لطفا آدرس را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                if (originStationCode.isEmpty()) {
                    MyApplication.Toast("لطفا ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }

                dialog.firstButton("بله", () -> editStation(cityCode, originAddress, tripModel.getDestination(), tripModel.getId() + "", originStationCode, tripModel.getPriceable(), tripModel.getOperatorId(), tripModel.getDestinationStation() + ""));

            } else if (tripModel.getDestinationStation() == 0) {
                destAddress = binding.edtDestinationAddress.getText().toString();
                destStationCode = binding.edtDestinationStation.getText().toString();
                if (destAddress.isEmpty()) {
                    MyApplication.Toast("لطفا آدرس را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }
                if (destStationCode.isEmpty()) {
                    MyApplication.Toast("لطفا ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                    return;
                }

                dialog.firstButton("بله", () -> editStation(cityCode, tripModel.getOriginText(), destAddress, tripModel.getId() + "",
                        tripModel.getOriginStation() + "", tripModel.getPriceable(), tripModel.getOperatorId(), destStationCode));

            }
            dialog.secondButton("خیر", null);
            dialog.cancelable(false);
            dialog.show();

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
//            if (binding.spCity == null) return;
            binding.spCity.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, cityList));
            binding.spCity.setSelection(pos);
            binding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (spCity != null)
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
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
            AvaCrashReporter.send(e, TAG + " class, initCitySpinner method");
        }
    }

    private void editStation(int cityCode, String address, String destAddress, String serviceId, String stationCode, int priceable, int operatorId, String destStation) {

//        if (binding.vfLoader != null) {
            binding.vfLoader.setDisplayedChild(1);


        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.STATION)
                .addParam("tripId", StringHelper.toEnglishDigits(serviceId))
                .addParam("originStation", StringHelper.toEnglishDigits(stationCode + ""))
                .addParam("destStation", StringHelper.toEnglishDigits(destStation + ""))
                .addParam("cityCode", StringHelper.toEnglishDigits(cityCode + ""))
                .addParam("address", StringHelper.toEnglishDigits(address))
                .addParam("destAddress", StringHelper.toEnglishDigits(destAddress))
                .addParam("priceable", StringHelper.toEnglishDigits(priceable + ""))
                .addParam("tripOperatorId", StringHelper.toEnglishDigits(operatorId + ""))
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
                        editationCallBack.onEdited(true, "");
//                        if (status) {
                        dismiss();
//                        }
                    } else {
                        editationCallBack.onEdited(false, message);
                    }

//                    if (binding.vfLoader != null) {
                        binding.vfLoader.setDisplayedChild(0);


                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDialog.dismissCancelableDialog();
                    AvaCrashReporter.send(e, TAG + " class, onEditStation method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}