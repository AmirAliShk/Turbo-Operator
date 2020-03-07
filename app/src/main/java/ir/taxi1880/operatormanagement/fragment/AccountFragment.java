package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

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

    edtCardNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean flag = true;
        String eachBlock[] = edtCardNumber.getText().toString().split(" ");
        for (int i = 0; i < eachBlock.length; i++) {
          if (eachBlock[i].length() > 4) {
            flag = false;
          }
        }

        if (flag) {
          edtCardNumber.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL)
              keyDel = 1;
            return false;
          });

          if (keyDel == 0) {
            if (((edtCardNumber.getText().length() + 1) % 5) == 0) {

              if (edtCardNumber.getText().toString().split(" ").length <= 3) {
                edtCardNumber.setText(edtCardNumber.getText() + " ");
                edtCardNumber.setSelection(edtCardNumber.getText().length());
              }
            }
            a = edtCardNumber.getText().toString();
          } else {
            a = edtCardNumber.getText().toString();
            keyDel = 0;
          }

        } else {
          edtCardNumber.setText(a);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    edtAccountNum.setText(MyApplication.prefManager.getAccountNumber());
    edtCardNumber.setText(MyApplication.prefManager.getCardNumber());
    edtIben.setText(MyApplication.prefManager.getSheba());

    return view;
  }

  private void getBalance(int userId) {
    vfBalance.setDisplayedChild(0);

    RequestHelper.loadBalancingBuilder(EndPoints.BALANCE + "/" + userId)
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
            vfBalance.setDisplayedChild(1);
            JSONObject obj = new JSONObject(args[0].toString());
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");
            JSONObject dataObj = obj.getJSONObject("data");
            int accountBalance = dataObj.getInt("accountBalance");

            txtOperatorCharge.setText(StringHelper.toPersianDigits(accountBalance + " تومان "));

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
//      vfBalance.setDisplayedChild(1);
    }
  };

  private void UpdateProfile(int userId,String accountNumber,String sheba) {

    RequestHelper.loadBalancingBuilder(EndPoints.UPDATE_PROFILE)
            .addPath(userId + "")
            .addPath(accountNumber + "")
            .addPath(sheba + "")
            .listener(UpdateProfile)
            .put();

  }

  RequestHelper.Callback UpdateProfile = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject obj = new JSONObject(args[0].toString());


          } catch (JSONException e) {
            e.printStackTrace();
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
