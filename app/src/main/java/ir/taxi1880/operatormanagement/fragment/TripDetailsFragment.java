package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.ComplaintRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.DriverLockDialog;
import ir.taxi1880.operatormanagement.dialog.ErrorAddressDialog;
import ir.taxi1880.operatormanagement.dialog.ErrorRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.LostDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class TripDetailsFragment extends Fragment {
    Unbinder unbinder;
    String serviceId;
    String passengerPhone;
    String customerMobile;
    String passengerName;
    String passengerAddress = "";
    String taxiCode;
    String description;
    String voipId;
    double lat = 0;
    double lng = 0;
    String lastPositionTime = "";
    String lastPositionDate = "";
    String carMobile;
    String driverMobile;
    String stationCode;
    int userId;
    String callDate;
    String callTime;
    String cityName;
    int cityCode;
    String price;
    String destinationStation;
    String destination;
    String serviceDetails;

    @OnClick(R.id.imgBack)
    void onBackPress() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.txtTimeToCome)
    TextView txtTimeToCome;
    @BindView(R.id.txtStatus)
    TextView txtStatus;

    @BindView(R.id.llHeaderStatus)
    LinearLayout llHeaderStatus;

    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;

    @BindView(R.id.txtUserCodeDestination)
    TextView txtUserCodeDestination;

    @BindView(R.id.txtUserCodeOrigin)
    TextView txtUserCodeOrigin;

    @BindView(R.id.txtDate)
    TextView txtDate;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.txtTripType)
    TextView txtTripType;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtCustomerAddress)
    TextView txtCustomerAddress;

    @BindView(R.id.txtCustomerTell)
    TextView txtCustomerTell;

    @BindView(R.id.txtCustomerMobile)
    TextView txtCustomerMobile;

    @BindView(R.id.txtPercent)
    TextView txtPercent;

    @BindView(R.id.txtMaxPercent)
    TextView txtMaxPercent;

    @BindView(R.id.txtSendDate)
    TextView txtSendDate;

    @BindView(R.id.txtSendTime)
    TextView txtSendTime;

    @BindView(R.id.txtDriverCode)
    TextView txtDriverCode;

    @BindView(R.id.txtDriverName)
    TextView txtDriverName;

    @BindView(R.id.txtDriverMob)
    TextView txtDriverMob;

    @BindView(R.id.txtCarType)
    TextView txtCarType;

    @BindView(R.id.txtPrice)
    TextView txtPrice;

    @BindView(R.id.txtEndTime)
    TextView txtEndTime;

    @BindView(R.id.txtDestStation)
    TextView txtDestStation;

    @BindView(R.id.txtDestAddress)
    TextView txtDestAddress;

    @BindView(R.id.txtPlaque)
    TextView txtPlaque;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.txtStationCode)
    TextView txtStationCode;

    @BindView(R.id.txtTrafficPlan)
    TextView txtTrafficPlan;

    @BindView(R.id.txtServiceComment)
    TextView txtServiceComment;

    @BindView(R.id.txtNull)
    TextView txtNull;

    @BindView(R.id.txtServiceFixedComment)
    TextView txtServiceFixedComment;

    @BindView(R.id.vfTripDetails)
    ViewFlipper vfTripDetails;

    @BindView(R.id.btnDriverLock)
    Button btnDriverLock;

    @BindView(R.id.btnComplaintRegistration)
    Button btnComplaintRegistration;

    @BindView(R.id.btnLost)
    Button btnLost;

    @BindView(R.id.btnReFollow)
    Button btnReFollow;

    @BindView(R.id.btnErrorRegistration)
    Button btnErrorRegistration;

    @BindView(R.id.btnCancelTrip)
    Button btnCancelTrip;

    @BindView(R.id.btnDriverLocation)
    Button btnDriverLocation;

    @BindView(R.id.imgEndCall)
    ImageView imgEndCall;

    @OnClick(R.id.btnDisposal)
    void onDisposal() {
        new GeneralDialog()
                .title("تبدیل به در اختیار")
                .message("آیا از در اختیار کردن این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", () -> makeDisposal())
                .secondButton("خیر", null)
                .show();
    }

    @OnClick(R.id.btnCancelTrip)
    void onCancel() {
        new GeneralDialog()
                .title("لغو سفر")
                .message("آیا از کنسل کردن این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", () -> cancelService())
                .secondButton("خیر", null)
                .show();
    }

    @OnClick(R.id.btnArchiveAddress)
    void onArchiveAddress() {
        new GeneralDialog()
                .title("بایگانی آدرس")
                .message("آیا اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::archiveAddress)
                .secondButton("خیر", null)
                .show();
    }

    @OnClick(R.id.btnEditAddress)
    void onEditAddress() {
        new ErrorAddressDialog().show(passengerAddress, serviceId);
    }

    @OnClick(R.id.btnDriverLocation)
    void onLocation() {
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        bundle.putString("time", lastPositionTime);
        bundle.putString("date", lastPositionDate);
        bundle.putString("taxiCode", taxiCode);
        FragmentHelper.toFragment(MyApplication.currentActivity, new DriverLocationFragment()).setArguments(bundle).add();
    }

    @OnClick(R.id.btnReFollow)
    void onReFollow() {
        new GeneralDialog()
                .title("پیگیری مجدد")
                .message("آیا از پیگیری مجدد این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", () -> trackingAgain())
                .secondButton("خیر", null)
                .show();
    }

    @OnClick(R.id.btnErrorRegistration)
    void onError() {
//        String cityName= new DataBase(MyApplication.context).getCityName2(cityCode);
        new ErrorRegistrationDialog()
                .show(serviceId, passengerPhone, customerMobile, passengerAddress, passengerName, voipId, cityCode, stationCode, userId, callTime, callDate, price, destinationStation, destination);
    }

    @OnClick(R.id.btnComplaintRegistration)
    void onComplaint() {
        new ComplaintRegistrationDialog().show(serviceId, voipId);
    }

    @OnClick(R.id.btnLost)
    void onLost() {
        new LostDialog().show(serviceId, passengerName, passengerPhone, taxiCode, false);
    }

    @OnClick(R.id.btnDriverLock)
    void onLock() {
        new DriverLockDialog().show(taxiCode);
    }

//    @OnClick(R.id.btnResendService)
//    void onResendService() {
//        new GeneralDialog()
//                .message("آیا میخواهید سرویس را مجدد ارسال کنید؟")
//                .firstButton("بله", this::resendCancelService)
//                .secondButton("خیر ", null)
//                .show();
//    }

    @OnClick(R.id.rlEndCall)
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
//    Call call = LinphoneService.getCore().getCurrentCall();
//    call.terminate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(txtTitle);
        TypefaceUtil.overrideFonts(txtNull);

        Bundle bundle = getArguments();
        if (bundle != null) {
            serviceId = bundle.getString("id");
        }

        tripDetails();

        return view;
    }

    private void tripDetails() {
        if (vfTripDetails != null) {
            vfTripDetails.setDisplayedChild(0);
        }

        RequestHelper.builder(EndPoints.SERVICE_DETAIL)
                .addParam("serviceId", serviceId)
                .listener(onGetTripDetails)
                .post();
    }

    RequestHelper.Callback onGetTripDetails = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject tripObject = new JSONObject(args[0].toString());
                    Boolean success = tripObject.getBoolean("success");
                    String message = tripObject.getString("message");
