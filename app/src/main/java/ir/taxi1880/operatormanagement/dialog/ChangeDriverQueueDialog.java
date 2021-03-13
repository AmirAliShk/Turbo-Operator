package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class ChangeDriverQueueDialog {
    Dialog dialog;
    Unbinder unbinder;

    @OnClick(R.id.imgClose)
    void onClosePress() {
        dismiss();
    }

    @BindView(R.id.imgClose)
    ImageView imgClose;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_change_driver_queue);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        unbinder = ButterKnife.bind(this, dialog);
        dialog.setCancelable(false);

        dialog.show();
    }

    private void setQueue() {
        RequestHelper.builder(EndPoints.DRIVER_STATION_POSITION)
                .ignore422Error(true)
                .addParam("driverCode", "")
                .addParam("position", "")
                .listener(onChangeQueue)
                .put();
    }

    private RequestHelper.Callback onChangeQueue = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {

        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
        }
    };

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            e.getMessage();
        }

        dialog = null;
    }

}
