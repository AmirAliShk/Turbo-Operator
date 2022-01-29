package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.StationInfoAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogStationInfoBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class StationInfoDialog {
    private static final String TAG = StationInfoDialog.class.getSimpleName();
    DialogStationInfoBinding binding;
    private StationInfoAdapter stationInfoAdapter;
    ArrayList<StationInfoModel> stationInfoModels;
    int stationCode;
    String stationName;
    boolean isCountrySide = false;
    private static Dialog dialog;

    public void show(JSONArray dataArr) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogStationInfoBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        getStationInfo(dataArr);

        binding.llCLose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private void getStationInfo(JSONArray dataArr) {
        try {
            stationInfoModels = new ArrayList<>();
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                StationInfoModel stationInfoModel = new StationInfoModel();
                stationInfoModel.setStcode(dataObj.getInt("stcode"));
                stationInfoModel.setStreet(dataObj.getString("street"));
                stationInfoModel.setOdd(dataObj.getString("odd"));
                stationInfoModel.setEven(dataObj.getString("even"));
                stationInfoModel.setStationName(dataObj.getString("stationName"));
                stationInfoModel.setCountrySide(dataObj.getInt("countrySide"));
                isCountrySide = dataObj.getInt("countrySide") == 1;

                if (!dataObj.getString("stationName").equals("")) {
                    stationName = dataObj.getString("stationName");
                }

                stationCode = dataObj.getInt("stcode");

                if (stationInfoModel.getStreet().isEmpty()) continue;
                stationInfoModels.add(stationInfoModel);
            }

            if (stationInfoModels.size() == 0) {
                MyApplication.Toast("اطلاعاتی موجود نیست", Toast.LENGTH_SHORT);
            } else {

                if (binding.txtStationCode == null) return;

                stationInfoAdapter = new StationInfoAdapter(stationInfoModels);
                binding.listStationInfo.setAdapter(stationInfoAdapter);

                binding.txtStationCode.setText(StringHelper.toEnglishDigits(stationCode + ""));
                if (stationName.equals("")) {
                    binding.txtStationName.setText("ثبت نشده");
                } else {
                    binding.txtStationName.setText(stationName);
                }

                if (isCountrySide) {
                    binding.llSuburbs.setVisibility(View.VISIBLE);
                } else {
                    binding.llSuburbs.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, getStationInfo method");
        }
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
}