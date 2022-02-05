package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverTurnoverAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogDriverTurnoverBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverTurnoverDialog {

    public static final String TAG = DriverTurnoverDialog.class.getSimpleName();
    Dialog dialog;
    DialogDriverTurnoverBinding binding;
    ArrayList<DriverTurnoverModel> driverTurnoverModels;
    DriverTurnoverAdapter adapter;

    public void show(JSONArray data) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogDriverTurnoverBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        dialog.setCancelable(false);

        fillList(data);

        binding.imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    void fillList(JSONArray dataArr) {
        driverTurnoverModels = new ArrayList<>();
        try {
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                DriverTurnoverModel model = new DriverTurnoverModel();
                model.setDate(dataObj.getString("date"));
                model.setTime(dataObj.getString("time"));
                model.setDescription(dataObj.getString("sharh"));
                model.setDebit(dataObj.getString("debit"));
                model.setCredit(dataObj.getString("credit"));
                driverTurnoverModels.add(model);
            }

            if (driverTurnoverModels.size() == 0) {
//                if (binding.vfFinancial != null)
                    binding.vfFinancial.setDisplayedChild(1);
            } else {
//                if (binding.vfFinancial != null)
                    binding.vfFinancial.setDisplayedChild(0);
                adapter = new DriverTurnoverAdapter(driverTurnoverModels);
                binding.listDriverTurnover.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, fillList method");
        }
    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}