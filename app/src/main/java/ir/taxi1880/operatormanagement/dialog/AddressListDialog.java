package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.LastAddressAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogAddressListBinding;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AddressesModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AddressListDialog {

    private static final String TAG = AddressListDialog.class.getSimpleName();

    private static Dialog dialog;
    private DialogAddressListBinding binding;
    private LastAddressAdapter lastAddressAdapter;
    private ArrayList<AddressesModel> passengerAddressModels;
    private Listener listener;
    private String passengerId;


    public interface Listener {
        void description(String address, int stationCode, String addressId);
//    void selectedAddress(boolean b);
    }

    public void show(boolean isFromOrigin, String passengerId, ArrayList<AddressesModel> passengerAddresses, Listener listener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogAddressListBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;
        this.passengerAddressModels = passengerAddresses;
        this.passengerId = passengerId;

        lastAddressAdapter = new LastAddressAdapter(isFromOrigin, passengerId, passengerAddressModels);
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
            listener.description(passengerAddressModels.get(position).getAddress(), passengerAddressModels.get(position).getStation(), passengerAddressModels.get(position).getAddressId());
            dismiss();
        });

        binding.blrView.setOnClickListener(v -> dialog.dismiss());

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
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}