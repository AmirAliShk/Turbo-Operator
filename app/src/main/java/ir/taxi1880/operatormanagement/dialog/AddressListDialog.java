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
import android.widget.ViewFlipper;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.LastAddressAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.LastAddressModel;

public class AddressListDialog {

  private static final String TAG = AddressListDialog.class.getSimpleName();

  public interface Listener {
    void description(String address);

//    void selectedAddress(boolean b);
  }

  private LastAddressAdapter lastAddressAdapter;
  private ListView listLastAddress;
  private ViewFlipper vfLastAddress;

  private Listener listener;
  private static Dialog dialog;

  public void show(Listener listener,ArrayList<LastAddressModel> lastAddressModels) {
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_address_list);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    listLastAddress = dialog.findViewById(R.id.listLastAddress);
    vfLastAddress=dialog.findViewById(R.id.vfLastAddress);

    listLastAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.description(lastAddressModels.get(position).getAddress());
//        listener.selectedAddress(true);
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

  private String city = "[{\"address\":\"دلاوران18\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"19\",\"status\":\"0\"}," +
          "{\"address\":\"سیدرضی15\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"20\",\"status\":\"1\"}," +
          "{\"address\":\"فدائیان اسلام17\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"15\",\"status\":\"0\"}," +
          "{\"address\":\"هاشمیه10\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"10\",\"status\":\"1\"}," +
          "{\"address\":\"دلاوران5\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"78\",\"status\":\"0\"}," +
          "{\"address\":\"حر17\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"100\",\"status\":\"1\"}," +
          "{\"address\":\"سیدرضی15\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"20\",\"status\":\"0\"}," +
          "{\"address\":\"فدائیان اسلام17\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"15\",\"status\":\"0\"}," +
          "{\"address\":\"هاشمیه10\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"10\",\"status\":\"1\"}," +
          "{\"address\":\"دلاوران5\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"78\",\"status\":\"0\"}," +
          "{\"address\":\"حر17\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"100\",\"status\":\"1\"}," +
          "{\"address\":\"پیروزی6\",\"stationName\":\"وکیل آباداز سیدرضی تا دانشجو-دانشجو فرد تا معلم\",\"stationCode\":\"10\",\"status\":\"0\"}]";


//  private void getPassengerAddress(String phoneNumber) {
//    RequestHelper.builder(EndPoints.PASSENGER_ADDRESS + "/" + phoneNumber)
//            .method(RequestHelper.GET)
//            .listener(getPassengerAddress)
//            .request();
//
//  }
//
//  RequestHelper.Callback getPassengerAddress = new RequestHelper.Callback() {
//    @Override
//    public void onResponse(Runnable reCall, Object... args) {
//      MyApplication.handler.post(new Runnable() {
//        @Override
//        public void run() {
//          try {
//            stationModels=new ArrayList<>();
//            JSONArray arr = new JSONArray(args[0].toString());
//            for (int i = 0; i < arr.length(); i++) {
//              JSONObject object = arr.getJSONObject(i);
//              JSONObject objWay = object.getJSONObject("way");
//              JSONObject obgStation = object.getJSONObject("station");
//              StationModel stationModel = new StationModel();
//              stationModel.setAddress(objWay.getString("name"));
//              stationModel.setName(obgStation.getString("stationName"));
//              stationModel.setCode(obgStation.getString("stationCode"));
//              stationModel.setCountrySide(obgStation.getInt("countryside"));
//              stationModels.add(stationModel);
//            }
//
//            vfLocation.setDisplayedChild(2);
//            addressAdapter = new StationAdapter(stationModels, MyApplication.context);
//            listPlace.setAdapter(addressAdapter);
//
//            if (stationModels.size() == 0) {
//              vfLocation.setDisplayedChild(0);
//            }
//
//          } catch (JSONException e) {
//            e.printStackTrace();
//          }
//        }
//      });
//    }
//
//    @Override
//    public void onFailure(Runnable reCall, Exception e) {
//
//    }
//  };
//
//  private ArrayList<LastAddressModel> address() {
//    lastAddressModels = new ArrayList<>();
//    try {
//      JSONArray arr = new JSONArray(city);
//      for (int i = 0; i < arr.length(); i++) {
//        JSONObject object = arr.getJSONObject(i);
//        LastAddressModel stationModel = new LastAddressModel();
//        stationModel.setAddress(object.getString("address"));
//        stationModel.setCode(object.getString("stationCode"));
//        lastAddressModels.add(stationModel);
//      }
//      lastAddressAdapter = new LastAddressAdapter(lastAddressModels, MyApplication.context);
//      listLastAddress.setAdapter(lastAddressAdapter);
//
//      if (lastAddressModels.size()==0){
//        vfLastAddress.setDisplayedChild(0);
//      }
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//
//    return lastAddressModels;
//  }
//

}