//                    JSONObject data = tripObject.getJSONObject("data");

                    if (success) {
                        JSONObject data = tripObject.getJSONObject("data");
                        serviceDetails = tripObject.getJSONObject("data").toString();
                        serviceId = data.getString("serviceId");
                        int status = data.getInt("Status");
                        callDate = data.getString("callDate");
                        callTime = data.getString("callTime");
                        String sendDate = data.getString("SendDate");
                        String sendTime = data.getString("SendTime");
                        stationCode = data.getString("stationCode");
                        price = data.getString("Price");
                        String finishdate = data.getString("Finishdate");
                        String finishTime = data.getString("FinishTime");
                        taxiCode = data.getString("taxicode");
                        String driverId = data.getString("driverId");
                        userId = data.getInt("UserId");
                        String perDiscount = data.getString("PerDiscount");
                        String discountAmount = data.getString("discountAmount");
                        String rewardCode = data.getString("RewardCode");
                        String maxDiscount = data.getString("MaxDiscount");
                        passengerName = data.getString("customerName");
                        passengerPhone = data.getString("customerTel");
                        customerMobile = data.getString("customerMobile").trim();
                        passengerAddress = data.getString("customerAddress");
                        cityName = data.getString("cityName");
                        cityCode = data.getInt("cityCode");
                        String carType = data.getString("CarType");
                        String plak = data.getString("plak");
                        carMobile = data.getString("carMobile").startsWith("0") ? data.getString("carMobile").substring(1) : data.getString("carMobile");
                        String deriverName = data.getString("driverName");
                        String deriverFamily = data.getString("driverFamily");
                        driverMobile = data.getString("driverMobile").startsWith("0") ? data.getString("driverMobile").substring(1) : data.getString("driverMobile");
                        String typeService = data.getString("typeService");
                        if (!data.isNull("lat"))
                            lat = data.getDouble("lat");
                        if (!data.isNull("long"))
                            lng = data.getDouble("long");
                        lastPositionTime = data.getString("lastPositionTime");
                        lastPositionDate = data.getString("lastPositionDate");
                        int Finished = data.getInt("Finished");
                        String statusColor = data.getString("statusColor");
                        String statusText = data.getString("statusDes");
                        int TrafficPlan = data.getInt("TrafficPlan");
                        String customerFixedDes = data.getString("customerFixedDes");
                        String serviceComment = data.getString("serviceComment");
                        voipId = data.getString("VoipId");
                        destinationStation = data.getString("destinationStation");
                        destination = data.getString("destinationAddress");
                        int stationRegisterUser = data.getInt("stationRegisterUser");
                        int destStationRegisterUser = data.getInt("destStationRegisterUser");
                        String timeToCome = data.getString("PUGTime");
                        String dateToCome = data.getString("PUGDate");

                        if (status == 0) { // waiting
                            disableControllerButtonWaitingState();
                        }

                        if (status == 6 && taxiCode.equals("null")) { // cancel before driver
                            disableControllerButtonCancelState(true);
                        }

                        if (status == 6 && !taxiCode.equals("null")) { // cancel after driver
                            disableControllerButtonCancelState(false);
                        }

                        if (Finished == 1) { // finished
                            disableControllerButtonFinishedState();
                        }

                        if (txtCustomerName == null) return;

                        txtUserCodeOrigin.setText(StringHelper.toPersianDigits(stationRegisterUser + ""));
                        txtUserCodeDestination.setText(StringHelper.toPersianDigits(destStationRegisterUser + ""));
                        txtCustomerName.setText(StringHelper.toPersianDigits(passengerName));
                        txtDate.setText(StringHelper.toPersianDigits(callDate));
                        txtTime.setText(StringHelper.toPersianDigits(callTime));
                        txtTripType.setText(StringHelper.toPersianDigits(typeService));
                        txtCity.setText(cityName);
                        txtStationCode.setText(StringHelper.toPersianDigits(stationCode + ""));
                        txtCustomerAddress.setText(StringHelper.toPersianDigits(passengerAddress));
                        txtCustomerTell.setText(StringHelper.toPersianDigits(passengerPhone));
                        txtCustomerMobile.setText(StringHelper.toPersianDigits(customerMobile));
                        txtServiceComment.setText(serviceComment.equals("null") ? " " : StringHelper.toPersianDigits(serviceComment));
                        txtTrafficPlan.setText(TrafficPlan == 0 ? "نیست" : "هست");
                        txtMaxPercent.setText(maxDiscount.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(maxDiscount)));
                        txtPercent.setText(discountAmount.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(discountAmount)));
                        txtSendDate.setText(sendDate.equals("null") ? " " : StringHelper.toPersianDigits(sendDate));
                        txtSendTime.setText(sendTime.equals("null") ? " " : StringHelper.toPersianDigits(sendTime));
                        txtDriverCode.setText(taxiCode.equals("null") ? " " : StringHelper.toPersianDigits(taxiCode));
                        txtDriverName.setText(deriverName.equals("null") ? " " : StringHelper.toPersianDigits(deriverName + " " + deriverFamily));
                        txtDriverMob.setText(carMobile.equals("null") ? " " : StringHelper.toPersianDigits(carMobile));
                        txtCarType.setText(carType.equals("null") ? " " : carType);
                        txtPrice.setText(price.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(price)));
                        txtEndTime.setText(finishTime.equals("null") ? " " : StringHelper.toPersianDigits(finishTime));
                        txtPlaque.setText(plak.equals("null") ? " " : StringHelper.toPersianDigits(plak));
                        txtServiceFixedComment.setText(customerFixedDes.equals("null") ? " " : StringHelper.toPersianDigits(customerFixedDes));
                        txtDestAddress.setText(StringHelper.toPersianDigits(destination));

                        txtDestStation.setText(StringHelper.toPersianDigits(destinationStation));
                        if (!dateToCome.isEmpty()) {
                            String date = DateHelper.strPersianTree(DateHelper.parseDate(dateToCome));
                            txtTimeToCome.setText(StringHelper.toPersianDigits(date) + " ساعت " + StringHelper.toPersianDigits(timeToCome.substring(0, 5)));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable bg_blue_border_edge = AppCompatResources.getDrawable(context, R.drawable.bg_blue_border_edge);
                            llHeaderStatus.setBackground(bg_blue_border_edge);
                            DrawableCompat.setTint(bg_blue_border_edge, Color.parseColor(statusColor));
                        } else {
                            llHeaderStatus.setBackgroundColor(Color.parseColor(statusColor));
                        }

                        txtStatus.setText(statusText);

                        if (vfTripDetails != null)
                            vfTripDetails.setDisplayedChild(1);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .firstButton("باشه", null)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.handler.post(() -> {
                        if (vfTripDetails != null) {
                            vfTripDetails.setDisplayedChild(2);
                        }
                    });
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfTripDetails != null) {
                    vfTripDetails.setDisplayedChild(2);
                }
            });
        }

    };

    private void disableControllerButtonWaitingState() {
        if (btnDriverLocation == null) return;
        btnDriverLocation.setEnabled(false);
        btnReFollow.setEnabled(false);
        btnComplaintRegistration.setEnabled(false);
        btnLost.setEnabled(false);
        btnDriverLock.setEnabled(false);
    }

    private void disableControllerButtonCancelState(boolean isBefore) {
        if (btnDriverLocation == null) return;
        btnDriverLocation.setEnabled(false);
        btnReFollow.setEnabled(false);
        btnCancelTrip.setEnabled(false);
        if (isBefore) {
            btnComplaintRegistration.setEnabled(false);
            btnDriverLock.setEnabled(false);
            btnLost.setEnabled(false);
        }
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
    }

    private void disableControllerButtonFinishedState() {
        if (btnCancelTrip == null) return;
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
        btnReFollow.setEnabled(false);
    }

    private void cancelService() {

        String driverMessage = "اپراتور گرامی، این تماس از سمت راننده میباشد و امکان لغو سرویس میسر نیست.\n" +
                "اگر راننده خود را به عنوان مسافر معرفی کرده و درخواست لغو سفرش را دارد، با همین موضوع ثبت خطا کنید.";

        if (MyApplication.prefManager.getLastCallerId().trim().equals(driverMobile.trim()) || MyApplication.prefManager.getLastCallerId().trim().equals(carMobile.trim())) {
            new GeneralDialog()
                    .title("هشدار")
                    .message(driverMessage)
                    .cancelable(false)
                    .firstButton("باشه", null)
                    .show();
        } else {
            LoadingDialog.makeCancelableLoader();
            RequestHelper.builder(EndPoints.CANCEL)
                    .addParam("serviceId", serviceId)
                    .addParam("scope", "passenger")
                    .listener(onCancelService)
                    .post();
        }

    }

    RequestHelper.Callback onCancelService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
