package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.CityAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogSelectCityBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class CityDialog {

    private static final String TAG = CityDialog.class.getSimpleName();
    DialogSelectCityBinding binding;

    public interface Listener {
        void selectedCity(int position);
    }

    private CityAdapter cityAdapter;
//    private Listener listener;
    String title;
    private static Dialog dialog;

    public void show(Listener listener, boolean isCity) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogSelectCityBinding.inflate(LayoutInflater.from(dialog.getContext()));
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
//        this.listener = listener;

        binding.txtTitle.setText(title);
        binding.llSelectCity.setOnClickListener(view -> {
            return;
        });
        binding.blrView.setOnClickListener(view -> dismiss());

        if (isCity) {
            ArrayList<CityModel> cityModels = new ArrayList<>();
            try {
                JSONArray cityArr = new JSONArray(MyApplication.prefManager.getCity());
                for (int i = 0; i < cityArr.length(); i++) {
                    JSONObject cityObj = cityArr.getJSONObject(i);
                    CityModel cityModel = new CityModel();
                    cityModel.setCity(cityObj.getString("cityname"));
                    cityModel.setId(cityObj.getInt("cityid"));
                    cityModel.setCityLatin(cityObj.getString("latinName"));
                    cityModels.add(cityModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                AvaCrashReporter.send(e, TAG + " class, show method");
            }
            cityAdapter = new CityAdapter(cityModels);
            binding.listCity.setAdapter(cityAdapter);
            binding.listCity.setOnItemClickListener((parent, view, position, id) -> {
                listener.selectedCity(position);
                dismiss();
            });

        } else {
            ArrayList<String> waitingTime = new ArrayList<>(Arrays.asList("بدون توقف", "۵ دقیقه", "۱۰ دقیقه", "۲۰ دقیقه", "۳۰ دقیقه", "۴۰ دقیقه", "۵۰ دقیقه", "۱ ساعت", "۱.۵ ساعت", "۲ ساعت", "۲.۵ ساعت", "۳ ساعت"));
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(MyApplication.currentActivity, R.layout.item_city, R.id.txtCity, waitingTime);
            binding.listCity.setAdapter(adapter);
            binding.listCity.setOnItemClickListener((adapterView, view, position, l) -> {
                listener.selectedCity(position);
                dismiss();
            });
        }

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + ": Crash in show dialog");
        }
    }

    public CityDialog setTitle(String title) {
        binding.txtTitle.setText(title);
        return this;
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}