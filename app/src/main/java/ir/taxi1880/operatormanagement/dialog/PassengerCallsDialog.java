package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.PassengerCallsAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerCallsModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PassengerCallsDialog {

    Dialog dialog;
    Unbinder unbinder;

    @BindView(R.id.listPassengerCalls)
    ListView listPassengerCalls;

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    PassengerCallsAdapter mAdapter;
    ArrayList<PassengerCallsModel> passengerCallsModels;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_passenger_calls);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        passengerCallsModels = new ArrayList<>();

        passengerCallsModels.add(new PassengerCallsModel("99/12/24", "12:23", "00:30"));
        passengerCallsModels.add(new PassengerCallsModel("99/12/24", "12:23", "00:30"));
        passengerCallsModels.add(new PassengerCallsModel("99/12/24", "12:23", "00:30"));

        mAdapter = new PassengerCallsAdapter(MyApplication.currentActivity, passengerCallsModels);
        listPassengerCalls.setAdapter(mAdapter);

        dialog.show();
    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("TAG", "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "ReserveDialog class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}