//              MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
                            new GeneralDialog()
                                    .title("تایید شد")
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
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void trackingAgain() {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.AGAIN_TRACKING)
                .addParam("serviceId", serviceId)
                .listener(inTrackingAgain)
                .post();
    }

    RequestHelper.Callback inTrackingAgain = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("تایید شد")
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
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
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

    private void archiveAddress() {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.DELETE_ADDRESS)
                .addParam("phoneNumber", passengerPhone)
                .addParam("adrs", passengerAddress)
                .addParam("mobile", customerMobile)
                .listener(onArchiveAddress)
                .put();
    }

    RequestHelper.Callback onArchiveAddress = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    JSONObject dataObj = object.getJSONObject("data");
                    boolean status = dataObj.getBoolean("status");

                    if (status) {
                        new GeneralDialog()
                                .title("تایید شد")
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

    private void resendCancelService() {
        try {
            LoadingDialog.makeCancelableLoader();
            RequestHelper.builder(EndPoints.CANCEL)
                    .addParam("serviceId", serviceId)
                    .addParam("scope", "driver")
                    .listener(onResendCancelService)
                    .post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onResendCancelService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {

                            insertService(new JSONObject(serviceDetails));// register service again...

                        } else {
                            //TODO  what to do? show error dialog?
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDialog.dismissCancelableDialog();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            //TODO  what to do? show error dialog?
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };

    private void insertService(JSONObject objServiceDetails) {
        try {
            RequestHelper.builder(EndPoints.INSERT_TRIP_SENDING_QUEUE)
                    .addParam("phoneNumber", objServiceDetails.getString("customerTel").trim())
                    .addParam("mobile", objServiceDetails.getString("customerMobile").trim())
                    .addParam("callerName", objServiceDetails.getString("customerName"))
                    .addParam("fixedComment", objServiceDetails.getString("customerFixedDes"))
                    .addParam("address", objServiceDetails.getString("customerAddress"))
                    .addParam("stationCode", 0)
                    .addParam("destinationStation", 0)
                    .addParam("destination", objServiceDetails.getString("destinationAddress"))
                    .addParam("cityCode", objServiceDetails.getInt("cityCode"))
                    .addParam("typeService", objServiceDetails.getInt("ServiceTypeId"))
                    .addParam("description", objServiceDetails.getString("serviceComment"))
                    .addParam("TrafficPlan", objServiceDetails.getInt("TrafficPlan"))
                    .addParam("voipId", objServiceDetails.getString("VoipId"))
                    .addParam("classType", objServiceDetails.getInt("classType"))
                    .addParam("defaultClass", 0)
                    .addParam("count", 1)
                    .addParam("queue", objServiceDetails.getString("queue"))
                    .addParam("senderClient", 0)
                    .listener(insertService)
                    .post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback insertService = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    LoadingDialog.dismissCancelableDialog();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        new GeneralDialog()
                                .title("ثبت شد")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .secondButton("بستن", null)
                                .show();
                    }
                } catch (JSONException e) {
                    //TODO  what to do? show error dialog?
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "TripRegisterActivity class, insertService onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                LoadingDialog.dismissCancelableDialog();
                //TODO  what to do? show error dialog?
            });
        }

        @Override
        public void onReloadPress(boolean v) {
            super.onReloadPress(v);
        }
    };

    private void makeDisposal() {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.MAKE_DISPOSAL)
                .addParam("tripId", serviceId)
                .listener(makeDisposalCallBack)
                .put();
    }

    RequestHelper.Callback makeDisposalCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//            {"success":true,"message":"","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("تایید شد")
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
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }
                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}