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
import android.widget.TextView;
import android.widget.Toast;

import org.linphone.core.Call;

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
    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    TextView txtTitle=dialog.findViewById(R.id.txtTitle);

    //TODO  if call is available this layer must be visible
    Call call = LinphoneService.getCore().getCurrentCall();
    if (call == null) {
      llEndCall.setVisibility(View.GONE);
      txtTitle.setText("تماس با پشتیبانی");
    } else {
      llEndCall.setVisibility(View.VISIBLE);
      txtTitle.setText("انتقال تماس");
    }

    llTransferCall.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        TODO complete this part
        Call call = LinphoneService.getCore().getCurrentCall();
        call.transfer("1880");
        MyApplication.Toast("تماس به صف پشتیبانی منتقل شد", Toast.LENGTH_SHORT);
        listener.onClose(true);
        dismiss();
      }
    });

    llEndCall.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        TODO complete this part
        Call call = LinphoneService.getCore().getCurrentCall();
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
