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

    public interface Listener {
        void description(String address, int stationCode);

//    void selectedAddress(boolean b);
    }

    private StationInfoAdapter stationInfoAdapter;
    private ListView listStationInfo;
    ArrayList<StationInfoModel> stationInfoModels;
    private static final String TAG = StationInfoDialog.class.getSimpleName();
    int stationCode;
    TextView txtStationCode;
    TextView txtTitle;
    TextView txtCountrySide;
    ViewFlipper vfSearchStation;
    RealtimeBlurView blrView;
    private Listener listener;
    private static Dialog dialog;

    public void show(int stationCode) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_station_info);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        listStationInfo = dialog.findViewById(R.id.listStationInfo);
        txtTitle = dialog.findViewById(R.id.txtTitle);
        txtCountrySide = dialog.findViewById(R.id.txtCountrySide);
        txtStationCode = dialog.findViewById(R.id.txtStationCode);
        LinearLayout llCLose = dialog.findViewById(R.id.llCLose);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        vfSearchStation = dialog.findViewById(R.id.vfSearchStation);
        blrView = dialog.findViewById(R.id.blrView);

        blrView.setOnClickListener(view -> dismiss());
        this.stationCode = stationCode;

        if (stationCode == 0) {
            if (vfSearchStation != null)
                vfSearchStation.setDisplayedChild(0);
        } else {
            if (vfSearchStation != null)
                vfSearchStation.setDisplayedChild(1);
            getStationInfo(stationCode + "");
        }

        btnSubmit.setOnClickListener(view -> {
            String origin = txtStationCode.getText().toString();
            if (origin.isEmpty()) {
                MyApplication.Toast("لطفا شماره ایستگاه را وارد کنید", Toast.LENGTH_SHORT);
                return;
            }
            getStationInfo(origin);
            KeyBoardHelper.hideKeyboard();
        });

        llCLose.setOnClickListener(view -> dismiss());

        dialog.show();

    }

    private void getStationInfo(String stationCode) {
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

                            if (vfSearchStation != null)
                                vfSearchStation.setDisplayedChild(1);

                            if (stationName.equals("")) {
                                txtTitle.setText("کد ایستگاه : " + txtStationCode.getText().toString());
                            } else {
                                txtTitle.setText(stationName + " \n " + "کد ایستگاه : " + txtStationCode.getText().toString());
                            }

                            if (isCountrySide) {
                                txtCountrySide.setVisibility(View.VISIBLE);
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
