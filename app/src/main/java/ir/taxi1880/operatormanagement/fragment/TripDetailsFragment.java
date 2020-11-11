package ir.taxi1880.operatormanagement.fragment;

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

  @BindView(R.id.vfTripDetails)
  ViewFlipper vfTripDetails;

  @OnClick(R.id.btnCancelTrip)
  void onCancel() {
    new GeneralDialog()
            .title("لغو سفر")
            .message("آیا از کنسل کردن این سفر اطمینان دارید؟")
            .cancelable(false)
            .firstButton("بله", null)
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
    new ErrorRegistrationDialog().show();
  }

  @OnClick(R.id.btnComplaintRegistration)
  void onComplaint() {
    new ComplaintRegistrationDialog().show();
  }

  @OnClick(R.id.btnLost)
  void onLost() {
    new LostDialog().show();
  }

  @OnClick(R.id.btnDriverLock)
  void onLock() {
    new DriverLockDialog().show();
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
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject tripObject = new JSONObject(args[0].toString());
            Boolean success = tripObject.getBoolean("success");
            String message = tripObject.getString("message");
            JSONObject data = tripObject.getJSONObject("data");
            String serviceId = data.getString("serviceId");
            int status = data.getInt("Status");
            String callDate = data.getString("callDate");
            String callTime = data.getString("callTime");
            String sendDate = data.getString("SendDate");
            String sendTime = data.getString("SendTime");
            int stationCode = data.getInt("stationCode");
            String price = data.getString("Price");
            String finishdate = data.getString("Finishdate");
            String finishTime = data.getString("FinishTime");
            String taxicode = data.getString("taxicode");
            String driverId = data.getString("driverId");
            int userId = data.getInt("UserId");
            String perDiscount = data.getString("PerDiscount");
            String rewardCode = data.getString("RewardCode");
            String maxDiscount = data.getString("MaxDiscount");
            String customerName = data.getString("customerName");
            String customerTel = data.getString("customerTel");
            String customerMobile = data.getString("customerMobile");
            String customerAddress = data.getString("customerAddress");
            String cityName = data.getString("cityName");
            String carType = data.getString("CarType");
            String plak = data.getString("plak");
            String carMobile = data.getString("carMobile");
            String deriverName = data.getString("deriverName");
            String deriverFamily = data.getString("deriverFamily");
            String driverMobile = data.getString("driverMobile");
            String typeService = data.getString("typeService");
            String lat = data.getString("lat");
            String lon = data.getString("lon");
            String lastPositionTime = data.getString("lastPositionTime");
            String lastPositionDate = data.getString("lastPositionDate");

            txtCustomerName.setText(StringHelper.toPersianDigits(customerName));
            txtDate.setText(StringHelper.toPersianDigits(callDate));
            txtTime.setText(StringHelper.toPersianDigits(callTime));
            txtTripType.setText(StringHelper.toPersianDigits(typeService));
            txtCity.setText(cityName);
            txtCustomerAddress.setText(StringHelper.toPersianDigits(customerAddress));
            txtCustomerTell.setText(StringHelper.toPersianDigits(customerTel));
            txtCustomerMobile.setText(StringHelper.toPersianDigits(customerMobile));
            txtMaxPercent.setText(StringHelper.toPersianDigits(maxDiscount));
            txtPercent.setText(StringHelper.toPersianDigits(rewardCode));
            txtSendDate.setText(StringHelper.toPersianDigits(sendDate));
            txtSendTime.setText(StringHelper.toPersianDigits(sendTime));
            txtDriverCode.setText(StringHelper.toPersianDigits(taxicode));
            txtDriverName.setText(StringHelper.toPersianDigits(deriverName + " " + deriverFamily));
            txtDriverMob.setText(StringHelper.toPersianDigits(driverMobile));
            txtCarType.setText(carType);
            txtPrice.setText(StringHelper.toPersianDigits(price));
            txtEndTime.setText(StringHelper.toPersianDigits(finishTime));
            txtPlaque.setText(StringHelper.toPersianDigits(plak));

            int headerColor = R.drawable.header_blue;
            String statusTitle = "";

            if (finishdate != null && finishTime != null) {
              headerColor = R.drawable.header_green;
              statusTitle = "اتمام یافته";
            }

            switch (status) {
              case 0:
                headerColor = R.drawable.header_blue;
                statusTitle = "درحال انتظار";
                break;

              case 6:
                headerColor = R.drawable.header_red;
                statusTitle = "کنسل شده";
                break;

              case 1:
                headerColor = R.drawable.header_yellow;
                statusTitle = "اعزام شده";
                break;
            }
            llHeaderStatus.setBackgroundResource(headerColor);

            txtStatus.setText(statusTitle);

            if (vfTripDetails != null)
              vfTripDetails.setDisplayedChild(1);

          } catch (JSONException e) {
            e.printStackTrace();
          }
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

  private void lockTaxi() {
    if (vfTripDetails != null) {
      vfTripDetails.setDisplayedChild(0);
    }

    RequestHelper.builder(EndPoints.SERVICE_DETAIL)
            .addParam("serviceId", 1)
            .addParam("userId", 1)
            .addParam("todate", 1)
            .addParam("totime", 1)
            .addParam("reasonId", 1)
            .listener(onGetTripDetails)
            .post();
  }

  RequestHelper.Callback onLockTaxi = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
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
    if (vfTripDetails != null) {
      vfTripDetails.setDisplayedChild(0);
    }

    RequestHelper.builder(EndPoints.SERVICE_DETAIL)
            .addParam("serviceId", 1)
            .addParam("userId", 1)
            .listener(inCancelService)
            .post();
  }

  RequestHelper.Callback inCancelService = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
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

  private void setLostObject() {

    RequestHelper.builder(EndPoints.INSERT_LOST_OBJECT)
            .addParam("serviceId", 1)
            .addParam("address", 1)
            .addParam("objectType", 1)
            .addParam("description", 1)
            .listener(onSetLostObject)
            .post();
  }

  RequestHelper.Callback onSetLostObject = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());

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

  private void setMistake() {

    RequestHelper.builder(EndPoints.INSERT_MISTAKE)
            .addParam("serviceId", 1)
            .addParam("userId", 1)
            .addParam("tel", 1)
            .addParam("adrs", 1)
            .addParam("customerName", 1)
            .addParam("voipId", 1)
            .addParam("description", 1)
            .listener(onSetMistake)
            .post();
  }

  RequestHelper.Callback onSetMistake = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());

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

  private void setComplaint() {

    RequestHelper.builder(EndPoints.INSERT_COMPLAINT)
            .addParam("serviceId", 1)
            .addParam("userId", 1)
            .addParam("taxiCode", 1)
            .addParam("taxiPlak", 1)
            .addParam("complaintType", 1)
            .addParam("driverName", 1)
            .addParam("driverMobile", 1)
            .addParam("description", 1)
            .addParam("customerId", 1)
            .addParam("customerName", 1)
            .addParam("customerTel", 1)
            .addParam("carType", 1)
            .addParam("adrs", 1)
            .addParam("destination", 1)
            .addParam("price", 1)
            .addParam("cityCode", 1)
            .addParam("voipId", 1)
            .addParam("voipIdService", 1)
            .addParam("customerMobile", 1)
            .addParam("insertUser", 1)
            .addParam("khodroCode", 1)
            .addParam("smartCode", 1)
            .listener(onSetComplaint)
            .post();
  }

  RequestHelper.Callback onSetComplaint = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("TripDetailsFragment", "run: " + args[0].toString());

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