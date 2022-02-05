package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import ir.taxi1880.operatormanagement.databinding.DialogSearchStationInfoBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SearchStationInfoDialog {
    private static final String TAG = SearchStationInfoDialog.class.getSimpleName();
    DialogSearchStationInfoBinding binding;
    private StationInfoAdapter stationInfoAdapter;
    ArrayList<StationInfoModel> stationInfoModels;
    StationInfoModel stationInfoModel;
    private static Dialog dialog;
    int city;
    RelativeLayout rlSearchType;
    String stationCode = "0";
    String address = "0";
    boolean firstTime = false;


    public interface Listener {
        void stationCode(String stationCode);
    }

    public void show(Listener listener, int city, boolean isFromAddress, String station, boolean isFromDetermination) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogSearchStationInfoBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
        this.city = city;

        initWaitingTimeSpinner();
        binding.imgSearch.setOnClickListener(view -> {
            String origin = binding.edtStationCode.getText().toString();
            if (origin.isEmpty()) {
                MyApplication.Toast("لطفا آدرس یا شماره ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                return;
            }
            if (binding.spSearchType.getSelectedItemPosition() == 0) {
                stationCode = StringHelper.toEnglishDigits(binding.edtStationCode.getText().toString());
                address = "0";
            } else if (binding.spSearchType.getSelectedItemPosition() == 1) {
                stationCode = "0";
                address = binding.edtStationCode.getText().toString() + "";
            }

            getStationInfo(city, stationCode, address);
            KeyBoardHelper.hideKeyboard();
        });

        binding.imgClear.setOnClickListener(view -> {
            binding.edtStationCode.setText("");
                binding.vfStationInfo.setDisplayedChild(0);
        });

        binding.llCLose.setOnClickListener(view -> dismiss());

        binding.rlSearchType.setOnClickListener(view -> {
            binding.spSearchType.performClick();
        });

        binding.edtStationCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String origin = binding.edtStationCode.getText().toString();
                if (origin.isEmpty()) {
                    MyApplication.Toast("لطفا نام یا شماره ایستگاه را وارد کنید.", Toast.LENGTH_SHORT);
                    return false;
                }
                if (binding.spSearchType.getSelectedItemPosition() == 0) {
                    stationCode = StringHelper.toEnglishDigits(binding.edtStationCode.getText().toString());
                    address = "0";
                } else if (binding.spSearchType.getSelectedItemPosition() == 1) {
                    stationCode = "0";
                    address = binding.edtStationCode.getText().toString() + "";
                }

                getStationInfo(city, stationCode, address);

                return true;
            }
            return false;
        });


        if (isFromAddress) {
            binding.spSearchType.setSelection(1);
        }

        if (!station.equals("")) {
            stationCode = station;
            binding.edtStationCode.setText(station);
            binding.spSearchType.setSelection(0);
            getStationInfo(city, station, address);
            KeyBoardHelper.hideKeyboard();
        }
        if (isFromDetermination) {
            binding.listStationInfo.setOnItemClickListener((adapterView, view, i, l) -> {
                listener.stationCode(stationInfoModels.get(i).getStcode() + "");
                Log.i(TAG, "show: " + stationInfoModels.get(i).getStcode() + "");
                dismiss();
            });
        }

        dialog.show();

    }

    private void initWaitingTimeSpinner() {
        ArrayList<String> searchType = new ArrayList<>(Arrays.asList("کد ایستگاه", "آدرس"));
        try {
            binding.spSearchType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, searchType));
            binding.spSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (spSearchType != null)
//                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    if (firstTime) {
                        binding.edtStationCode.setText("");
                    }
                    firstTime = true;

                    if (binding.spSearchType.getSelectedItemPosition() == 0) {
                        binding.edtStationCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    if (binding.spSearchType.getSelectedItemPosition() == 1) {
                        binding.edtStationCode.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, initWaitingTimeSpinner method");
        }
    }

    private void getStationInfo(int city, String stationCode, String address) {
//        if (binding.vfStationInfo != null)
            binding.vfStationInfo.setDisplayedChild(1);
        KeyBoardHelper.hideKeyboard();
        RequestHelper.builder(EndPoints.STATION_INFO)
//                .addPath(StringHelper.toEnglishDigits(stationCode) + "")
                .addPath(city + "")
                .addPath(stationCode + "")
                .addPath(address + "")
                .listener(getStationInfo)
                .get();
    }

    RequestHelper.Callback getStationInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    MyApplication.handler.postDelayed(KeyBoardHelper::hideKeyboard, 100);
                        binding.vfStationInfo.setDisplayedChild(2);
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
                            stationInfoModel = new StationInfoModel();
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

                            binding.txtStationCode.setText(StringHelper.toPersianDigits(dataObj.getString("stcode") + ""));

                            if (stationInfoModel.getStreet().isEmpty()) continue;

                            if (stationCode.equals("0")) {
                                binding.llStationHeader.setVisibility(View.GONE);
                                stationInfoModel.setAddressOrNot("address");
                            } else {
                                binding.llStationHeader.setVisibility(View.VISIBLE);
                                stationInfoModel.setAddressOrNot("stationCode");
                            }

                            stationInfoModels.add(stationInfoModel);
                        }

                        if (stationInfoModels.size() == 0) {
//                            if (binding.vfStationInfo != null)
                                binding.vfStationInfo.setDisplayedChild(4);
                        } else {
//                            if (binding.txtStationCode == null) return;
                            stationInfoAdapter = new StationInfoAdapter(stationInfoModels);
                            binding.listStationInfo.setAdapter(stationInfoAdapter);

                            if (stationName.equals("")) {
                                binding.txtStationName.setText("ثبت نشده");
                            } else {
                                binding.txtStationName.setText(StringHelper.toPersianDigits(stationName));
                            }

                            if (isCountrySide) {
                                binding.llSuburbs.setVisibility(View.VISIBLE);
                            } else {
                                binding.llSuburbs.setVisibility(View.GONE);
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
                    AvaCrashReporter.send(e, TAG + " class, getStationInfo method");
//                    if (binding.vfStationInfo != null)
                        binding.vfStationInfo.setDisplayedChild(3);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
//                if (binding.vfStationInfo != null)
                    binding.vfStationInfo.setDisplayedChild(3);
            });
        }
    };

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                MyApplication.handler.postDelayed(KeyBoardHelper::hideKeyboard, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}