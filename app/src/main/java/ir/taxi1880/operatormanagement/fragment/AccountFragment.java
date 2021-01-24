package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
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

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.txtCharge)
    TextView txtCharge;

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
                .firstButton("بله مطمئنم", () -> payment())
                .secondButton("پشیمون شدم", null)
                .show();
    }

    @OnClick(R.id.rlParent)
    void onParent() {
        KeyBoardHelper.hideKeyboard();
    }

    @OnClick(R.id.btnUpdate)
    void OnUpdae() {
        String cardNumber = edtCardNumber.getText().toString().replaceAll(" ", "");
        new GeneralDialog()
                .title("به روز رسانی")
                .message("اطلاعات شما به روز شود؟")
                .firstButton("بله", () ->
                        updateProfile(edtAccountNum.getText().toString(), cardNumber, edtIben.getText().toString()))
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
        TypefaceUtil.overrideFonts(txtCharge, MyApplication.IraSanSBold);

        getBalance();

        StringHelper.setCharAfterOnTime(edtCardNumber, " - ", 4);
        edtAccountNum.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getAccountNumber()));
        edtCardNumber.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getCardNumber()));
        edtIben.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getSheba()));


        return view;
    }

    private void getBalance() {
        if (vfBalance != null)
            vfBalance.setDisplayedChild(0);

        RequestHelper.builder(EndPoints.BALANCE)
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
//                        {"success":true,"message":"","data":{"accountBalance":230037}}
                        JSONObject obj = new JSONObject(args[0].toString());
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");

                        if (success) {
                            JSONObject dataObj = obj.getJSONObject("data");
                            String accountBalance = dataObj.getString("accountBalance");
                            String balance = StringHelper.setComma(accountBalance);
                            if (txtCharge != null)
                                txtCharge.setText(StringHelper.toPersianDigits(balance + ""));
                            if (vfBalance != null)
                                vfBalance.setDisplayedChild(1);
                        } else {
                            new GeneralDialog()
                                    .title("هشدار")
                                    .message(message)
                                    .secondButton("باشه", null)
                                    .cancelable(false)
                                    .show();
                        }
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

    private void updateProfile(String accountNumber, String cardNumber, String sheba) {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.UPDATE_PROFILE)
                .addParam("accountNumber", StringHelper.toEnglishDigits(accountNumber))
                .addParam("cardNumber", StringHelper.toEnglishDigits(cardNumber.replaceAll("-", "")))
                .addParam("sheba", StringHelper.toEnglishDigits(sheba))
                .listener(updateProfile)
                .put();

    }

    RequestHelper.Callback updateProfile = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (status) {
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
                        } else {
                            new GeneralDialog()
                                    .title("هشدار")
                                    .message(message)
                                    .secondButton("باشه", null)
                                    .cancelable(false)
                                    .show();
                        }
                    }
                    LoadingDialog.dismissCancelableDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadingDialog.dismissCancelableDialog();
                    AvaCrashReporter.send(e, "AccountFragment class, updateProfile onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> LoadingDialog.dismissCancelableDialog());
        }

    };

    private void payment() {
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.PAYMENT)
                .listener(Payment)
                .post();

    }

    RequestHelper.Callback Payment = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("ارسال شد")
                                    .message("درخواست شما با موفقیت ارسال شد")
                                    .firstButton("باشه", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .title("هشدار")
                                    .message(message)
                                    .secondButton("باشه", null)
                                    .cancelable(false)
                                    .show();
                        }
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }

                    LoadingDialog.dismissCancelableDialog();
                } catch (JSONException e) {
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "AccountFragment class, Payment onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> LoadingDialog.dismissCancelableDialog());
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
