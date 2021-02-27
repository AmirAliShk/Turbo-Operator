package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.TripAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
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
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class SupportDriverTripsFragment extends Fragment {
    Unbinder unbinder;
    ArrayList<TripModel> tripModels;
    TripAdapter tripAdapter;
    int searchCase = 2;
    int extendedTime = 1;
    Core core;
    String searchText;

    @OnClick(R.id.imgBack)
    void onBackPress() {
        KeyBoardHelper.hideKeyboard();
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.vfTrip)
    ViewFlipper vfTrip;

    @BindView(R.id.imgSearchType)
    ImageView imgSearchType;

    @BindView(R.id.imgEndCall)
    ImageView imgEndCall;

    @BindView(R.id.imgExtendedTime)
    ImageView imgExtendedTime;

    @BindView(R.id.txtExtendTime)
    TextView txtExtendTime;

    @BindView(R.id.txtCancel)
    TextView txtCancel;

    @OnClick(R.id.txtCancel)
    void txtCancel()  {
        if (vfTrip != null) {
            vfTrip.setDisplayedChild(3);
        }
    }

    @OnClick(R.id.imgEndCall)
    void onPressEndCall() {
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
                    if (imgEndCall != null)
                        imgEndCall.setBackgroundResource(0);
                }
            }, true);
        } else {
            MyApplication.Toast("در حال حاضر تماسی برقرار نیست", Toast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.imgSearch)
    void onSearchPress() {
        searchText = StringHelper.toEnglishDigits(edtSearchTrip.getText().toString());
        if (searchText.isEmpty()) {
            MyApplication.Toast("موردی را برای جستو جو وارد کنید", Toast.LENGTH_SHORT);
            return;
        }
        KeyBoardHelper.hideKeyboard();
        searchService(searchText, searchCase);
    }

    @OnLongClick(R.id.imgClear)
    boolean onLongPressClear() {
        edtSearchTrip.setText("");
        return true;
    }

    @OnClick(R.id.imgSearchType)
    void onSearchTypePress() {
        new SearchFilterDialog().show("driver",searchCase -> {
            if (edtSearchTrip == null) return;
            int imageType = R.drawable.ic_call;
            switch (searchCase) {
                case 6:
                    imageType = R.drawable.ic_call;
                    edtSearchTrip.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case 7:
                    imageType = R.drawable.ic_taxi;
                    edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
            imgSearchType.setImageResource(imageType);
            edtSearchTrip.setText("");
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
        View view = inflater.inflate(R.layout.fragment_support_driver_trip, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        TypefaceUtil.overrideFonts(edtSearchTrip, MyApplication.IraSanSMedume);

        String tellNumber;
        Bundle bundle = getArguments();
        if (bundle != null) {
            tellNumber = bundle.getString("tellNumber");
            edtSearchTrip.setText(tellNumber);
            onSearchPress();
        }

        edtSearchTrip.requestFocus();
        edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtSearchTrip.addTextChangedListener(searchWatcher);

        if (MyApplication.prefManager.getConnectedCall()) {
            imgEndCall.setBackgroundResource(R.drawable.bg_pink_edge);
        } else {
            imgEndCall.setBackgroundResource(0);
        }

        return view;
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
                edtSearchTrip.setText(PhoneNumberValidation.removePrefix(editable.toString()));
        }
    };

    private void searchService(String searchText, int searchCase) {
        if (vfTrip != null) {
            vfTrip.setDisplayedChild(1);
        }

        switch (searchCase) {

            case 0:
                RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                        .ignore422Error(true)
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
                        .ignore422Error(true)
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
                        .ignore422Error(true)
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
                        .ignore422Error(true)
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
                        .ignore422Error(true)
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
                        .ignore422Error(true)
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
            MyApplication.handler.post(() -> {
                try {
                    Log.i("TAG", "run: " + args[0].toString());
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
                            tripModel.setStatusText(dataObj.getString("statusDes"));
                            tripModel.setStatusColor(dataObj.getString("statusColor"));
                            tripModels.add(tripModel);
                        }

                        tripAdapter = new TripAdapter(tripModels);
                        if (recycleTrip != null)
                            recycleTrip.setAdapter(tripAdapter);

                        if (tripModels.size() == 0) {
                            if (vfTrip != null)
                                vfTrip.setDisplayedChild(0);
                        } else {
                            if (vfTrip != null)
                                vfTrip.setDisplayedChild(2);
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
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
//       e = {"message":"Unprocessable Entity","data":[{"field":"stationCode","message":"کد ایستگاه صحیح نیست"}],"success":false}
                if (vfTrip != null) {
                    vfTrip.setDisplayedChild(3);
                }
            });
        }

    };

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            if (state == Call.State.End) {
                imgEndCall.setBackgroundResource(0);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}