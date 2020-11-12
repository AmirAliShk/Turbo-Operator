package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

  public static final String TAG = AccountFragment.class.getSimpleName();
  Unbinder unbinder;
  String a;
  private int keyDel;

  @OnClick(R.id.rlActionBar)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.txtOperatorName)
  TextView txtOperatorName;

  @BindView(R.id.txtOperatorCharge)
  TextView txtOperatorCharge;

  @BindView(R.id.edtCardNumber)
  EditText edtCardNumber;

  @BindView(R.id.edtIben)
  EditText edtIben;

  @BindView(R.id.edtAccountNum)
  EditText edtAccountNum;

  @OnClick(R.id.btnCheckOut)
  void OnCheckOut() {
    new GeneralDialog()
            .title("هشدار")
            .message("آیا از درخواست تسویه حساب خود اطمینان دارید؟")
            .firstButton("بله مطمئنم", () -> payment(MyApplication.prefManager.getUserCode()))
            .secondButton("پشیمون شدم", null)
            .show();
  }

  @OnClick(R.id.btnUpdate)
  void OnUpdae() {
    String cardNumber = edtCardNumber.getText().toString().replaceAll(" ", "");
    new GeneralDialog()
            .title("به روز رسانی")
            .message("اطلاعات شما به روز شود؟")
            .firstButton("بله", () ->
                    updateProfile(MyApplication.prefManager.getUserCode(), edtAccountNum.getText().toString(), cardNumber, edtIben.getText().toString()))
            .secondButton("خیر", null)
            .show();
  }

  @BindView(R.id.btnCheckOut)
  Button btnCheckOut;

  @BindView(R.id.vfBalance)
  ViewFlipper vfBalance;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_account, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    getBalance(MyApplication.prefManager.getUserCode());

    txtOperatorName.setText(MyApplication.prefManager.getOperatorName());
    edtAccountNum.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getAccountNumber()));
    edtCardNumber.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getCardNumber()));
    edtIben.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getSheba()));

    StringHelper.setCharAfterOnTime(edtCardNumber, " - ", 4);
    StringHelper.setCharAfterOnTime(edtAccountNum, "", 0);
    StringHelper.setCharAfterOnTime(edtIben, "", 0);

    return view;
  }

  private void getBalance(int userId) {
    if (vfBalance != null)
      vfBalance.setDisplayedChild(0);

    RequestHelper.builder(EndPoints.BALANCE)
            .addPath(userId + "")
            .listener(getBalance)
            .get();

  }

  RequestHelper.Callback getBalance = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "run: " + args[0].toString());

            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");
            String accountBalance = dataObj.getString("accountBalance");
            String balance = StringHelper.setComma(accountBalance);
            if (txtOperatorCharge != null)
              txtOperatorCharge.setText(StringHelper.toPersianDigits(balance + " تومان "));
            if (vfBalance != null)
              vfBalance.setDisplayedChild(1);
          } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "AccountFragment class, getBalance onResponse method");
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
//      vfBalance.setDisplayedChild(1);
    }
  };

  private void updateProfile(int userId, String accountNumber, String cardNumber, String sheba) {

    RequestHelper.builder(EndPoints.UPDATE_PROFILE)
            .addParam("userId", userId)
            .addParam("accountNumber", StringHelper.toEnglishDigits(accountNumber))
            .addParam("cardNumber", StringHelper.toEnglishDigits(cardNumber.replaceAll("-", "")))
            .addParam("sheba", StringHelper.toEnglishDigits(sheba))
            .listener(updateProfile)
            .put();

  }

  RequestHelper.Callback updateProfile = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            JSONObject data = obj.getJSONObject("data");

            boolean status = data.getBoolean("status");

            if (success) {
              new GeneralDialog()
                      .title("به روزرسانی")
                      .message("اطلاعات شما با موفقـیت به روز رسانی شد")
                      .firstButton("باشه", null)
                      .show();
              if (edtAccountNum != null)
                MyApplication.prefManager.setAccountNumber(edtAccountNum.getText().toString());
              if (edtIben != null)
                MyApplication.prefManager.setSheba(edtIben.getText().toString());
              if (edtCardNumber != null)
                MyApplication.prefManager.setCardNumber(edtCardNumber.getText().toString());
            }

          } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "AccountFragment class, updateProfile onResponse method");
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
    }
  };

  private void payment(int userId) {

    RequestHelper.builder(EndPoints.PAYMENT)
            .addParam("userId", userId)
            .listener(Payment)
            .post();

  }

  RequestHelper.Callback Payment = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");
            boolean status = dataObj.getBoolean("status");

            if (success) {
              new GeneralDialog()
                      .title("ارسال شد")
                      .message("درخواست شما با موفقیت ارسال شد")
                      .firstButton("باشه", null)
                      .show();
            } else {
              new GeneralDialog()
                      .title("خطا")
                      .message(message)
                      .firstButton("تلاش مجدد", () -> payment(MyApplication.prefManager.getUserCode()))
                      .secondButton("بعدا امتحان میکنم", null)
                      .show();
            }

          } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "AccountFragment class, Payment onResponse method");
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
    }
  };

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
