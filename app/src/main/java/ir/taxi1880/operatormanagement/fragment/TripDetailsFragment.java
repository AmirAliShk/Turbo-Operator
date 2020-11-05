package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
    FragmentHelper.toFragment(MyApplication.currentActivity,new DriverLocationFragment()).replace();
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
    TypefaceUtil.overrideFonts(view);

    return view;
  }

  private void serviceDetails() {
    if (vfTripDetails!=null){
      vfTripDetails.setDisplayedChild(0);
    }

    RequestHelper.builder(EndPoints.SERVICE_DETAIL)
            .addParam("serviceId",1)
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
            Log.i("TripDetailsFragment", "run: "+args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
        if (vfTripDetails!=null){
          vfTripDetails.setDisplayedChild(2);
        }
      });
    }
  };

  private void setLostObject() {

    RequestHelper.builder(EndPoints.INSERT_LOST_OBJECT)
            .addParam("serviceId",1)
            .addParam("address",1)
            .addParam("objectType",1)
            .addParam("description",1)
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
            Log.i("TripDetailsFragment", "run: "+args[0].toString());

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
            .addParam("serviceId",1)
            .addParam("description",1)
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
            Log.i("TripDetailsFragment", "run: "+args[0].toString());

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
            .addParam("serviceId",1)
            .addParam("userId",1)
            .addParam("complaintType",1)
            .addParam("description",1)
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
            Log.i("TripDetailsFragment", "run: "+args[0].toString());

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