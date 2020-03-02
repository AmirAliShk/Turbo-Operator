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
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;

public class AddressListDialog {

  private static final String TAG = AddressListDialog.class.getSimpleName();

  public interface Listener {
    void description(String address,int stationCode);

//    void selectedAddress(boolean b);
  }

  private LastAddressAdapter lastAddressAdapter;
  private ListView listLastAddress;
  private ViewFlipper vfLastAddress;

  private Listener listener;
  private static Dialog dialog;

  public void show(Listener listener,ArrayList<PassengerAddressModel> passengerAddressModels) {
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

    lastAddressAdapter=new LastAddressAdapter(passengerAddressModels,MyApplication.context);
    listLastAddress.setAdapter(lastAddressAdapter);

    listLastAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.description(passengerAddressModels.get(position).getAddress(),passengerAddressModels.get(position).getStation());
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

}
