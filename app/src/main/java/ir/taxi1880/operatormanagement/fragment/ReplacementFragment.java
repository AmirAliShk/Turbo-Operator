package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.OperatorDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReplacementFragment extends Fragment {
  public static final String TAG = ReplacementFragment.class.getSimpleName();
  Unbinder unbinder;
  String[] shift = {"صبح", "عصر", "شب", "استراحت"};
  GeneralDialog generalDialog = new GeneralDialog();
  int shiftId;
  String shiftDate;
  String shiftName;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.llLoader)
  LinearLayout llLoader;

  @BindView(R.id.llDate)
  LinearLayout llDate;

  @BindView(R.id.llShift)
  LinearLayout llShift;

  @BindView(R.id.edtDate)
  TextView edtDate;

  @BindView(R.id.spinnerShift)
  TextView spinnerShift;

  @BindView(R.id.edtOperator)
  TextView edtOperator;

  int opId = 0;

  @OnClick(R.id.llOperator)
  void onOperator() {

    switch (shiftName) {
      case "صبح":
        shiftId = 1;
        break;
      case "عصر":
        shiftId = 2;
        break;
      case "شب":
        shiftId = 3;
        break;
      case "استراحت":
        shiftId = 4;
        break;
      default:
        shiftId = 0;
        break;
    }
    getOnlineOperator();
  }

  @BindView(R.id.btnSubmit)
  Button btnSubmit;

  @BindView(R.id.vfOperator)
  ViewFlipper vfOperator;

  @OnClick(R.id.btnSubmit)
  void onSubmit() {
    if (edtOperator.getText().equals("")) {
      MyApplication.Toast("اپراتور مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT);
      return;
    }

    new GeneralDialog()
            .title("ثبت درخواست")
            .message(" شما میخواهید خانم " + edtOperator.getText() + " در تاریخ " + shiftDate + " در شیفت " + shiftName + " به جای شما حضور یابد.")
            .firstButton("بله", () -> {
              shiftReplacementRequest();
              edtOperator.setText("");
            })
            .secondButton("خیر", null)
            .show();
  }

  @SuppressLint("Clickab leViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_replacement, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    Bundle bundle = getArguments();
    if (bundle != null) {
      shiftDate = bundle.getString("shiftDate");
      shiftName = bundle.getString("shiftName");
    }

//    if (bundle.getString("shiftName").equals("استراحت")) {
//        spinnerShift.setText();
//        edtDate.setText();
//        llDate.setVisibility(View.GONE);
//        llShift.setVisibility(View.GONE);
//      } else {
//        spinnerShift.setText(bundle.getString("shiftName"));
//        edtDate.setText(bundle.getString("shiftDate"));
//    }

    return view;
  }

  private void shiftReplacementRequest() {
    llLoader.setVisibility(View.VISIBLE);
    RequestHelper.builder(EndPoints.SHIFT_REPLACEMENT_REQUEST)
            .addParam("intendedOperatorId", opId)
            .addParam("shift", shiftId)
            .addParam("date", shiftDate)
            .listener(onShiftReplacementRequest)
            .post();

  }

  RequestHelper.Callback onShiftReplacementRequest = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          JSONObject object = new JSONObject(args[0].toString());
          int status = object.getInt("status");
          String msgStatus = object.getString("messageStatus");
          if (status == 1) {
            generalDialog.thirdButton("باشه", () -> generalDialog.dismiss());
            generalDialog.message(msgStatus);
            generalDialog.title("تایید");
            generalDialog.show();
//                        MyApplication.Toast(msgStatus, Toast.LENGTH_SHORT);
          } else {
            generalDialog.thirdButton("باشه", () -> generalDialog.dismiss());
            generalDialog.message(msgStatus);
            generalDialog.title("هشدار");
            generalDialog.show();
          }
//                    else {
//                        new ErrorDialog()
//                                .titleText("خطایی رخ داده")
//                                .messageText("پردازش داده های ورودی با مشکل مواجه گردید")
//                                .tryAgainRunnable("تلاش مجدد", () -> shiftReplacementRequest(1, 1, " ", " "))
//                                .closeRunnable("بستن", () -> MyApplication.currentActivity.finish())
//                                .show();
//                    }
        } catch (Exception e) {
          e.printStackTrace();
          AvaCrashReporter.send(e,"ReplacementFragment class, onShiftReplacementRequest onResponse method");
        }

        if (llLoader != null)
          llLoader.setVisibility(View.GONE);
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) { }
  };

  private void getOnlineOperator() {
    // TODO test it ...
    if (vfOperator != null)
      vfOperator.setDisplayedChild(1);
    RequestHelper.builder(EndPoints.GET_SHIFT_OPERATOR)
            .addPath(shiftDate)
            .addPath(shiftId+"")
            .listener(onGetOnlineOperator)
            .get();

  }

  RequestHelper.Callback onGetOnlineOperator = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          JSONArray operatorArr = new JSONArray(args[0].toString());
          MyApplication.prefManager.setOperatorList(operatorArr.toString());

        } catch (Exception e) {
          e.printStackTrace();
          AvaCrashReporter.send(e,"ReplacementFragment class, onGetOnlineOperator onResponse method");
        }
        if (vfOperator != null)
          vfOperator.setDisplayedChild(0);
        new OperatorDialog().show((op) -> {
          if (edtOperator != null)
            edtOperator.setText(op.getOperatorName());
          opId = op.getOperatorId();
        });
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) { }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
