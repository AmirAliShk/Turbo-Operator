package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ir.taxi1880.operatormanagement.dialog.LostDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class TripDetailsFragment extends Fragment {
  Unbinder unbinder;
  String serviceId;
  String passengerPhone;
  String passengerName;
  String passengerAddress;
  String carCode;
  String description;
  String voipId;

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

  @BindView(R.id.txtTrafficPlan)
  TextView txtTrafficPlan;

  @BindView(R.id.txtServiceComment)
  TextView txtServiceComment;

  @BindView(R.id.vfTripDetails)
  ViewFlipper vfTripDetails;

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
    //open map page
    FragmentHelper.toFragment(MyApplication.currentActivity, new DriverLocationFragment()).replace();
  }

  @OnClick(R.id.btnReFollow)
  void onReFollow() {
    new GeneralDialog()
            .title("پیگیری مجدد")
            .message("آیا از پیگیری مجدد این سفر اطمینان دارید؟")
            .cancelable(false)
            .firstButton("بله", null)
            .secondButton("خیر", null)
            .show();
  }

  @OnClick(R.id.btnErrorRegistration)
  void onError() {
    new ErrorRegistrationDialog().show(serviceId,passengerPhone,passengerAddress,passengerName,voipId);
  }

  @OnClick(R.id.btnComplaintRegistration)
  void onComplaint() {
    new ComplaintRegistrationDialog().show(serviceId,voipId);
  }

  @OnClick(R.id.btnLost)
  void onLost() {
    new LostDialog().show(serviceId, passengerName, passengerPhone, carCode);
  }

  @OnClick(R.id.btnDriverLock)
  void onLock() {
    new DriverLockDialog().show(serviceId);
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
          Log.i("TAG", "onResponse: "+args[0].toString());
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
            int stationCode = data.getInt("stationCode");
            String price = data.getString("Price");
            String finishdate = data.getString("Finishdate");
            String finishTime = data.getString("FinishTime");
            carCode = data.getString("taxicode");
            String driverId = data.getString("driverId");
            int userId = data.getInt("UserId");
            String perDiscount = data.getString("PerDiscount");
            String rewardCode = data.getString("RewardCode");
            String maxDiscount = data.getString("MaxDiscount");
            passengerName = data.getString("customerName");
            passengerPhone = data.getString("customerTel");
            String customerMobile = data.getString("customerMobile");
            passengerAddress = data.getString("customerAddress");
            String cityName = data.getString("cityName");
            String carType = data.getString("CarType");
            String plak = data.getString("plak");
            String carMobile = data.getString("carMobile");
            String deriverName = data.getString("driverName");
            String deriverFamily = data.getString("driverFamily");
            String driverMobile = data.getString("driverMobile");
            String typeService = data.getString("typeService");
            String lat = data.getString("lat");
            String lon = data.getString("lon");
            String lastPositionTime = data.getString("lastPositionTime");
            String lastPositionDate = data.getString("lastPositionDate");
            int Finished = data.getInt("Finished");
            String statusColor = data.getString("statusColor");
            String statusText = data.getString("statusDes");
            int TrafficPlan = data.getInt("TrafficPlan");
            String customerFixedDes = data.getString("customerFixedDes");
            String serviceComment = data.getString("serviceComment");
            voipId = data.getString("VoipId");

            txtCustomerName.setText(StringHelper.toPersianDigits(passengerName));
            txtDate.setText(StringHelper.toPersianDigits(callDate));
            txtTime.setText(StringHelper.toPersianDigits(callTime));
            txtTripType.setText(StringHelper.toPersianDigits(typeService));
            txtCity.setText(cityName);
            txtCustomerAddress.setText(StringHelper.toPersianDigits(passengerAddress));
            txtCustomerTell.setText(StringHelper.toPersianDigits(passengerPhone));
            txtCustomerMobile.setText(StringHelper.toPersianDigits(customerMobile));
            txtServiceComment.setText(serviceComment.equals("null") ? " " : StringHelper.toPersianDigits(serviceComment));
            txtTrafficPlan.setText(TrafficPlan==0?"نیست":"هست");
            txtMaxPercent.setText(maxDiscount.equals("null") ? " " : StringHelper.toPersianDigits(maxDiscount));
            txtPercent.setText(rewardCode.equals("null") ? " " : StringHelper.toPersianDigits(rewardCode));
            txtSendDate.setText(sendDate.equals("null") ? " " : StringHelper.toPersianDigits(sendDate));
            txtSendTime.setText(sendTime.equals("null") ? " " : StringHelper.toPersianDigits(sendTime));
            txtDriverCode.setText(carCode.equals("null") ? " " : StringHelper.toPersianDigits(carCode));
            txtDriverName.setText(deriverName.equals("null") ? " " : StringHelper.toPersianDigits(deriverName + " " + deriverFamily));
            txtDriverMob.setText(carMobile.equals("null") ? " " : StringHelper.toPersianDigits(carMobile));
            txtCarType.setText(carType.equals("null") ? " " : carType);
            txtPrice.setText(price.equals("null") ? " " : StringHelper.toPersianDigits(price));
            txtEndTime.setText(finishTime.equals("null") ? " " : StringHelper.toPersianDigits(finishTime));
            txtPlaque.setText(plak.equals("null") ? " " : StringHelper.toPersianDigits(plak));

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

  private void cancelService() {

    RequestHelper.builder(EndPoints.CANCEL_SERVICE)
            .addParam("serviceId", serviceId)
            .addParam("userId", MyApplication.prefManager.getUserCode())
            .listener(onCancelService)
            .post();
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
            JSONObject object=new JSONObject(args[0].toString());
            boolean success=object.getBoolean("success");
            String message=object.getString("message");
            JSONObject dataObj = object.getJSONObject("data");
            boolean status=dataObj.getBoolean("status");

            if (status){
              new GeneralDialog()
                      .title("تایید شد")
                      .message("سرویس با موفقیت کنسل شد")
                      .cancelable(false)
                      .firstButton("باشه", () -> MyApplication.currentActivity.onBackPressed())
                      .show();
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
      });
    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}