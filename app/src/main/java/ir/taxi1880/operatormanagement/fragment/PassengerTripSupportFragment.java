package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.TripAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentPassengerTripSupportBinding;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.ExtendedTimeDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.SearchFilterDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TripModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class PassengerTripSupportFragment extends Fragment {
    public static final String TAG = PassengerTripSupportFragment.class.getSimpleName();
    FragmentPassengerTripSupportBinding binding;
    ArrayList<TripModel> tripModels;
    TripAdapter tripAdapter;
    int searchCase = 2;
    int extendedTime = 1;
    Core core;
    String searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPassengerTripSupportBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.edtSearchTrip, MyApplication.IraSanSMedume);

        String tellNumber;
        Bundle bundle = getArguments();
        if (bundle != null) {
            tellNumber = bundle.getString("tellNumber");
            binding.edtSearchTrip.setText(tellNumber);
            onSearchPress();
        }

        binding.edtSearchTrip.requestFocus();
        binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);

        binding.edtSearchTrip.addTextChangedListener(searchWatcher);

        binding.edtSearchTrip.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                searchText = StringHelper.toEnglishDigits(binding.edtSearchTrip.getText().toString());
                if (searchText.isEmpty()) {
                    MyApplication.Toast("موردی را برای جستو جو وارد کنید", Toast.LENGTH_SHORT);
                    return false;
                }
                KeyBoardHelper.hideKeyboard();
                searchService(searchText, searchCase);
                return true;
            }
            return false;
        });

        if (MyApplication.prefManager.getConnectedCall()) {
            binding.imgEndCall.setBackgroundResource(R.drawable.bg_pink_edge);
        } else {
            binding.imgEndCall.setBackgroundResource(0);
        }

        binding.llExtendedTime.setOnClickListener(view -> new ExtendedTimeDialog().show((type, title, icon) -> {
            if (binding.txtExtendTime != null) {
                extendedTime = type;
                binding.txtExtendTime.setText(title);
                binding.imgExtendedTime.setImageResource(icon);
            }
        }));

        binding.imgSearchType.setOnClickListener(view -> new SearchFilterDialog().show("passenger", searchCase -> {
            if (binding.edtSearchTrip == null) return;
            int imageType = R.drawable.ic_call;
            switch (searchCase) {
                case 1: //search by name
                    imageType = R.drawable.ic_user;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case 2: //search by tell
                    imageType = R.drawable.ic_call;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 3: //search by address
                    imageType = R.drawable.ic_origin;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case 4: //search by taxi code
                    imageType = R.drawable.ic_taxi;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 5: //search by station code
                    imageType = R.drawable.ic_station_search;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 11: //search by address
                    imageType = R.drawable.ic_destination;
                    binding.edtSearchTrip.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
            }
            binding.imgSearchType.setImageResource(imageType);
            binding.edtSearchTrip.setText("");
            this.searchCase = searchCase;
        }));

        binding.imgClear.setOnLongClickListener(view -> {
            binding.edtSearchTrip.setText("");
            return true;
        });

        binding.imgSearch.setOnClickListener(view -> onSearchPress());

        binding.imgEndCall.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            if (MyApplication.prefManager.getConnectedCall()) {
                new CallDialog().show(new CallDialog.CallBack() {
                    @Override
                    public void onDismiss() {
                    }

                    @Override
                    public void onCallReceived() {
                    }

                    @Override
                    public void onCallTransferred() {
                    }

                    @Override
                    public void onCallEnded() {
                        if (binding.imgEndCall != null)
                            binding.imgEndCall.setBackgroundResource(0);
                    }
                }, true);
            } else {
                MyApplication.Toast("در حال حاضر تماسی برقرار نیست", Toast.LENGTH_SHORT);
            }
        });

        binding.txtCancel.setOnClickListener(view -> {
            if (binding.vfTrip != null) {
                binding.vfTrip.setDisplayedChild(3);
            }
        });

        binding.imgBack.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            MyApplication.currentActivity.onBackPressed();
        });

        return binding.getRoot();
    }

    public void onSearchPress() {
        searchText = StringHelper.toEnglishDigits(binding.edtSearchTrip.getText().toString());
        if (searchText.isEmpty()) {
            MyApplication.Toast("موردی را برای جستو جو وارد کنید", Toast.LENGTH_SHORT);
            return;
        }
        KeyBoardHelper.hideKeyboard();
        searchService(searchText, searchCase);
    }

    TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (PhoneNumberValidation.havePrefix(editable.toString()))
                binding.edtSearchTrip.setText(PhoneNumberValidation.removePrefix(editable.toString()));
        }
    };

    private void searchService(String searchText, int searchCase) {
        String phonenumber = "0";
        String driverPhone = "0";
        String name = "0";
        String address = "0";
        String taxiCode = "0";
        String stationCode = "0";
        String destinationAddress = "0";

        if (binding.vfTrip != null) {
            binding.vfTrip.setDisplayedChild(1);
        }
//        {post} /api/operator/v3/support/trip/v1/search
//
//        params:
//        {varchar(11)} phonenumber
//        {varchar(11)} driverPhone
//        {varchar(100)} name
//        {varchar(150)} address
//        {int} taxiCode
//        {int} stationCode
//        {tinyint} searchInterval 1= today , 2 = last day, 3 = last 2 day
//        {varchar(150)} destinationAddress
        switch (searchCase) {
            case 1:
                name = searchText;
                break;

            case 2:
                phonenumber = searchText;
                break;

            case 3:
                address = searchText;
                break;

            case 4:
                taxiCode = searchText;
                break;

            case 5:
                stationCode = searchText;
                break;

            case 11:
                destinationAddress = searchText;
                break;
        }
        RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                .ignore422Error(true)
                .addParam("phonenumber", phonenumber)
                .addParam("driverPhone", driverPhone)
                .addParam("name", name)
                .addParam("address", address)
                .addParam("taxiCode", taxiCode)
                .addParam("stationCode", stationCode)
                .addParam("destinationAddress", destinationAddress)
                .addParam("searchInterval", extendedTime)
                .listener(onGetTripList)
                .post();
    }

    RequestHelper.Callback onGetTripList = new RequestHelper.Callback() {

        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    Log.i("TAG", "run: " + args[0].toString());
                    tripModels = new ArrayList<>();
                    JSONObject tripObject = new JSONObject(args[0].toString());
                    boolean success = tripObject.getBoolean("success");
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
                            tripModel.setStatusText(dataObj.getString("statusDes"));
                            tripModel.setStatusColor(dataObj.getString("statusColor"));
                            tripModel.setPrice(dataObj.getString("servicePrice"));
                            tripModel.setDestStation(dataObj.getString("destinationStation"));
                            tripModel.setDestination(dataObj.getString("destinationAddress"));
                            tripModels.add(tripModel);
                        }

                        tripAdapter = new TripAdapter(tripModels);
                        if (binding.recycleTrip != null)
                            binding.recycleTrip.setAdapter(tripAdapter);

                        if (tripModels.size() == 0) {
                            if (binding.vfTrip != null)
                                binding.vfTrip.setDisplayedChild(0);
                        } else {
                            if (binding.vfTrip != null)
                                binding.vfTrip.setDisplayedChild(2);
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .firstButton("باشه", null)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetTripList onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
//       e = {"message":"Unprocessable Entity","data":[{"field":"stationCode","message":"کد ایستگاه صحیح نیست"}],"success":false}
                if (binding.vfTrip != null) {
                    binding.vfTrip.setDisplayedChild(3);
                }
            });
        }
    };

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            if (state == Call.State.End) {
                binding.imgEndCall.setBackgroundResource(0);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        core = LinphoneService.getCore();
        core.addListener(mCoreListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        core.removeListener(mCoreListener);
        core = null;
    }
}