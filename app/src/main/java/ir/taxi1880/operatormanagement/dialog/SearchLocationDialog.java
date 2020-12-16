package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.StationAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SearchLocationDialog {

  private static final String TAG = SearchLocationDialog.class.getSimpleName();

  public interface Listener {
    void description(String address, int code);
//    void onClose(boolean isClose);

//    void selectedAddress(boolean b);
  }

  private ArrayList<StationModel> stationModels;
  private StationAdapter addressAdapter;
  private ListView listPlace;
  private ViewFlipper vfLocation;

  private Listener listener;
  private static Dialog dialog;

  public void show(Listener listener, String title, String cityLatin) {
    if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
      return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_search_location);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    listPlace = dialog.findViewById(R.id.listPlace);
    vfLocation = dialog.findViewById(R.id.vfLocation);

    EditText edtSearch = dialog.findViewById(R.id.edtSearch);
    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
    ImageView imgSearch = dialog.findViewById(R.id.imgSearch);

    edtSearch.requestFocus();

    edtSearch.setHint(title);
    txtTitle.setText(title);

    listPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        listener.description(stationModels.get(position).getAddress(), stationModels.get(position).getCode());
//        listener.selectedAddress(true);
        dismiss();
      }
    });

    imgSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (edtSearch.getText().toString().isEmpty()) {
          MyApplication.Toast("لطفا نام منطقه را وارد نمایید", Toast.LENGTH_SHORT);
          return;
        }
        findWay(cityLatin, StringHelper.toEnglishDigits(edtSearch.getText().toString()));
      }
    });

    edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_NEXT) {
          if (edtSearch.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا نام منطقه را وارد نمایید", Toast.LENGTH_SHORT);
            return false;
          }
          findWay(cityLatin, StringHelper.toEnglishDigits(edtSearch.getText().toString()));
          return true;
        } else if (i == EditorInfo.IME_ACTION_DONE) {
          if (edtSearch.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا نام منطقه را وارد نمایید", Toast.LENGTH_SHORT);
            return false;
          }
          findWay(cityLatin, StringHelper.toEnglishDigits(edtSearch.getText().toString()));
          return true;
        }
        return false;
      }
    });

    MyApplication.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        KeyBoardHelper.showKeyboard(MyApplication.context);

      }
    }, 200);

    dialog.show();

  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
      AvaCrashReporter.send(e,"SearchLocationDialog class, dismiss method");
    }
    dialog = null;
  }

  private void findWay(String cityLName, String address) {
    vfLocation.setDisplayedChild(1);

    RequestHelper.builder(EndPoints.FIND_WAY)
            .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
            .addHeader("id_token", MyApplication.prefManager.getIdToken())
            .addPath(cityLName)
            .addPath(address)
            .listener(onFindWay)
            .get();

  }

  RequestHelper.Callback onFindWay = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            stationModels = new ArrayList<>();
            JSONArray arr = new JSONArray(args[0].toString());
            for (int i = 0; i < arr.length(); i++) {
              JSONObject object = arr.getJSONObject(i);
              JSONObject objWay = object.getJSONObject("way");
              JSONObject obgStation = object.getJSONObject("station");
              StationModel stationModel = new StationModel();
              stationModel.setAddress(objWay.getString("name"));
              stationModel.setName(obgStation.getString("stationName"));
              stationModel.setCode(obgStation.getInt("stationCode"));
              stationModel.setCountrySide(obgStation.getInt("countryside"));
              stationModels.add(stationModel);
            }

            if (vfLocation != null)
              vfLocation.setDisplayedChild(2);
            addressAdapter = new StationAdapter(stationModels, MyApplication.context);
            if (listPlace != null)
              listPlace.setAdapter(addressAdapter);

            if (stationModels.size() == 0) {
              if (vfLocation != null)
                vfLocation.setDisplayedChild(0);
            }

          } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e,"SearchLocationDialog class, onFindWay onResponse method");
          }
        }
      });
    }

    @Override
    public void onReloadPress(boolean v) {

      super.onReloadPress(v);
      try {

        if (v)
          vfLocation.setDisplayedChild(1);
        else
          vfLocation.setDisplayedChild(2);

      } catch (Exception e) {
        e.printStackTrace();
        AvaCrashReporter.send(e,"SearchLocationDialog class, onReloadPress onResponse method");
      }
    }

    @Override
    public void onRefreshTokenUpdated(Runnable reCall, boolean isRefreshTokenUpdated) {
      super.onRefreshTokenUpdated(reCall, isRefreshTokenUpdated);
      reCall.run();
    }
  };

//  private ArrayList<StationModel> stations() {
//    stationModels = new ArrayList<>();
//    try {
//      JSONArray arr = new JSONArray(city);
//      for (int i = 0; i < arr.length(); i++) {
//        JSONObject object = arr.getJSONObject(i);
//        StationModel stationModel = new StationModel();
//        stationModel.setAddress(object.getString("address"));
//        stationModel.setCode(object.getString("stationCode"));
//        stationModel.setName(object.getString("stationName"));
//        stationModel.setStatus(object.getInt("status"));
//        stationModels.add(stationModel);
//      }
//      addressAdapter = new StationAdapter(stationModels, MyApplication.context);
//      listPlace.setAdapter(addressAdapter);
//
//      if (stationModels.size() == 0) {
//        vfLocation.setDisplayedChild(0);
//      }
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//
//    return stationModels;
//  }


}
