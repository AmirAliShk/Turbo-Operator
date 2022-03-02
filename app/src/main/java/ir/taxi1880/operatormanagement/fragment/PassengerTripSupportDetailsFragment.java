package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentPassengerTripSupportDetailsBinding;
import ir.taxi1880.operatormanagement.dialog.CallDialog;
import ir.taxi1880.operatormanagement.dialog.ComplaintRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.DriverLockDialog;
import ir.taxi1880.operatormanagement.dialog.ErrorAddressDialog;
import ir.taxi1880.operatormanagement.dialog.ErrorRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.LostDialog;
import ir.taxi1880.operatormanagement.dialog.RewardTripDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PassengerTripSupportDetailsFragment extends Fragment {
    public static final String TAG = PassengerTripSupportDetailsFragment.class.getSimpleName();
    FragmentPassengerTripSupportDetailsBinding binding;
    int serviceId;
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
    String passengerId;
    String originId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPassengerTripSupportDetailsBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot(), MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(binding.txtTitle);
        TypefaceUtil.overrideFonts(binding.txtNull);

        Bundle bundle = getArguments();
        if (bundle != null) {
            serviceId = Integer.parseInt(bundle.getString("id"));
        }

        tripDetails();

        binding.rlEndCall.setOnClickListener(view -> {
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
        });

        binding.btnDriverLock.setOnClickListener(view -> new DriverLockDialog().show(taxiCode));

        binding.btnLost.setOnClickListener(view -> new LostDialog().show(serviceId + "", passengerName, passengerPhone, taxiCode, false));

        binding.btnComplaintRegistration.setOnClickListener(view -> new ComplaintRegistrationDialog().show(serviceId + "", voipId));

        binding.btnErrorRegistration.setOnClickListener(view -> {
//        String cityName= new DataBase(MyApplication.context).getCityName2(cityCode);
            new ErrorRegistrationDialog().show(serviceId + "", passengerPhone, customerMobile, passengerAddress, passengerName, voipId, cityCode, stationCode, userId, callTime, callDate, price, destinationStation, destination);
        });

        binding.btnReFollow.setOnClickListener(view -> new GeneralDialog()
                .title("پیگیری مجدد")
                .message("آیا از پیگیری مجدد این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::trackingAgain)
                .secondButton("خیر", null)
                .show());

        binding.btnDriverLocation.setOnClickListener(view -> {
            Bundle bundle1 = new Bundle();
            bundle1.putDouble("lat", lat);
            bundle1.putDouble("lng", lng);
            bundle1.putString("time", lastPositionTime);
            bundle1.putString("date", lastPositionDate);
            bundle1.putString("taxiCode", taxiCode);
            FragmentHelper.toFragment(MyApplication.currentActivity, new DriverLocationFragment()).setArguments(bundle1).add();
        });

        binding.btnEditAddress.setOnClickListener(view -> new ErrorAddressDialog().show(passengerAddress, serviceId + "", address -> binding.txtCustomerAddress.setText(address)));

        binding.btnRewardTip.setOnClickListener(view -> {
            String liveNumber = MyApplication.prefManager.getLastCallerId();
            if (liveNumber.equals(passengerPhone) || liveNumber.equals(customerMobile)) {
                new RewardTripDialog().show(serviceId, reward -> {
                    int rewardInt = Integer.parseInt(reward);
                    int priceInt = Integer.parseInt(price);
                    int total = priceInt + rewardInt;
                    price = total + "";
                    binding.txtPrice.setText(price.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(price)));
                });
            } else {
                MyApplication.Toast("سرویس به این مشتری تعلق ندارد", 2);
            }
        });

        binding.btnArchiveAddress.setOnClickListener(view -> new GeneralDialog()
                .title("بایگانی آدرس")
                .message("آیا اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::archiveAddress)
                .secondButton("خیر", null)
                .show());

        binding.btnCancelTrip.setOnClickListener(view -> new GeneralDialog()
                .title("لغو سفر")
                .message("آیا از کنسل کردن این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::cancelService)
                .secondButton("خیر", null)
                .show());

        binding.btnDisposal.setOnClickListener(view -> new GeneralDialog()
                .title("تبدیل به در اختیار")
                .message("آیا از در اختیار کردن این سفر اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", this::makeDisposal)
                .secondButton("خیر", null)
                .show());

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    private void tripDetails() {
        if (binding.vfTripDetails != null) {
            binding.vfTripDetails.setDisplayedChild(0);
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
                    boolean success = tripObject.getBoolean("success");
                    String message = tripObject.getString("message");
//                    JSONObject data = tripObject.getJSONObject("data");

                    if (success) {
                        JSONObject data = tripObject.getJSONObject("data");
                        serviceDetails = tripObject.getJSONObject("data").toString();
                        serviceId = data.getInt("serviceId");
                        passengerId = data.getString("passengerId");
                        originId = data.getString("originId");
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

                        if (binding.txtCustomerName == null) return;

                        binding.txtUserCodeOrigin.setText(StringHelper.toPersianDigits(stationRegisterUser + ""));
                        binding.txtUserCodeDestination.setText(StringHelper.toPersianDigits(destStationRegisterUser + ""));
                        binding.txtCustomerName.setText(StringHelper.toPersianDigits(passengerName));
                        binding.txtDate.setText(StringHelper.toPersianDigits(callDate));
                        binding.txtTime.setText(StringHelper.toPersianDigits(callTime));
                        binding.txtTripType.setText(StringHelper.toPersianDigits(typeService));
                        binding.txtCity.setText(cityName);
                        binding.txtStationCode.setText(StringHelper.toPersianDigits(stationCode + ""));
                        binding.txtCustomerAddress.setText(StringHelper.toPersianDigits(passengerAddress));
                        binding.txtCustomerTell.setText(StringHelper.toPersianDigits(passengerPhone));
                        binding.txtCustomerMobile.setText(StringHelper.toPersianDigits(customerMobile));
                        binding.txtServiceComment.setText(serviceComment.equals("null") ? " " : StringHelper.toPersianDigits(serviceComment));
                        binding.txtTrafficPlan.setText(TrafficPlan == 0 ? "نیست" : "هست");
                        binding.txtMaxPercent.setText(maxDiscount.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(maxDiscount)));
                        binding.txtPercent.setText(discountAmount.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(discountAmount)));
                        binding.txtSendDate.setText(sendDate.equals("null") ? " " : StringHelper.toPersianDigits(sendDate));
                        binding.txtSendTime.setText(sendTime.equals("null") ? " " : StringHelper.toPersianDigits(sendTime));
                        binding.txtDriverCode.setText(taxiCode.equals("null") ? " " : StringHelper.toPersianDigits(taxiCode));
                        binding.txtDriverName.setText(deriverName.equals("null") ? " " : StringHelper.toPersianDigits(deriverName + " " + deriverFamily));
                        binding.txtDriverMob.setText(carMobile.equals("null") ? " " : StringHelper.toPersianDigits(carMobile));
                        binding.txtCarType.setText(carType.equals("null") ? " " : carType);
                        binding.txtPrice.setText(price.equals("null") ? " " : StringHelper.toPersianDigits(StringHelper.setComma(price)));
                        binding.txtEndTime.setText(finishTime.equals("null") ? " " : StringHelper.toPersianDigits(finishTime));
                        binding.txtPlaque.setText(plak.equals("null") ? " " : StringHelper.toPersianDigits(plak));
                        binding.txtServiceFixedComment.setText(customerFixedDes.equals("null") ? " " : StringHelper.toPersianDigits(customerFixedDes));
                        binding.txtDestAddress.setText(StringHelper.toPersianDigits(destination));

                        binding.txtDestStation.setText(StringHelper.toPersianDigits(destinationStation));
                        if (!dateToCome.isEmpty()) {
                            String date = DateHelper.strPersianTree(DateHelper.parseDate(dateToCome));
                            binding.txtTimeToCome.setText(StringHelper.toPersianDigits(date) + " ساعت " + StringHelper.toPersianDigits(timeToCome.substring(0, 5)));
                        }
                        setBackgroundTitleColor(statusColor);
                        binding.txtStatus.setText(statusText);

                        if (binding.vfTripDetails != null)
                            binding.vfTripDetails.setDisplayedChild(1);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .firstButton("باشه", null)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetTripDetails method");
                    MyApplication.handler.post(() -> {
                        if (binding.vfTripDetails != null) {
                            binding.vfTripDetails.setDisplayedChild(2);
                        }
                    });
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfTripDetails != null) {
                    binding.vfTripDetails.setDisplayedChild(2);
                }
            });
        }
    };

    private void disableControllerButtonWaitingState() {
        if (binding.btnDriverLocation == null) return;
        binding.btnDriverLocation.setEnabled(false);
        binding.btnReFollow.setEnabled(false);
        binding.btnComplaintRegistration.setEnabled(false);
        binding.btnLost.setEnabled(false);
        binding.btnDriverLock.setEnabled(false);
    }

    private void disableControllerButtonCancelState(boolean isBefore) {
        if (binding.btnDriverLocation == null) return;
        binding.btnDriverLocation.setEnabled(false);
        binding.btnReFollow.setEnabled(false);
        binding.btnCancelTrip.setEnabled(false);
        binding.btnRewardTip.setEnabled(false);
        binding.btnDisposal.setEnabled(false);
        if (isBefore) {
            binding.btnComplaintRegistration.setEnabled(false);
            binding.btnDriverLock.setEnabled(false);
            binding.btnLost.setEnabled(false);
        }
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
    }

    private void disableControllerButtonFinishedState() {
        if (binding.btnCancelTrip == null) return;
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
        binding.btnReFollow.setEnabled(false);
        binding.btnDriverLocation.setEnabled(false);
        binding.btnRewardTip.setEnabled(false);
        binding.btnCancelTrip.setEnabled(false);
        binding.btnDisposal.setEnabled(false);
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

    private void setBackgroundTitleColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable bg_blue_border_edge = AppCompatResources.getDrawable(context, R.drawable.bg_blue_border_edge);
            binding.llHeaderStatus.setBackground(bg_blue_border_edge);
            DrawableCompat.setTint(bg_blue_border_edge, Color.parseColor(color));
        } else {
            binding.llHeaderStatus.setBackgroundColor(Color.parseColor(color));
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
//              MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently

                            new GeneralDialog()
                                    .title("تایید شد")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {
                                        binding.txtStatus.setText("کنسل شده توسط " + MyApplication.prefManager.getOperatorName() + " پشتیبانی مسافر");
                                        setBackgroundTitleColor("#d50d0d");
                                    })
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
                    AvaCrashReporter.send(e, TAG + " class, onCancelService method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
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
                    AvaCrashReporter.send(e, TAG + " class, inTrackingAgain method");
                    LoadingDialog.dismissCancelableDialog();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };

    private void archiveAddress() {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.DELETE_ADDRESS)
                .addParam("passengerId", passengerId)
                .addParam("addressId", originId)
                .addParam("type", "origin")
                .listener(onArchiveAddress)
                .delete();
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
                    AvaCrashReporter.send(e, TAG + " class, onArchiveAddress method");
                    LoadingDialog.dismissCancelableDialog();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
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
            AvaCrashReporter.send(e, TAG + " class, resendCancelService method");
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
                    AvaCrashReporter.send(e, TAG + " class, onResendCancelService method");
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
            AvaCrashReporter.send(e, TAG + " class, insertService method");
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
                    AvaCrashReporter.send(e, TAG + " class, insertService onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            //TODO  what to do? show error dialog?
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
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
                    AvaCrashReporter.send(e, TAG + " class, makeDisposalCallBack onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };
}