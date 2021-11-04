package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentAccountBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.LoadingDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.ServiceHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.push.AvaService;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class AccountFragment extends Fragment {

    public static final String TAG = AccountFragment.class.getSimpleName();
    FragmentAccountBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.txtCharge, MyApplication.IraSanSBold);

        getBalance();

        StringHelper.setCharAfterOnTime(binding.edtCardNumber, " - ", 4);
        binding.edtAccountNum.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getAccountNumber()));
        binding.edtCardNumber.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getCardNumber()));
        binding.edtIben.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getSheba()));

        binding.btnCheckOut.setOnClickListener(view -> new GeneralDialog()
                .title("هشدار")
                .message("آیا از درخواست تسویه حساب خود اطمینان دارید؟")
                .firstButton("بله مطمئنم", this::payment)
                .secondButton("پشیمون شدم", null)
                .show());

        binding.llLogout.setOnClickListener(view -> new GeneralDialog()
                .title("خروج از حساب")
                .message("میخواهید از حساب خود خارج شوید؟")
                .firstButton("بله", () -> {
                    MyApplication.currentActivity.finish();
                    ServiceHelper.stop(context, LinphoneService.class);
                    ServiceHelper.stop(context, AvaService.class);
                    MyApplication.prefManager.cleanPrefManger();
                    MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, SplashActivity.class));
                })
                .secondButton("نه", null)
                .cancelable(true)
                .show());

        binding.rlParent.setOnClickListener(view -> KeyBoardHelper.hideKeyboard());

        binding.btnUpdate.setOnClickListener(view -> {
            String cardNumber = binding.edtCardNumber.getText().toString().replaceAll(" ", "");
            new GeneralDialog()
                    .title("به روز رسانی")
                    .message("اطلاعات شما به روز شود؟")
                    .firstButton("بله", () ->
                            updateProfile(binding.edtAccountNum.getText().toString(), cardNumber, binding.edtIben.getText().toString()))
                    .secondButton("خیر", null)
                    .show();
        });

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    private void getBalance() {
        if (binding.vfBalance != null)
            binding.vfBalance.setDisplayedChild(0);

        RequestHelper.builder(EndPoints.BALANCE)
                .listener(getBalance)
                .get();

    }

    RequestHelper.Callback getBalance = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                        {"success":true,"message":"","data":{"accountBalance":230037}}
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        String accountBalance = dataObj.getString("accountBalance");
                        String balance = StringHelper.setComma(accountBalance);
                        if (binding.txtCharge != null)
                            binding.txtCharge.setText(StringHelper.toPersianDigits(balance + ""));
                        if (binding.vfBalance != null)
                            binding.vfBalance.setDisplayedChild(1);
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
                            if (binding.edtAccountNum != null)
                                MyApplication.prefManager.setAccountNumber(binding.edtAccountNum.getText().toString());
                            if (binding.edtIben != null)
                                MyApplication.prefManager.setSheba(binding.edtIben.getText().toString());
                            if (binding.edtCardNumber != null)
                                MyApplication.prefManager.setCardNumber(binding.edtCardNumber.getText().toString());
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
    }
}
