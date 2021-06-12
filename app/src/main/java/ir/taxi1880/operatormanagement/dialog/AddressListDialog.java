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
import android.widget.ViewFlipper;

import com.github.mmin18.widget.RealtimeBlurView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.LastAddressAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AddressListDialog {

    private static final String TAG = AddressListDialog.class.getSimpleName();

    public interface Listener {
        void description(String address, int stationCode, int addressId);

//    void selectedAddress(boolean b);
    }

    private LastAddressAdapter lastAddressAdapter;
    private ListView listLastAddress;
    private ViewFlipper vfLastAddress;
    RealtimeBlurView blrView;

    private Listener listener;
    private static Dialog dialog;

    public void show(boolean isFromOrigin, Listener listener, ArrayList<PassengerAddressModel> passengerAddressModels) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_address_list);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        listLastAddress = dialog.findViewById(R.id.listLastAddress);
        vfLastAddress = dialog.findViewById(R.id.vfLastAddress);
        blrView = dialog.findViewById(R.id.blrView);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        lastAddressAdapter = new LastAddressAdapter(isFromOrigin, passengerAddressModels, MyApplication.context);
        listLastAddress.setAdapter(lastAddressAdapter);

        if (isFromOrigin) {
            txtTitle.setText("آدرس های مبدا");
        } else {
            txtTitle.setText("آدرس های مقصد");
        }

        blrView.setOnClickListener(view -> dismiss());

        listLastAddress.setOnItemClickListener((parent, view, position, id) -> {
            listener.description(passengerAddressModels.get(position).getAddress(), passengerAddressModels.get(position).getStation(),passengerAddressModels.get(position).getAddressId());
//        listener.selectedAddress(true);
            dismiss();
        });

        if (passengerAddressModels.size() == 0) {
            vfLastAddress.setDisplayedChild(1);
        }

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
            AvaCrashReporter.send(e, "AddressListDialog class, dismiss method");
        }
        dialog = null;
    }

}
