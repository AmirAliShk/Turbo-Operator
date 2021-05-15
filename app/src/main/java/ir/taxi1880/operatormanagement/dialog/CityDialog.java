package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mmin18.widget.RealtimeBlurView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.CityAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class CityDialog {

    private static final String TAG = CityDialog.class.getSimpleName();

    public interface Listener {
        void selectedCity(int position);
    }

    private CityAdapter cityAdapter;
    private ListView listCity;
    RealtimeBlurView blrView;
    private Listener listener;
    private static Dialog dialog;
    LinearLayout llSelectCity;
    TextView txtTitle;
    String title;

    public void show(Listener listener, boolean isCity) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_select_city);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        listCity = dialog.findViewById(R.id.listCity);
        blrView = dialog.findViewById(R.id.blrView);
        llSelectCity = dialog.findViewById(R.id.llSelectCity);
        txtTitle = dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        llSelectCity.setOnClickListener(view -> {
            return;
        });
        blrView.setOnClickListener(view -> dismiss());

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
                AvaCrashReporter.send(e, "CityDialog class, show method");

            }

            cityAdapter = new CityAdapter(cityModels, MyApplication.context);
            listCity.setAdapter(cityAdapter);

        } else {
            ArrayList<String> waitingTime = new ArrayList<>(Arrays.asList("بدون توقف", "۵ دقیقه", "۱۰ دقیقه", "۲۰ دقیقه", "۳۰ دقیقه", "۴۰ دقیقه", "۵۰ دقیقه", "۱ ساعت", "۱.۵ ساعت", "۲ ساعت", "۲.۵ ساعت", "۳ ساعت"));

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(MyApplication.currentActivity, R.layout.item_city, R.id.txtCity, waitingTime);
            listCity.setAdapter(adapter);

            listCity.setOnItemClickListener((adapterView, view, position, l) -> {
                // TODO Auto-generated method stub
                String value=adapter.getItem(position);
                Toast.makeText(MyApplication.context,value,Toast.LENGTH_SHORT).show();

            });

        }

        listCity.setOnItemClickListener((parent, view, position, id) -> {
            listener.selectedCity(position);
            dismiss();
        });

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "CityDialog: Crash in show dialog line 89");

        }

    }

    public CityDialog setTitle(String title) {
        this.title = title;
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
            AvaCrashReporter.send(e, "CityDialog class, dismiss method");
        }
        dialog = null;
    }

}
