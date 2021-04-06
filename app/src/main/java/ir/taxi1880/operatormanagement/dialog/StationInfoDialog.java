package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.mmin18.widget.RealtimeBlurView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverTurnoverAdapter;
import ir.taxi1880.operatormanagement.adapter.StationInfoAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class StationInfoDialog {
    private StationInfoAdapter stationInfoAdapter;
    ArrayList<StationInfoModel> stationInfoModels;
    private static final String TAG = StationInfoDialog.class.getSimpleName();
    int stationCode;
    private static Dialog dialog;
    private ListView listStationInfo;
    TextView txtStationCode;
    TextView txtStationName;
    EditText edtStationCode;
    LinearLayout llSuburbs;
    LinearLayout llSearchStation;
    ViewFlipper vfStationInfo;
    ImageView imgClear;
    ImageView imgSearch;
    RealtimeBlurView blrView;

    public void show(int stationCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_station_info);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(),MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        listStationInfo = dialog.findViewById(R.id.listStationInfo);
        llSearchStation = dialog.findViewById(R.id.llSearchStation);
        txtStationName = dialog.findViewById(R.id.txtStationName);
        llSuburbs = dialog.findViewById(R.id.llSuburbs);
        edtStationCode = dialog.findViewById(R.id.edtStationCode);
        txtStationCode = dialog.findViewById(R.id.txtStationCode);
        LinearLayout llCLose = dialog.findViewById(R.id.llCLose);
        imgSearch = dialog.findViewById(R.id.imgSearch);
        imgClear = dialog.findViewById(R.id.imgClear);
        vfStationInfo = dialog.findViewById(R.id.vfStationInfo);
        blrView = dialog.findViewById(R.id.blrView);

        this.stationCode = stationCode;

        if (stationCode == 0) {
            llSearchStation.setVisibility(View.VISIBLE);
            if (vfStationInfo != null)
                vfStationInfo.setDisplayedChild(2);
        } else {
            llSearchStation.setVisibility(View.GONE);
            getStationInfo(stationCode + "");
        }
        blrView.setOnClickListener(view -> dismiss());

        imgSearch.setOnClickListener(view -> {
            String origin = edtStationCode.getText().toString();
            if (origin.isEmpty()) {
                MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
                return;
            }
            KeyBoardHelper.hideKeyboard();
            getStationInfo(origin);
        });

        imgClear.setOnClickListener(view -> {
            edtStationCode.setText("");
            if (vfStationInfo != null)
                vfStationInfo.setDisplayedChild(2);
        });

        llCLose.setOnClickListener(view -> dismiss());

        dialog.show();

    }

    private void getStationInfo(String stationCode) {
        if (vfStationInfo != null)
            vfStationInfo.setDisplayedChild(0);
        KeyBoardHelper.hideKeyboard();
        RequestHelper.builder(EndPoints.STATION_INFO)
                .addPath(StringHelper.toEnglishDigits(stationCode) + "")
                .listener(getStationInfo)
                .get();
    }

    RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    KeyBoardHelper.hideKeyboard();
                    boolean isCountrySide = false;
                    String stationName = "";
                    Log.i("TAG", "onResponse: " + args[0].toString());
                    stationInfoModels = new ArrayList<>();
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");

                    if (success) {
                        JSONArray dataArr = obj.getJSONArray("data");
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
                            if (stationInfoModel.getStreet().isEmpty()) continue;
                            stationInfoModels.add(stationInfoModel);
                        }

                        if (stationInfoModels.size() == 0) {
                            MyApplication.Toast("اطلاعاتی موجود نیست", Toast.LENGTH_SHORT);
                        } else {

                            if (txtStationCode == null) return;

                            stationInfoAdapter = new StationInfoAdapter(stationInfoModels, MyApplication.context);
                            listStationInfo.setAdapter(stationInfoAdapter);

                            if (vfStationInfo != null)
                                vfStationInfo.setDisplayedChild(1);

                            txtStationCode.setText(stationCode + "");
                            if (stationName.equals("")) {
                                txtStationName.setText("ثبت نشده");
                            } else {
                                txtStationName.setText(stationName);
                            }

                            if (isCountrySide) {
                                llSuburbs.setVisibility(View.VISIBLE);
                            }else {
                                llSuburbs.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "StationInfoDialog class, dismiss method");
        }
        dialog = null;
    }

}
