package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.LastAddressAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogAddressListBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AddressesModel;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AddressListDialog {

    private static final String TAG = AddressListDialog.class.getSimpleName();

    public interface Listener {
        void description(String address, int stationCode, String addressId);
//    void selectedAddress(boolean b);
    }
    private DialogAddressListBinding binding;
    private LastAddressAdapter lastAddressAdapter;
    private ArrayList <AddressesModel> passengerAddressModels;

    private Listener listener;
    private static Dialog dialog;

    public void setPassengerAddresses(ArrayList <AddressesModel>passengerAddresses)
    {
        passengerAddressModels = new ArrayList<>();
        this.passengerAddressModels = passengerAddresses;
    }

    public void show(boolean isFromOrigin, Listener listener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogAddressListBinding.inflate(LayoutInflater.from(MyApplication.context));
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

        lastAddressAdapter = new LastAddressAdapter(isFromOrigin, passengerAddressModels);
        binding.listLastAddress.setAdapter(lastAddressAdapter);

        if (isFromOrigin) {
            binding.txtTitle.setText("آدرس های مبدا");
        } else {
            binding.txtTitle.setText("آدرس های مقصد");
        }

        if (passengerAddressModels.size() == 0) {
            binding.vfLastAddress.setDisplayedChild(1);
        }

        binding.listLastAddress.setOnItemClickListener((parent, view, position, id) -> {
//            listener .description(passengerAddressModels.get(position).getAddress(), passengerAddressModels.get(position).getStation(),passengerAddressModels.get(position).getAddressId());
////        listener.selectedAddress(true);
            dismiss();
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
            AvaCrashReporter.send(e, "AddressListDialog class, dismiss method");
        }
        dialog = null;
    }

}
