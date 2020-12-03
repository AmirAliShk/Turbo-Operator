package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ComplaintRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.DriverLockDialog;
import ir.taxi1880.operatormanagement.dialog.ErrorRegistrationDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.dialog.LostDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class TripDetailsFragment extends Fragment {
  Unbinder unbinder;
  String serviceId;
  String passengerPhone;
  String customerMobile;
  String passengerName;
  String passengerAddress;
  String taxiCode;
  String description;
  String voipId;
  double lat = 0;
  double lng = 0;
  String lastPositionTime = "";
  String lastPositionDate = "";
  String carMobile;
  String driverMobile;

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.txtStatus)
  TextView txtStatus;

  @BindView(R.id.llHeaderStatus)
  LinearLayout llHeaderStatus;

  @BindView(R.id.txtCustomerName)
  TextView txtCustomerName;

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
    new ErrorRegistrationDialog()
            .show(serviceId, passengerPhone, passengerAddress, passengerName, voipId);
  }

  @OnClick(R.id.btnComplaintRegistration)
  void onComplaint() {
    new ComplaintRegistrationDialog().show(serviceId, voipId);
  }

  @OnClick(R.id.btnLost)
  void onLost() {
    new LostDialog().show(serviceId, passengerName, passengerPhone, taxiCode);
  }

  @OnClick(R.id.btnDriverLock)
  void onLock() {
    new DriverLockDialog().show(taxiCode);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
    TypefaceUtil.overrideFonts(txtTitle);

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
          Log.i("TAG", "onResponse: " + args[0].toString());
          JSONObject tripObject = new JSONObject(args[0].toString());
          Boolean success = tripObject.getBoolean("success");
          String message = tripObject.getString("message");
          JSONObject data = tripObject.getJSONObject("data");

          if (success) {
            serviceId = data.getString("serviceId");
            int status = data.getInt("Status");
            String callDate = data.getString("callDate");
            String callTime = data.getString("callTime");
            String sendDate = data.getString("SendDate");
            String sendTime = data.getString("SendTime");
            String stationCode = data.getString("stationCode");
            String price = data.getString("Price");
            String finishdate = data.getString("Finishdate");
            String finishTime = data.getString("FinishTime");
            taxiCode = data.getString("taxicode");
            String driverId = data.getString("driverId");
            int userId = data.getInt("UserId");
            String perDiscount = data.getString("PerDiscount");
            String discountAmount = data.getString("discountAmount");
            String rewardCode = data.getString("RewardCode");
            String maxDiscount = data.getString("MaxDiscount");
            passengerName = data.getString("customerName");
            passengerPhone = data.getString("customerTel");
            customerMobile = data.getString("customerMobile").trim();
            passengerAddress = data.getString("customerAddress");
            String cityName = data.getString("cityName");
            String carType = data.getString("CarType");
            String plak = data.getString("plak");
            carMobile = data.getString("carMobile").startsWith("0")?data.getString("carMobile").substring(1):data.getString("carMobile");
            String deriverName = data.getString("driverName");
            String deriverFamily = data.getString("driverFamily");
            driverMobile = data.getString("driverMobile").startsWith("0")?data.getString("driverMobile").substring(1):data.getString("driverMobile");
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

            if (status == 0) { // waiting
              disableControllerButton0();
            }

            if (status == 6) { // cancel
              disableControllerButton6();
            }

            if (Finished == 1) { // finished
              disableControllerButton();
            }

            if (txtCustomerName == null) return;

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

            llHeaderStatus.setBackgroundColor(Color.parseColor(statusColor));

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

  private void disableControllerButton0() {
    if (btnDriverLocation == null) return;
    btnDriverLocation.setEnabled(false);
    btnDriverLocation.setBackgroundResource(R.drawable.bg_btn_disable);
    btnReFollow.setEnabled(false);
    btnReFollow.setBackgroundResource(R.drawable.bg_btn_disable);
    btnComplaintRegistration.setEnabled(false);
    btnComplaintRegistration.setBackgroundResource(R.drawable.bg_btn_disable);
    btnLost.setEnabled(false);
    btnLost.setBackgroundResource(R.drawable.bg_btn_disable);
    btnDriverLock.setEnabled(false);
    btnDriverLock.setBackgroundResource(R.drawable.bg_btn_disable);
  }

  private void disableControllerButton6() {
    if (btnDriverLocation == null) return;
    btnDriverLocation.setEnabled(false);
    btnDriverLocation.setBackgroundResource(R.drawable.bg_btn_disable);
    btnReFollow.setEnabled(false);
    btnReFollow.setBackgroundResource(R.drawable.bg_btn_disable);
    btnCancelTrip.setEnabled(false);
    btnCancelTrip.setBackgroundResource(R.drawable.bg_btn_disable);
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
  }

  private void disableControllerButton() {
    if (btnCancelTrip == null) return;
//    MyApplication.prefManager.setLastCallerId("");// set empty, because I don't want save this permanently .
    btnReFollow.setEnabled(false);
    btnReFollow.setBackgroundResource(R.drawable.bg_btn_disable);
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
    }else {
      LoadingDialog.makeCancelableLoader();
      RequestHelper.builder(EndPoints.CANCEL_SERVICE)
              .addParam("serviceId", serviceId)
              .addParam("userId", MyApplication.prefManager.getUserCode())
              .listener(onCancelService)
              .post();
    }

  }

  RequestHelper.Callback onCancelService = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());
//            {"success":true,"message":"","data":{"status":true}}
            JSONObject object = new JSONObject(args[0].toString());
            boolean success = object.getBoolean("success");
            String message = object.getString("message");
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

            LoadingDialog.dismissCancelableDialog();
          } catch (Exception e) {
            e.printStackTrace();
          }
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
            .addParam("userId", MyApplication.prefManager.getUserCode())
            .listener(inTrackingAgain)
            .post();
  }

  RequestHelper.Callback inTrackingAgain = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());
//            {"success":true,"message":"","data":{"status":true}}
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
            }

            LoadingDialog.dismissCancelableDialog();
          } catch (Exception e) {
            e.printStackTrace();
          }
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