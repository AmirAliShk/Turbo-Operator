package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogHireBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.HireTypeModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class HireDialog {

    private static final String TAG = HireDialog.class.getSimpleName();
    DialogHireBinding binding;

    public interface Listener {
        void onClose(boolean b);
    }

    Listener listener;
    private int hireType;
    static Dialog dialog;

    public void show(Listener listener, String mobile, String name, int cityCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogHireBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        getHireType();

        binding.llHire.setOnClickListener(view -> {
            return;
        });
        binding.blrView.setOnClickListener(view -> dismiss());

        binding.imgClose.setOnClickListener(view -> dismiss());

        binding.btnSubmit.setOnClickListener(view -> new GeneralDialog()
                .title("استخدامی")
                .message("آیا از ثبت درخواست اطمینان دارید؟")
                .firstButton("بله", () ->
                        setHire(name, mobile, binding.edtComment.getText().toString(), hireType, cityCode))
                .secondButton("خیر", null)
                .show());

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }

    private void setHire(String name, String phoneNumber, String comment, int hireType, int cityCode) {
//        if (binding.vfLoader != null)
            binding.vfLoader.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.HIRE)
                .addParam("phoneNumber", phoneNumber)
                .addParam("cityCode", cityCode)
                .addParam("name", name)
                .addParam("comment", comment)
                .addParam("type", hireType)
                .listener(setHire)
                .post();
    }

    RequestHelper.Callback setHire = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                  {"success":true,"message":"","data":{"status":true}}
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .title("ثبت شد")
                                    .message(message)
                                    .firstButton("باشه", () -> {
                                        dismiss();
                                        listener.onClose(true);
                                    })
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
//                    if (binding.vfLoader != null)
                        binding.vfLoader.setDisplayedChild(0);

                } catch (Exception e) {
                    e.printStackTrace();
//                    if (binding.vfLoader != null) {
                        binding.vfLoader.setDisplayedChild(0);

                    AvaCrashReporter.send(e, TAG + " class, setHire onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                    binding.vfLoader.setDisplayedChild(0);

            });
        }
    };

    private void getHireType() {
        RequestHelper.builder(EndPoints.HIRE_TYPES)
                .listener(getHireType)
                .get();
    }

    RequestHelper.Callback getHireType = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                ArrayList<HireTypeModel> hireTypeModels = new ArrayList<>();
                ArrayList<String> hireTypes = new ArrayList<String>();
                try {
                    Log.i(TAG, "run: " + args[0].toString());
                    JSONObject hireObj = new JSONObject(args[0].toString());
                    boolean success = hireObj.getBoolean("success");
                    String message = hireObj.getString("message");

                    if (success) {
                        JSONArray data = hireObj.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            HireTypeModel hireTypeModel = new HireTypeModel();
                            hireTypeModel.setName(obj.getString("name"));
                            hireTypeModel.setId(obj.getInt("id"));
                            hireTypeModels.add(hireTypeModel);
                            hireTypes.add(obj.getString("name"));
                        }
//                        if (binding.spHireType != null)
                            binding.spHireType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner_right, hireTypes));
                            binding.spHireType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                    if (spHireType != null)
//                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                                    hireType = hireTypeModels.get(position).getId();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

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
                    AvaCrashReporter.send(e, TAG + " class, getHireType onResponse method");
                }
            });
        }
    };
}