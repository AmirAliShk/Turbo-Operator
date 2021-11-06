package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import com.chaos.view.PinView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DBTripModel;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.EditPassengerAddressDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.PlayLastConversationDialog;
import ir.taxi1880.operatormanagement.dialog.SearchStationInfoDialog;
import ir.taxi1880.operatormanagement.dialog.StationInfoDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class DeterminationPageFragment extends Fragment {
    String TAG = DeterminationPageFragment.class.getSimpleName();
    String LOG = "STATION_REGISTER --> ";
    boolean doubleBackPressedOnce = false;
    Unbinder unbinder;
    boolean pressedRefresh = false;
    boolean isEnable = false;
    boolean callLastTime = false;
    boolean isFinished = false;
    boolean isFragmentOpen = false;
    boolean pressSubmit = false; // press twice for generate station Code
    DataBase dataBase;
    boolean isOriginZero = false;
    boolean isDestinationZero = false;
    boolean bothStationAreZero = false;
    Timer timer;
    DBTripModel tripModel;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.gridNumber)
    GridLayout gridNumber;

    @BindView(R.id.vfStationInfo)
    ViewFlipper vfStationInfo;

    @BindView(R.id.imgRefresh)
    ImageView imgRefresh;

    @BindView(R.id.pin)
    PinView pin;


    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.txtRemainingAddress)
    TextView txtRemainingAddress;

    @BindView(R.id.btnActivate)
    Button btnActivate;

    @BindView(R.id.btnDeActivate)
    Button btnDeActivate;

    @BindView(R.id.imgNextAddress)
    ImageView imgNextAddress;

    @OnClick(R.id.imgDelete)
    void onDelete() {
        pin.setText("");
    }

    @OnClick(R.id.btnSubmit)
    void onSubmit() {
        Log.i(TAG, "onSubmit: " + pin.getText().toString());
        if (pin.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
            return;
        }
        if (dataBase.getRemainingAddress() == 0) {
            MyApplication.Toast("آدرسی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
            pin.setText("");
        }

        String station = pin.getText().toString();
        String code = StringHelper.toEnglishDigits(station);

        if (pressSubmit) {
            if (bothStationAreZero) {
                dataBase.updateOriginStation(dataBase.getTopAddress().getId(), Integer.parseInt(station));
            } else if (isOriginZero) {
                dataBase.updateOriginStation(dataBase.getTopAddress().getId(), Integer.parseInt(station));
                Log.i(LOG, "onSubmit1: " + dataBase.getTopAddress().getOriginStation() + "/:" + dataBase.getTopAddress().getDestinationStation() + "/:" + dataBase.getTopAddress().getId());
                setStationCode(dataBase.getTopAddress().getOriginStation() + "", dataBase.getTopAddress().getDestinationStation() + "");
            } else if (isDestinationZero) {
                Log.i(LOG, "onSubmit:2 origin:" + txtAddress.getText());
                Log.i(LOG, "onSubmit:2 DB: origin" + dataBase.getTopAddress().getOriginText() + " DEst:" + dataBase.getTopAddress().getDestination());
                dataBase.updateDestinationStation(dataBase.getTopAddress().getId(), Integer.parseInt(station));
                Log.i(LOG, "onSubmit:2 " + dataBase.getTopAddress().getOriginStation() + "/: " + dataBase.getTopAddress().getDestinationStation() + "/:" + dataBase.getTopAddress().getId() + "" + isOriginZero);
                setStationCode(dataBase.getTopAddress().getOriginStation() + "", dataBase.getTopAddress().getDestinationStation() + "");
            }
            pin.setText("");
            txtAddress.setText(showAddress());
        } else {
            this.pressSubmit = true;
            MyApplication.handler.postDelayed(() -> pressSubmit = false, 300);
        }
    }

    @OnClick(R.id.imgSearch)
    void onSearch() {
        if (dataBase.getRemainingAddress() == 0) {
            if (pin.getText().toString().isEmpty()) {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), 0, false, "", true);
            } else {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), 0, false, StringHelper.toEnglishDigits(pin.getText().toString()), true);
            }
        } else {
            if (pin.getText().toString().isEmpty()) {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), dataBase.getTopAddress().getCity(), false, "", true);
            } else {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), dataBase.getTopAddress().getCity(), false, StringHelper.toEnglishDigits(pin.getText().toString()), true);
            }
        }

