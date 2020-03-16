package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.services.LinphoneService;

public class CallDialog {

  private static final String TAG = CallDialog.class.getSimpleName();

  public interface Listener {
    void onClose(boolean b);
  }


  static Dialog dialog;
  Listener listener;

  public void show(Listener listener) {
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_call);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;
    LinearLayout llTransferCall = dialog.findViewById(R.id.llTransfer);
    LinearLayout llEndCall = dialog.findViewById(R.id.llEndCall);
    LinearLayout llCallSupport = dialog.findViewById(R.id.llCallSupport);
    LinearLayout llTestConnection = dialog.findViewById(R.id.llTestConnection);
    ViewFlipper vfCall = dialog.findViewById(R.id.vfCall);
    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    TextView txtTitle = dialog.findViewById(R.id.txtTitle);


    //TODO  if call is available this layer must be visible
    Core core = LinphoneService.getCore();
    Call call = core.getCurrentCall();
    vfCall.setDisplayedChild((call == null) ? 0 : 1);

    llCallSupport.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Address addressToCall = core.interpretUrl("950");
        CallParams params = core.createCallParams(null);
        AudioManager mAudioManager = ((AudioManager) LinphoneService.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
        mAudioManager.setSpeakerphoneOn(true);
        params.enableVideo(false);
        if (addressToCall != null) {
          core.inviteAddressWithParams(addressToCall, params);
        }
        listener.onClose(false);
      }
    });

    llTestConnection.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Address addressToCall = core.interpretUrl("998");
        CallParams params = core.createCallParams(null);
        params.enableVideo(false);
        if (addressToCall != null) {
          core.inviteAddressWithParams(addressToCall, params);
        }

        listener.onClose(false);
      }
    });


    llTransferCall.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        call.transfer("950");
        MyApplication.Toast("تماس به صف پشتیبانی منتقل شد", Toast.LENGTH_SHORT);
        listener.onClose(true);
        dismiss();
      }
    });

    llEndCall.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        call.terminate();
        MyApplication.Toast("تماس به اتمام رسید", Toast.LENGTH_SHORT);
        listener.onClose(true);
        dismiss();
      }
    });

    imgClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
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
