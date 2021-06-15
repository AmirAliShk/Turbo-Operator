package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.adapter.StationInfoAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SearchStationInfoDialog {
    private StationInfoAdapter stationInfoAdapter;
    ArrayList<StationInfoModel> stationInfoModels;
    private static final String TAG = SearchStationInfoDialog.class.getSimpleName();
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

    Spinner spSearchType;
    int city;
    RelativeLayout rlSearchType;

    String stationCode = "0";
    String address = "0";

    public void show(int city) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_search_station_info);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);
        this.city = city;
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
        spSearchType = dialog.findViewById(R.id.spSearchType);
        rlSearchType = dialog.findViewById(R.id.rlSearchType);

        imgSearch.setOnClickListener(view -> {
            String origin = edtStationCode.getText().toString();
            if (origin.isEmpty()) {
                MyApplication.Toast("لطفا آدرس یا شماره ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }
            getStationInfo();
        });

        imgClear.setOnClickListener(view -> {
            edtStationCode.setText("");
            if (vfStationInfo != null)
                vfStationInfo.setDisplayedChild(0);
        });

        llCLose.setOnClickListener(view -> dismiss());

        rlSearchType.setOnClickListener(view -> {
            spSearchType.performClick();
        });

        initWaitingTimeSpinner();

        dialog.show();

    }

    private void initWaitingTimeSpinner() {
        ArrayList<String> searchType = new ArrayList<String>(Arrays.asList("کد ایستگاه", "آدرس"));
        try {

            if (spSearchType == null)
                return;

            spSearchType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, searchType));

            spSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        stationCode = StringHelper.toEnglishDigits(edtStationCode.getText().toString());
                        address = "0";
                    } else if (position == 1) {
                        stationCode = "0";
                        address = edtStationCode.getText().toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStationInfo() {
        if (vfStationInfo != null)
            vfStationInfo.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.STATION_INFO + "/" + city + "/" + stationCode + "/" + address)
//                .addPath(StringHelper.toEnglishDigits(stationCode) + "")
                .listener(getStationInfo)
                .get();
    }

    RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    MyApplication.handler.postDelayed(() -> KeyBoardHelper.hideKeyboard(), 100);
                    if (vfStationInfo != null)
                        vfStationInfo.setDisplayedChild(2);
                    boolean isCountrySide = false;
                    String stationName = "";
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

                            txtStationCode.setText(StringHelper.toPersianDigits(dataObj.getString("stcode") + ""));

                            if (stationInfoModel.getStreet().isEmpty()) continue;

                            stationInfoModels.add(stationInfoModel);
                        }

                        if (stationInfoModels.size() == 0) {
                            if (vfStationInfo != null)
                                vfStationInfo.setDisplayedChild(4);
                        } else {
                            if (txtStationCode == null) return;
                            stationInfoAdapter = new StationInfoAdapter(stationInfoModels, MyApplication.context);
                            listStationInfo.setAdapter(stationInfoAdapter);

                            if (stationName.equals("")) {
                                txtStationName.setText("ثبت نشده");
                            } else {
                                txtStationName.setText(stationName);
                            }

                            if (isCountrySide) {
                                llSuburbs.setVisibility(View.VISIBLE);
                            } else {
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
                    if (vfStationInfo != null)
                        vfStationInfo.setDisplayedChild(3);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfStationInfo != null)
                    vfStationInfo.setDisplayedChild(3);
            });
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                MyApplication.handler.postDelayed(() -> KeyBoardHelper.hideKeyboard(), 200);
            }
        } catch (Exception e) {
            AvaCrashReporter.send(e, "SearchStationInfoDialog class, dismiss method");
        }
        dialog = null;
    }

}
