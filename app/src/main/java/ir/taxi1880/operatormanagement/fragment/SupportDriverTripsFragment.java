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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.core.content.ContextCompat;
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
import ir.taxi1880.operatormanagement.adapter.DriverTripsAdapter;
import ir.taxi1880.operatormanagement.adapter.TripAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.ChangeDriverQueueDialog;
import ir.taxi1880.operatormanagement.dialog.DriverInfoDialog;
import ir.taxi1880.operatormanagement.dialog.DriverStationRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.DriverTurnoverDialog;
import ir.taxi1880.operatormanagement.dialog.ExtendedTimeDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.SearchFilterDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.PhoneNumberValidation;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TripModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class SupportDriverTripsFragment extends Fragment {
    Unbinder unbinder;
    View view;
    ArrayList<TripModel> tripModels;
    DriverTripsAdapter tripAdapter;
    int searchCase = 6;
    int extendedTime = 1;
    private int mDisplayedQuality = -1;
    private Runnable mCallQualityUpdater = null;
    Core core;
    String searchText;
    String driverInfo;
    String taxiCode = "";
    String carCode = "";

    @OnClick(R.id.imgBack)
    void onBackPress() {
        KeyBoardHelper.hideKeyboard();
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.vfTrip)
    ViewFlipper vfTrip;

    @OnClick(R.id.imgChangeDriverQueue)
    void onPressChangeDriverQueue() {
        KeyBoardHelper.showKeyboard(MyApplication.context);
        new ChangeDriverQueueDialog().show(taxiCode);
    }

    @OnClick(R.id.imgStationInfo)
    void onPressStationInfo() {
        new DriverStationRegistrationDialog().show(taxiCode);
    }

    @OnClick(R.id.imgFinancial)
    void onPressFinancial() {
        new DriverTurnoverDialog().show(taxiCode, carCode);
    }

    @OnClick(R.id.imgDriverInfo)
    void onPressDriverInfo() {
        new DriverInfoDialog().show(driverInfo);
    }

    @OnClick(R.id.imgDriverLocation)
    void onPressDriverLocation() {
        if (taxiCode.isEmpty()) {
            MyApplication.Toast("خطا در دریافت اطلاعات لظفا بعدا تلاش کنید.", Toast.LENGTH_SHORT);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("taxiCode", taxiCode);
            bundle.putBoolean("isFromDriverSupport", true);
            FragmentHelper.toFragment(MyApplication.currentActivity, new DriverLocationFragment()).setArguments(bundle).add();
        }
    }

    @BindView(R.id.imgSearchType)
    ImageView imgSearchType;

    @BindView(R.id.imgEndCall)
    ImageView imgEndCall;

    @BindView(R.id.imgCallQuality)
    ImageView imgCallQuality;

    @BindView(R.id.imgExtendedTime)
    ImageView imgExtendedTime;

    @BindView(R.id.txtExtendTime)
    TextView txtExtendTime;

    @BindView(R.id.txtCancel)
    TextView txtCancel;

    @OnClick(R.id.txtCancel)
    void txtCancel() {
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
                }
            }, false);
        }
    }

    @OnClick(R.id.imgSearch)
    void onSearchPress() {
        if (view != null) {
            llDriverInfo.setVisibility(View.GONE);
        }
        searchText = StringHelper.toEnglishDigits(edtSearchTrip.getText().toString());
        if (searchText.isEmpty()) {
            MyApplication.Toast("موردی را برای جستو جو وارد کنید", Toast.LENGTH_SHORT);
            return;
        }
        KeyBoardHelper.hideKeyboard();
        searchService(searchText, searchCase);

        if (searchCase == 8 || searchCase == 9) // 9=search by station code, 8=search by address
            return;
        getDriverInfo(searchText, searchCase);
    }

    @OnLongClick(R.id.imgClear)
    boolean onLongPressClear() {
        if (vfTrip != null) {
            vfTrip.setDisplayedChild(0);
            llDriverInfo.setVisibility(View.GONE);
        }
        edtSearchTrip.setText("");
        return true;
    }

    @OnClick(R.id.imgSearchType)
    void onSearchTypePress() {
        new SearchFilterDialog().show("driver", searchCase -> {
            if (edtSearchTrip == null) return;
            int imageType = R.drawable.ic_call;
            switch (searchCase) {
                case 6: // driver mobile
                    imageType = R.drawable.ic_call;
                    edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 7: // taxi code
                    imageType = R.drawable.ic_taxi;
                    edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 8: // driver address
                    imageType = R.drawable.ic_gps;
                    edtSearchTrip.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case 9: // station code
                    imageType = R.drawable.ic_station_report;
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

    @BindView(R.id.llDriverInfo)
    LinearLayout llDriverInfo;

    @BindView(R.id.txtDriverName)
    TextView txtDriverName;

    @BindView(R.id.txtDriverCode)
    TextView txtDriverCode;

    @BindView(R.id.txtDriverQueue)
    TextView txtDriverQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_support_driver_trip, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        TypefaceUtil.overrideFonts(llDriverInfo, MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(edtSearchTrip, MyApplication.IraSanSMedume);

        String tellNumber;
        Bundle bundle = getArguments();
        if (bundle != null) {
            tellNumber = bundle.getString("number");
            edtSearchTrip.setText(tellNumber);
            onSearchPress();
        }

        edtSearchTrip.requestFocus();
        edtSearchTrip.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtSearchTrip.addTextChangedListener(searchWatcher);

        edtSearchTrip.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                searchText = StringHelper.toEnglishDigits(edtSearchTrip.getText().toString());
                if (searchText.isEmpty()) {
                    MyApplication.Toast("موردی را برای جستو جو وارد کنید", Toast.LENGTH_SHORT);
                    return false;
                }
                KeyBoardHelper.hideKeyboard();
                getDriverInfo(searchText, searchCase);
                searchService(searchText, searchCase);
                return true;
            }
            return false;
        });

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

            case 6: // search by driver mobile
                RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                        .ignore422Error(true)
                        .addParam("phonenumber", 0)
                        .addParam("driverPhone", searchText)
                        .addParam("name", 0)
                        .addParam("address", 0)
                        .addParam("taxiCode", 0)
                        .addParam("stationCode", 0)
                        .addParam("searchInterval", extendedTime)
                        .listener(onGetTripList)
                        .post();
                break;

            case 7: //search by taxi code
                RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                        .ignore422Error(true)
                        .addParam("phonenumber", 0)
                        .addParam("driverPhone", 0)
                        .addParam("name", 0)
                        .addParam("address", 0)
                        .addParam("taxiCode", searchText)
                        .addParam("stationCode", 0)
                        .addParam("searchInterval", extendedTime)
                        .listener(onGetTripList)
                        .post();
                break;

            case 8: //search by address
                RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                        .ignore422Error(true)
                        .addParam("phonenumber", 0)
                        .addParam("driverPhone", 0)
                        .addParam("name", 0)
                        .addParam("address", searchText)
                        .addParam("taxiCode", 0)
                        .addParam("stationCode", 0)
                        .addParam("searchInterval", extendedTime)
                        .listener(onGetTripList)
                        .post();
                break;

            case 9: //search by station code
                RequestHelper.builder(EndPoints.SEARCH_SERVICE)
                        .ignore422Error(true)
                        .addParam("phonenumber", 0)
                        .addParam("driverPhone", 0)
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
                            try {
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
                            } catch (Exception ignored) {
                            }
                        }

                        tripAdapter = new DriverTripsAdapter(tripModels);
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
                    if (vfTrip != null) {
                        vfTrip.setDisplayedChild(3);
                    }
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

    private void getDriverInfo(String searchText, int searchCase) {
        if (vfTrip != null) {
            vfTrip.setDisplayedChild(1);
        }

        switch (searchCase) {
            case 6: // driver mobile
                RequestHelper.builder(EndPoints.DRIVER_INFO)
                        .ignore422Error(true)
                        .addPath("0")
                        .addPath(searchText)
                        .listener(onGetDriverInfo)
                        .get();
                break;
            case 7: // taxi code
                RequestHelper.builder(EndPoints.DRIVER_INFO)
                        .ignore422Error(true)
                        .addPath(searchText)
                        .addPath("0")
                        .listener(onGetDriverInfo)
                        .get();
                break;
        }

    }

    RequestHelper.Callback onGetDriverInfo = new RequestHelper.Callback() {

        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    Boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        if (view != null) {
                            llDriverInfo.setVisibility(View.VISIBLE);
                        }

                        JSONObject dataObj = object.getJSONObject("data");
                        JSONObject infoObj = dataObj.getJSONObject("info");
                        driverInfo = infoObj.toString();
                        int cityCode = infoObj.getInt("cityCode");
                        taxiCode = infoObj.getString("driverCode");
                        carCode = infoObj.getString("carCode");
                        int smartCode = infoObj.getInt("smartCode");
                        String driverName = infoObj.getString("driverName");
                        int smartTaximeter = infoObj.getInt("smartTaximeter");
                        int carClass = infoObj.getInt("carClass");
                        int gender = infoObj.getInt("gender");
                        int confirmation = infoObj.getInt("confirmation");
                        String nationalCode = infoObj.getString("nationalCode");
                        String fatherName = infoObj.getString("fatherName");
                        String vin = infoObj.getString("vin");
                        String sheba = infoObj.getString("sheba");
                        String shenasname = infoObj.getString("shenasname");
                        int fuelRationing = infoObj.getInt("fuelRationing");
                        int cancelFuel = infoObj.getInt("cancelFuel");
                        String startActiveDate = infoObj.getString("startActiveDate");

                        JSONObject registrationObj = dataObj.getJSONObject("registration");
                        int status = registrationObj.getInt("status");
                        int station = registrationObj.getInt("station");
                        int dist = registrationObj.getInt("dist");
                        int turn = registrationObj.getInt("turn");
                        String futureTime = registrationObj.getString("futureTime");
                        String activeTime = registrationObj.getString("activeTime");
                        String lat = registrationObj.getString("lat");
                        String lng = registrationObj.getString("lng");
                        String borderLimit = registrationObj.getString("borderLimit");

                        String statusMessage = "";

                        if (view != null) {
                            txtDriverName.setText(driverName);
                            txtDriverCode.setText(StringHelper.toPersianDigits(taxiCode + ""));
                            switch (status) {
                                case 1:
                                    statusMessage = " نفر " + turn + " در ایستگاه " + station;
                                    break;

                                case 2:
                                    statusMessage = "ثبت آینده در ایستگاه " + station + " مدت زمان :" + futureTime;
                                    break;

                                case 3:
                                    statusMessage = "در حال سرویس دهی";
                                    break;

                                case 4:
                                    statusMessage = "ثبت ایستگاه نشده";
                                    break;

                                case 5:
                                    statusMessage = "فعال نیست";
                                    break;
                            }
                            txtDriverQueue.setText(statusMessage);
                        }
                    } else {
                        if (view != null) {
                            llDriverInfo.setVisibility(View.GONE);
                        }
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (view != null) {
                        llDriverInfo.setVisibility(View.GONE);
                        MyApplication.Toast("خطا در دریافت اطلاعات راننده", Toast.LENGTH_SHORT);
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (view != null) {
                    llDriverInfo.setVisibility(View.GONE);
                    MyApplication.Toast("خطا در دریافت اطلاعات راننده", Toast.LENGTH_SHORT);
                }
            });
        }

    };

    CoreListenerStub mCoreListener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {
            if (state == Call.State.End) {
                if (imgCallQuality != null)
                    imgCallQuality.setVisibility(View.INVISIBLE);
                imgEndCall.setBackgroundResource(0);
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            } else if (state == Call.State.Released) {
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater);
                    mCallQualityUpdater = null;
                }
            }
        }
    };

    private void updateQualityOfSignalIcon(float quality) {
        int iQuality = (int) quality;

        int imageRes = R.drawable.ic_quality_0;

        if (iQuality == mDisplayedQuality) return;
        if (quality >= 4) { // Good Quality
            imageRes = R.drawable.ic_quality_4;
        } else if (quality >= 3) {// Average quality
            imageRes = (R.drawable.ic_quality_3);
        } else if (quality >= 2) { // Low quality
            imageRes = (R.drawable.ic_quality_2);
        } else if (quality >= 1) { // Very low quality
            imageRes = (R.drawable.ic_quality_1);
        }
        if (imgCallQuality != null) {
            imgCallQuality.setVisibility(View.VISIBLE);
            imgCallQuality.setImageResource(imageRes);
        }
        mDisplayedQuality = iQuality;
    }

    private void startCallQuality() {
        if (mCallQualityUpdater == null)
            LinphoneService.dispatchOnUIThreadAfter(
                    mCallQualityUpdater =
                            new Runnable() {
                                final Call mCurrentCall = LinphoneService.getCore().getCurrentCall();

                                public void run() {
                                    if (mCurrentCall == null) {
                                        mCallQualityUpdater = null;
                                        return;
                                    }
                                    float newQuality = mCurrentCall.getCurrentQuality();
                                    updateQualityOfSignalIcon(newQuality);

                                    if (MyApplication.prefManager.getConnectedCall())
                                        LinphoneService.dispatchOnUIThreadAfter(this, 1000);
                                }
                            },
                    1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        core = LinphoneService.getCore();
        core.addListener(mCoreListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCallQuality();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyBoardHelper.hideKeyboard();
        core.removeListener(mCoreListener);
        core = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}