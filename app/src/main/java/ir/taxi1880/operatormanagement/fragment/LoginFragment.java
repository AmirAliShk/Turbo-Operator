package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

  public static final String TAG = LoginFragment.class.getSimpleName();
  Unbinder unbinder;

  @BindView(R.id.edtUserName)
  EditText edtUserName;

  @BindView(R.id.edtPassword)
  EditText edtPassword;

  @BindView(R.id.btnLogin)
  Button btnLogin;

  @OnClick(R.id.btnLogin)
  void onLogin() {
    if (edtUserName.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT);
      return;
    }
    if (edtPassword.getText().toString().isEmpty()) {
      MyApplication.Toast("لطفا رمز عبور خود را وارد نمایید", Toast.LENGTH_SHORT);
      return;
    }

    logIn(edtUserName.getText().toString(), edtPassword.getText().toString());
//        FragmentHelper
//                .toFragment(MyApplication.currentActivity, new LoginFragment())
//                .setAddToBackStack(false)
//                .replace();
    KeyBoardHelper.hideKeyboard();
  }

  @SuppressLint("SetTextI18n")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_login, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    return view;
  }

  private void logIn(String userName, String password) {

      RequestHelper.builder(EndPoints.LOGIN)
              .addParam("userName", userName)
              .addParam("password", password)
              .listener(onLogIn)
              .post();

  }

  RequestHelper.Callback onLogIn = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(() -> {
        try {
          Log.i(TAG, "onResponse: " + args[0].toString());
          JSONObject object = new JSONObject(args[0].toString());
          int status = object.getInt("status");
          int userId = object.getInt("userId");
          int block = object.getInt("isBlock");
          int accessInsertService = object.getInt("accessInsertService");
          int sipNumber = object.getInt("sipNumber");
          int pushId = object.getInt("pushId");
          String pushToken = object.getString("pushToken");
          String sipServer = object.getString("sipServer");
          String sipPassword = object.getString("sipPassword");
          String sheba = object.getString("sheba");
          String cardNumber = object.getString("cardNumber");
          String accountNumber = object.getString("accountNumber");
          int balance = object.getInt("balance");
          int activeInQueue = object.getInt("activeInQueue");
//          int isFinishContract = object.getInt("isFinishContract");
          int isFinishContract = 1;

          MyApplication.prefManager.setOperatorName(object.getString("name"));

          if (status == 1) {

            if (block == 1) {
              new GeneralDialog()
                      .title("هشدار")
                      .message("اکانت شما توسط سیستم مسدود شده است")
                      .firstButton("خروج از برنامه", () -> MyApplication.currentActivity.finish())
                      .show();
              return;
            }

            MyApplication.prefManager.setActivateStatus(activeInQueue == 1 ? true : false);
            MyApplication.prefManager.setUserCode(userId);
            MyApplication.prefManager.setAccessInsertService(accessInsertService);
            MyApplication.prefManager.setSipServer(sipServer);
            MyApplication.prefManager.setSipNumber(sipNumber);
            MyApplication.prefManager.setSipPassword(sipPassword);
            MyApplication.prefManager.setPushId(pushId);
            MyApplication.prefManager.setPushToken(pushToken);
            MyApplication.prefManager.setSheba(sheba);
            MyApplication.prefManager.setCardNumber(cardNumber);
            MyApplication.prefManager.setAccountNumber(accountNumber);
            MyApplication.prefManager.setBalance(balance);
            MyApplication.prefManager.setUserName((edtUserName.getText().toString()));
            MyApplication.prefManager.setPassword(edtPassword.getText().toString());
            /*TODO:(najafi) : this place is correct? */
            if (isFinishContract == 1){
              new GeneralDialog()
                      .title("اتمام قرار داد")
                      .message("مدت قرار داد شما به اتمام رسیده است. لطفا برای تمدید آن اقدام کنید.")
                      .cancelable(false)
                      .firstButton("مشاهده قرارداد", () -> {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new ContractFragment())
                                /*TODO(najafi) : dos it needed? and line 244*/
                                .setAddToBackStack(false)
                                .replace();
                      })
                      .secondButton("امضا قرارداد", () -> {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new SignatureFragment())
                                .setAddToBackStack(false)
                                .replace();
                      })
                      .show();
              return;
            }
            MyApplication.prefManager.isLoggedIn(true);
            MyApplication.avaStart();
            startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
          } else {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.titleText("خطایی رخ داده");
            errorDialog.messageText("نام کاربری یا رمز عبور اشتباه است");
            errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> logIn(edtUserName.getText().toString(), edtPassword.getText().toString()));
            errorDialog.closeBtnRunnable("بستن", null);
            errorDialog.cancelable(true);
            errorDialog.show();
          }

        } catch (Exception e) {
//                    new ErrorDialog()
//                            .messageText("پردازش داده های ورودی با مشکل مواجه شد")
//                            .closeBtnRunnable("بستن", () -> {
//
//                            })
//                            .tryAgainBtnRunnable("تلاش مجدد", () -> {
//
//                            })
//                            .show();
          e.printStackTrace();
          AvaCrashReporter.send(e,"LoginFragment class, onLogIn onResponse method");

        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
