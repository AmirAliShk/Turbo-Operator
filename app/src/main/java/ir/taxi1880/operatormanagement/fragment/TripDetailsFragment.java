package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
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

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.txtStatus)
  TextView txtStatus;

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
    TypefaceUtil.overrideFonts(view,MyApplication.IraSanSMedume);
    TypefaceUtil.overrideFonts(txtTitle);

    getDetails();

    return view;
  }

  String tripList = "{\"success\":true,\"message\":\"\",\"data\":" +
          "[{\"date\":\"1399/08/18\",\"time\":\"16:00\",\"city\":\"کاشمر\",\"serviceType\":\"سرویس\",\"customerName\":\"فائزه نوری\",\"customerTell\":\"09015693855\",\"customerMob\":\"33710834\",\"address\":\"مشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهدمشهد، فداییان اسلا\",\"carType\":\"تشریفات\",\"driverMobile\":\"09015693855\"" +
          ",\"driverCode\":\"123\",\"driverName\":\"نوری\",\"price\":\"18000\",\"endTime\":\"15:48\",\"plaque\":\"14ث1587\"}]}";

  private void getDetails() {
    try {
      JSONObject tripObject = new JSONObject(tripList);
      Boolean success = tripObject.getBoolean("success");
      String message = tripObject.getString("message");
      JSONArray data = tripObject.getJSONArray("data");
      JSONObject dataObj = data.getJSONObject(0);
      String date=dataObj.getString("date");
      String time=dataObj.getString("time");
      String city=dataObj.getString("city");
      String serviceType=dataObj.getString("serviceType");
      String customerName=dataObj.getString("customerName");
      String customerTell=dataObj.getString("customerTell");
      String customerMob=dataObj.getString("customerMob");
      String address=dataObj.getString("address");
      String carType=dataObj.getString("carType");
      String driverMobile=dataObj.getString("driverMobile");
      String driverCode=dataObj.getString("driverCode");
      String driverName=dataObj.getString("driverName");
      String price=dataObj.getString("price");
      String endTime=dataObj.getString("endTime");
      String plaque=dataObj.getString("plaque");

      txtCarType.setText(carType);
      txtCity.setText(city);
      txtCustomerAddress.setText(StringHelper.toPersianDigits(address));
      txtCustomerMobile.setText(StringHelper.toPersianDigits(customerMob));
      txtCustomerTell.setText(StringHelper.toPersianDigits(customerTell));
      txtCustomerName.setText(StringHelper.toPersianDigits(customerName));
      txtDate.setText(StringHelper.toPersianDigits(date));
      txtDriverCode.setText(StringHelper.toPersianDigits(driverCode));
      txtDriverMob.setText(StringHelper.toPersianDigits(driverMobile));
      txtDriverName.setText(StringHelper.toPersianDigits(driverName));
      txtEndTime.setText(StringHelper.toPersianDigits(endTime));
      txtMaxPercent.setText(StringHelper.toPersianDigits("fvgffd"));
      txtPercent.setText(StringHelper.toPersianDigits("sgfsdfg"));
      txtPlaque.setText(StringHelper.toPersianDigits(plaque));
      txtPrice.setText(StringHelper.toPersianDigits(price));
      txtSendDate.setText(StringHelper.toPersianDigits(date));
      txtSendTime.setText(StringHelper.toPersianDigits(time));
      txtStatus.setText("اعزام نشده");
      txtTime.setText(StringHelper.toPersianDigits(time));
      txtTripType.setText(StringHelper.toPersianDigits(serviceType));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void tripDetails() {
    if (vfTripDetails != null) {
      vfTripDetails.setDisplayedChild(0);
    }

    RequestHelper.builder(EndPoints.SERVICE_DETAIL)
            .addParam("serviceId", 1)
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