//        String origin = pin.getText().toString();
//        if (origin.isEmpty()) {
//            MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
//            return;
//        }
//        getStationInfo(origin);
    }

    @OnClick(R.id.imgSetMistake)
    void onSetMistake() {
        if (!MyApplication.prefManager.isStartGettingAddress()) {
            MyApplication.Toast("لطفا فعال شوید", Toast.LENGTH_SHORT);
            return;
        }
        if (dataBase.getRemainingAddress() == 0) {
            MyApplication.Toast("موردی برای ثبت موجود نیست", Toast.LENGTH_SHORT);
            return;
        }

        new GeneralDialog()
                .title("هشدار")
                .message("آیا از اشتباه بودن آدرس اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", () -> setMistake())
                .secondButton("خیر", null)
                .show();

    }

    @OnClick(R.id.imgPlayVoice)
    void onPressPlayVoice() {
        if (dataBase.getRemainingAddress() == 0) {
            MyApplication.Toast("مکالمه ای موجود نیست", Toast.LENGTH_SHORT);
            pin.setText("");
            return;
        }

        new PlayLastConversationDialog().show(dataBase.getTopAddress().getId(), EndPoints.CALL_VOICE + dataBase.getTopAddress().getVoipId());
    }

    @OnClick(R.id.imgNextAddress)
    void onNextAddress() {
        if (dataBase.getRemainingAddress() > 1) {
            dataBase.updateNextRecord(dataBase.getTopAddress().getId());
            txtAddress.setText(showAddress());
            //show text of next record
        } else {
            MyApplication.Toast("موردی برای نمایش موجود نیست", Toast.LENGTH_SHORT);
        }
    }

    @OnClick(R.id.llRefresh)
    void onPressRefresh() {
        if (!MyApplication.prefManager.isStartGettingAddress()) {
            MyApplication.Toast("لطفا فعال شوید", Toast.LENGTH_SHORT);
            return;
        }
        pressedRefresh = true;
        pin.setText("");
        imgRefresh.startAnimation(AnimationUtils.loadAnimation(MyApplication.context, R.anim.rotate));
        getAddressList();
//        MyApplication.handler.postDelayed(() -> getAddressList(), 500);

    }

    @OnClick(R.id.btnActivate)
    void onActivePress() {
        changeStatus(true);
    }

    @OnClick(R.id.btnDeActivate)
    void onDeActivePress() {
        changeStatus(false);
    }

    @OnClick(R.id.imgEdit)
    void onEdit() {
        if (dataBase.getRemainingAddress() == 0) {
            MyApplication.Toast("آدرسی موجود نیست...", Toast.LENGTH_SHORT);
            pin.setText("");
            return;
        }
        String originAddress = "";
        String destinationAddress = "";
        if (bothStationAreZero) {
            originAddress = dataBase.getTopAddress().getOriginText();
            destinationAddress = dataBase.getTopAddress().getDestination();
        } else if (isOriginZero) {
            originAddress = dataBase.getTopAddress().getOriginText();
        } else if (isDestinationZero) {
            destinationAddress = dataBase.getTopAddress().getDestination();
        }
        new EditPassengerAddressDialog().show(dataBase.getTopAddress(), (success, message) -> {
            if (success) {
                if (dataBase.getRemainingAddress() > 0)
                    dataBase.deleteRow(dataBase.getTopAddress().getId());
                txtAddress.setText(showAddress());
                if (pin != null)
                    pin.setText("");
            } else {
                new GeneralDialog()
                        .title("هشدار")
                        .message(message)
                        .secondButton("باشه", null)
                        .cancelable(false)
                        .show();
            }
        });
    }

    @OnClick(R.id.txtAddress)
    void onAddress() {
        if (doubleBackPressedOnce) {
            if (dataBase.getRemainingAddress() == 0) {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), 0, true, "", true);
            } else {
                new SearchStationInfoDialog().show(stationCode -> pin.setText(stationCode), dataBase.getTopAddress().getCity(), true, "", true);
            }
        } else {
            doubleBackPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackPressedOnce = false, 1500);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_determination_page, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        dataBase = new DataBase(MyApplication.context);

        dataBase.deleteAllData();
        changeStatus(MyApplication.prefManager.isStartGettingAddress());

        for (int numberCount = 0; numberCount < 10; numberCount++) {
            View grid = gridNumber.getChildAt(numberCount);
            int count = numberCount;
            grid.setOnClickListener(view1 -> {
                if (count == 9) {
                    if (pin.getText().toString().isEmpty()) return;
                    setNumber("0");
                } else {
                    setNumber(count + 1 + "");
                }
            });
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setNumber(String c) {
        String temp = pin.getText().toString();
        if (temp.length() == 3) {
//      pin.setText(StringHelper.toPersianDigits(temp.substring(0, 2) + c));
//      if (pin.getText().toString().indexOf(0)==0)return;
            if (c.equals("0")) {
                pin.setText("");
            } else {
                pin.setText(StringHelper.toPersianDigits(c));
            }
        } else {
//      if (pin.getText().toString().indexOf(0)==0)return;
            pin.setText(StringHelper.toPersianDigits(temp + c));
        }
    }

    private void getAddressList() {
        RequestHelper.builder(EndPoints.WITHOUT_STATION)
                .listener(withoutStationCallBack)
                .hideNetworkError(true)
                .get();
    }

    RequestHelper.Callback withoutStationCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    JSONArray dataArr = obj.getJSONArray("data");

                    if (success) {
                        if (pressedRefresh) {
                            if (imgRefresh != null)
                                imgRefresh.clearAnimation();
                            dataBase.deleteAllData();
                        }

                        if (dataArr.length() == 0) {
                            isFinished = true;
                            if (dataBase.getRemainingAddress() > 0) {
                                dataBase.deleteRemainingRecord(dataBase.getTopAddress().getId());
                                if (txtAddress != null)
                                    txtAddress.setText(showAddress());
                            } else {
                                dataBase.deleteAllData();
                                if (txtAddress == null) return;
                                if (!MyApplication.prefManager.isStartGettingAddress()) {
                                    if (txtAddress != null)
                                        txtAddress.setText("برای مشاهده آدرس ها فعال شوید");
                                } else {
                                    if (txtAddress != null)
                                        txtAddress.setText("آدرسی موجود نیست...");
                                }
                                if (txtRemainingAddress != null)
                                    txtRemainingAddress.setText("");
                            }
                        } else {
                            if (dataBase.getRemainingAddress() > 1)
                                dataBase.deleteRemainingRecord(dataBase.getTopAddress().getId());
                            for (int i = 0; i < dataArr.length(); i++) {
                                try {
                                    JSONObject dataObj = dataArr.getJSONObject(i);
                                    tripModel = new DBTripModel();
                                    tripModel.setId(dataObj.getInt("Id")); // the unique id for each trip
                                    tripModel.setPriceable(dataObj.getInt("priceable")); // if this value was 0, no need to set destination.
                                    String content = dataObj.getString("Content");
                                    JSONObject contentObj = new JSONObject(content);
                                    tripModel.setOperatorId(contentObj.getInt("userId")); // ID of the person who registered the service
                                    tripModel.setCity(contentObj.getInt("cityCode"));
                                    tripModel.setCustomerName(contentObj.getString("callerName"));
                                    tripModel.setTell(contentObj.getString("phoneNumber"));
                                    tripModel.setMobile(contentObj.getString("mobile"));
                                    tripModel.setVoipId(contentObj.getString("voipId"));
                                    tripModel.setOriginText(contentObj.getString("address"));
                                    tripModel.setOriginStation(contentObj.getInt("stationCode"));
                                    tripModel.setDestinationStation(contentObj.getInt("destinationStation"));
                                    tripModel.setDestination(contentObj.getString("destination"));
                                    tripModel.setSaveDate(dataObj.getString("SaveDate"));//date and time of service registered by Tehran timeZone,"SaveDate":"2021-03-01T12:24:42.820Z"
                                    dataBase.insertTripRow(tripModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (imgRefresh != null)
                                        imgRefresh.clearAnimation();
                                }
                            }

                            if (txtRemainingAddress != null)
                                txtRemainingAddress.setText("" + dataBase.getRemainingAddress());

                            // I can't put setAddress() function here! because I want set address just when the user is enable and is disable and press refresh.
                            // Do you think it never crossed my mind?! ;)

                            Log.i(LOG, "withoutStation:OriginStation = " +
                                    dataBase.getTopAddress().getOriginStation() +
                                    " DestinationStation= " + dataBase.getTopAddress().getDestinationStation() +
                                    " Priceable= " + dataBase.getTopAddress().getPriceable() +
                                    " serviceId= " + dataBase.getTopAddress().getId());

                            if (isEnable) {
                                if (txtAddress != null)
                                    txtAddress.setText(showAddress());
                                isEnable = false;
                            }

                            if (isFinished) {
                                if (txtAddress != null)
                                    txtAddress.setText(showAddress());
                                isFinished = false;
                            }

                            if (pressedRefresh) {
                                if (txtAddress != null)
                                    txtAddress.setText(showAddress());
                                pressedRefresh = false;
                            }

                        }
                    } else {
                        if (isFragmentOpen) {
                            new GeneralDialog()
                                    .title("خطا")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isFinished = false;
                    pressedRefresh = false;
                    if (imgRefresh != null)
                        imgRefresh.clearAnimation();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                isFinished = false;
                pressedRefresh = false;
                if (imgRefresh != null)
                    imgRefresh.clearAnimation();

            });
        }

    };

    private void changeStatus(boolean status) {
        if (status) {
            isEnable = true;
            callLastTime = true;
            txtRemainingAddress.setText("");
            txtAddress.setText("آدرسی موجود نیست...");
            startGetAddressTimer();
            MyApplication.prefManager.setStartGettingAddress(true);
            if (btnActivate != null)
                btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            if (btnDeActivate != null) {
                btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
                btnDeActivate.setTextColor(Color.parseColor("#ffffff"));//todo
            }
        } else {
            dataBase.deleteAllData();
            MyApplication.prefManager.setStartGettingAddress(false);
            if (callLastTime) {
                getAddressList();
            } else {// From the outside to the inside of the page
                txtAddress.setText("برای مشاهده آدرس ها فعال شوید");
                txtRemainingAddress.setText("");
            }
            callLastTime = false;
            isEnable = false;
            stopGetAddressTimer();
            if (btnActivate != null)
                btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            if (btnDeActivate != null) {
                btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
                btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    private void setStationCode(String stationCode, String destinationCode) {
        Log.i(LOG, "setStationCode:OriginStation = " +
                stationCode +
                " DestinationStation= " + destinationCode +
                " Priceable= " + dataBase.getTopAddress().getPriceable() +
                " serviceId= " + dataBase.getTopAddress().getId());
        RequestHelper.builder(EndPoints.STATION)
                .addParam("tripId", StringHelper.toEnglishDigits(dataBase.getTopAddress().getId() + ""))
                .addParam("originStation", StringHelper.toEnglishDigits(stationCode + ""))
                .addParam("destStation", StringHelper.toEnglishDigits(destinationCode + ""))
                .addParam("cityCode", StringHelper.toEnglishDigits(dataBase.getTopAddress().getCity() + ""))
                .addParam("address", StringHelper.toEnglishDigits(dataBase.getTopAddress().getOriginText() + ""))
                .addParam("destAddress", StringHelper.toEnglishDigits(dataBase.getTopAddress().getDestination() + ""))
                .addParam("priceable", StringHelper.toEnglishDigits(dataBase.getTopAddress().getPriceable() + ""))
                .addParam("tripOperatorId", StringHelper.toEnglishDigits(dataBase.getTopAddress().getOperatorId() + ""))
                .listener(setStationCode)
                .put();


        if (dataBase.getRemainingAddress() > 0)
            dataBase.deleteRow(dataBase.getTopAddress().getId());

        txtAddress.setText(showAddress());
        pin.setText("");
    }

    RequestHelper.Callback setStationCode = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
//                    {"success":true,"message":"کد ایستگاه در این شهر وجود ندارد","data":{"status":false}}

//                    Log.i(LOG, "onResponse: " + args[0].toString());
//                    JSONObject obj = new JSONObject(args[0].toString());

//                    boolean success = obj.getBoolean("success");
//                    String message = obj.getString("message");
                    if (dataBase.getRemainingAddress() < 1)
                        getAddressList();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {

            });
        }
    };

    private void setMistake() {
        String tripTime = DateHelper.strPersianFour1(DateHelper.parseFormat(dataBase.getTopAddress().getSaveDate() + "", null));
        String tripDate = DateHelper.strPersianSeven(DateHelper.parseFormat(dataBase.getTopAddress().getSaveDate() + "", null));

        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.MISTAKE)
                .addParam("serviceId", StringHelper.toEnglishDigits(dataBase.getTopAddress().getId() + ""))
                .addParam("phone", StringHelper.toEnglishDigits(dataBase.getTopAddress().getTell()))
                .addParam("mobile", StringHelper.toEnglishDigits(dataBase.getTopAddress().getMobile()))
                .addParam("tripUser", StringHelper.toEnglishDigits(dataBase.getTopAddress().getOperatorId() + ""))
                .addParam("cityId", StringHelper.toEnglishDigits(dataBase.getTopAddress().getCity() + ""))
                .addParam("tripStation", StringHelper.toEnglishDigits(dataBase.getTopAddress().getOriginStation() + ""))
                .addParam("tripDate", tripDate)
                .addParam("tripTime", tripTime)
                .addParam("adrs", StringHelper.toEnglishDigits(dataBase.getTopAddress().getOriginText()))
                .addParam("customerName", StringHelper.toEnglishDigits(dataBase.getTopAddress().getCustomerName()))
                .addParam("voipId", StringHelper.toEnglishDigits(dataBase.getTopAddress().getVoipId()))
                .addParam("description", " ")
                .addParam("destinationAddress", StringHelper.toEnglishDigits(dataBase.getTopAddress().getDestination() + ""))
                .addParam("destinationStation", StringHelper.toEnglishDigits(dataBase.getTopAddress().getDestinationStation() + ""))
                .listener(setMistake)
                .post();
    }

    RequestHelper.Callback setMistake = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    { success: true, message: "ثبت اشتباه، تکراری است", data: { status: false } }
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("تایید")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .title("خطا")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        }
                    }

                } catch (JSONException e) {
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

    private void getStationInfo(String stationCode) {
        if (vfStationInfo != null)
            vfStationInfo.setDisplayedChild(1);
        KeyBoardHelper.hideKeyboard();
        RequestHelper.builder(EndPoints.STATION_INFO)
                .addPath(StringHelper.toEnglishDigits(stationCode) + "")
                .listener(getStationInfo)
                .get();
    }

    RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (vfStationInfo != null)
                        vfStationInfo.setDisplayedChild(0);

                    KeyBoardHelper.hideKeyboard();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONArray dataArr = obj.getJSONArray("data");
                        if (dataArr.length() == 0) {
                            MyApplication.Toast("اطلاعاتی موجود نیست", Toast.LENGTH_SHORT);
                            return;
                        }
                        new StationInfoDialog().show(dataArr);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (vfStationInfo != null)
                        vfStationInfo.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfStationInfo != null)
                    vfStationInfo.setDisplayedChild(0);
            });
        }
    };

    private String showAddress() {
        try {
            isDestinationZero = false;
            isOriginZero = false;
            bothStationAreZero = false;
            if (txtAddress == null) return "";
            if (txtRemainingAddress == null) return "";

            if (dataBase.getRemainingAddress() == 0) {
                if (txtRemainingAddress != null)
                    txtRemainingAddress.setText("");
                if (!MyApplication.prefManager.isStartGettingAddress()) {
                    return "برای مشاهده آدرس ها فعال شوید";
                } else {
                    return "آدرسی موجود نیست...";
                }
            } else {
                if (txtRemainingAddress != null)
                    txtRemainingAddress.setText(dataBase.getRemainingAddress() + "");
                String cityName = dataBase.getCityName2(dataBase.getTopAddress().getCity());

                if (dataBase.getTopAddress().getOriginStation() == 0 && dataBase.getTopAddress().getDestinationStation() == 0 && dataBase.getTopAddress().getPriceable() == 1) {
                    bothStationAreZero = true;
                    Log.i(LOG, "showAddress: bothStationAreZero " + dataBase.getTopAddress().getOriginText() + " destination = " + dataBase.getTopAddress().getDestination());
                    return cityName + " , " + dataBase.getTopAddress().getOriginText();
                }

                if (dataBase.getTopAddress().getOriginStation() == 0) {
                    isOriginZero = true;
                    Log.i(LOG, "showAddress: isOriginZero " + dataBase.getTopAddress().getOriginText());
                    return cityName + " , " + dataBase.getTopAddress().getOriginText();
                }

                if (dataBase.getTopAddress().getDestinationStation() == 0 && dataBase.getTopAddress().getPriceable() == 1) {
                    isDestinationZero = true;
                    Log.i(LOG, "showAddress: isDestinationZero " + dataBase.getTopAddress().getDestination());
                    return cityName + " , " + dataBase.getTopAddress().getDestination();
                }

                Log.i(LOG, "showAddress:OriginStation = " +
                        dataBase.getTopAddress().getOriginStation() +
                        " DestinationStation= " + dataBase.getTopAddress().getDestinationStation() +
                        " Priceable= " + dataBase.getTopAddress().getPriceable() +
                        " serviceId= " + dataBase.getTopAddress().getId());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void startGetAddressTimer() {
        try {
            if (timer != null) {
                return;
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (dataBase.getRemainingAddress() < 1)
                        getAddressList();
                }
            }, 0, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopGetAddressTimer() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        stopGetAddressTimer();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentOpen = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentOpen = false;
    }
}