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
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.StationInfoAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationInfoModel;

public class StationInfoDialog {

  private static final String TAG = StationInfoDialog.class.getSimpleName();

  public interface Listener {
    void description(String address, int stationCode);

//    void selectedAddress(boolean b);
  }

  private StationInfoAdapter stationInfoAdapter;
  private ListView listStationInfo;

  private Listener listener;
  private static Dialog dialog;

  public void show(ArrayList<StationInfoModel> stationInfoModels,String title,boolean isCountrySide) {
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
    TextView txtTitle=dialog.findViewById(R.id.txtTitle);
    TextView txtCountrySide=dialog.findViewById(R.id.txtCountrySide);

    txtTitle.setText(title);

    if (isCountrySide){
      txtCountrySide.setVisibility(View.VISIBLE);
    }else {
      txtCountrySide.setVisibility(View.GONE);
    }

    stationInfoAdapter = new StationInfoAdapter(stationInfoModels, MyApplication.context);
    listStationInfo.setAdapter(stationInfoAdapter);

    listStationInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
      }
    });

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
    }
    dialog = null;
  }

}
