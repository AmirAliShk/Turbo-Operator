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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

  private Listener listener;
  private static Dialog dialog;

  public void show(Listener listener) {
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
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    listCity = dialog.findViewById(R.id.listCity);

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
      AvaCrashReporter.send(e,"CityDialog class, show method");

    }

    cityAdapter = new CityAdapter(cityModels, MyApplication.context);
    listCity.setAdapter(cityAdapter);

    listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.selectedCity(position);
        dismiss();
      }
    });
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
      AvaCrashReporter.send(e,"CityDialog: Crash in show dialog line 89");

    }

  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
      AvaCrashReporter.send(e,"CityDialog class, dismiss method");
    }
    dialog = null;
  }

}
