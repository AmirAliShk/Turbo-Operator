package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogEditAddressBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ErrorAddressDialog {

    private static final String TAG = ErrorAddressDialog.class.getSimpleName();

    public interface Listener {
        void address(String address);
    }

    DialogEditAddressBinding binding;
    static Dialog dialog;
    Listener listener;

    public void show(String passengerAddress, String serviceId, Listener listener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogEditAddressBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);
        this.listener = listener;

        binding.edtAddress.setText(passengerAddress);

        binding.imgClose.setOnClickListener(view -> dismiss());

        binding.btnSubmit.setOnClickListener(view -> {
            KeyBoardHelper.hideKeyboard();
            String address = binding.edtAddress.getText().toString();

            if (address.isEmpty()) {
                binding.edtAddress.setError("آدرس نباید خالی باشد");
                return;
            }

            if (passengerAddress.trim().equals(address.trim())) {
                MyApplication.Toast("آدرس تغییری نکرد.", Toast.LENGTH_SHORT);
                dismiss();
                return;
            }

            editAddress(serviceId, address);
        });

        dialog.show();
    }

    private void editAddress(String serviceId, String address) {
        if (binding.vfLoader != null) {
            binding.vfLoader.setDisplayedChild(1);
        }
        LoadingDialog.makeCancelableLoader();
        RequestHelper.builder(EndPoints.EDIT_ADDRESS)
                .addParam("serviceId", serviceId)
                .addParam("adrs", address)
                .listener(onEditAddress)
                .put();
    }

    RequestHelper.Callback onEditAddress = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject dataObj = object.getJSONObject("data");
                        boolean status = dataObj.getBoolean("status");
                        if (status) {
                            listener.address(binding.edtAddress.getText().toString());
                            dismiss();
                            new GeneralDialog()
                                    .title("تایید شد")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .title("خطا")
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("باشه", null)
                                    .show();
                        }
                    } else {
                        new GeneralDialog()
                                .title("خطا")
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", null)
                                .show();
                    }

                    if (binding.vfLoader != null) {
                        binding.vfLoader.setDisplayedChild(0);
                    }

                    LoadingDialog.dismissCancelableDialog();
                } catch (Exception e) {
                    LoadingDialog.dismissCancelableDialog();
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(LoadingDialog::dismissCancelableDialog);
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            AvaCrashReporter.send(e, "ErrorAddressDialog class, dismiss method");
        }
        dialog = null;
    }
}