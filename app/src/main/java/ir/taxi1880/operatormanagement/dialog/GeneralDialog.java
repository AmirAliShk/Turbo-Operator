package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/***
 * Created by Amirreza Erfanian on 2018/July/26.
 * v : 1.0.0
 */

public class GeneralDialog {

  private Runnable bodyRunnable = null;
  private Runnable dismissBody = null;
  private ButtonModel firstBtn = null;
  private ButtonModel secondBtn = null;
  private ButtonModel thirdBtn = null;
  private DismissListener listener;
  private Listener descListener;
  private String messageText = "";
  private String titleText = "";
  private int visibility;
  private boolean cancelable = true;
  public static final String ERROR = "error";

  private class ButtonModel {
    String text;
    Runnable body;

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Runnable getBody() {
      return body;
    }

    public void setBody(Runnable body) {
      this.body = body;
    }
  }

  interface DismissListener {
    void onDismiss();
  }

  public interface Listener {
    void onDescription(String message);
  }

  public GeneralDialog onDescriptionListener(Listener listener) {
    this.descListener = listener;
    return this;
  }

  public GeneralDialog messageVisibility(int visible) {
    this.visibility = visible;
    return this;
  }

  public GeneralDialog onDismissListener(DismissListener listener) {
    this.listener = listener;
    return this;
  }

  public GeneralDialog afterDismiss(Runnable dismissBody) {
    this.dismissBody = dismissBody;
    return this;
  }

  public GeneralDialog firstButton(String name, Runnable body) {
    firstBtn = new ButtonModel();
    firstBtn.setBody(body);
    firstBtn.setText(name);
    return this;
  }

  public GeneralDialog secondButton(String name, Runnable body) {
    secondBtn = new ButtonModel();
    secondBtn.setBody(body);
    secondBtn.setText(name);
    return this;
  }

  public GeneralDialog thirdButton(String name, Runnable body) {
    thirdBtn = new ButtonModel();
    thirdBtn.setBody(body);
    thirdBtn.setText(name);
    return this;
  }

  public GeneralDialog bodyRunnable(Runnable bodyRunnable) {
    this.bodyRunnable = bodyRunnable;
    return this;
  }

  public GeneralDialog message(String messageText) {
    this.messageText = messageText;
    return this;
  }

  public GeneralDialog title(String titleText) {
    this.titleText = titleText;
    return this;
  }

  public GeneralDialog cancelable(boolean cancelable) {
    this.cancelable = cancelable;
    return this;
  }

  Unbinder unbinder;

  @BindView(R.id.txtTitle)
  TextView txtTitle;

  @BindView(R.id.llTitle)
  LinearLayout llTitle;

  @BindView(R.id.txtMessage)
  TextView txtMessage;

  @BindView(R.id.llBtnView)
  LinearLayout llBtnView;

  @BindView(R.id.btnFirst)
  Button btnFirst;

  @BindView(R.id.edtMessage)
  EditText edtMessage;

  @BindView(R.id.btnSecond)
  Button btnSecond;

  @BindView(R.id.btnThird)
  Button btnThird;

  @OnClick(R.id.btnFirst)
  void onFirstPress() {
    if (edtMessage.getVisibility() == View.VISIBLE) {
      descListener.onDescription(edtMessage.getText().toString());
    }
    dismiss();
    if (firstBtn != null) {
      if (firstBtn.getBody() != null) {
        firstBtn.getBody().run();
      }
    }
  }

  @OnClick(R.id.btnSecond)
  void onSecondPress() {
    dismiss();

    if (secondBtn != null) {
      if (secondBtn.getBody() != null)
        secondBtn.getBody().run();
    }
  }

  @OnClick(R.id.btnThird)
  void onThirdPress() {
    dismiss();

    if (thirdBtn != null) {
      if (thirdBtn.getBody() != null)
        thirdBtn.getBody().run();
    }
  }

  @BindView(R.id.divider_st)
  ImageView divider_st;

  @BindView(R.id.divider_fs)
  ImageView divider_fs;

  Dialog dialog;

  public void show() {
    if (MyApplication.currentActivity==null|| MyApplication.currentActivity.isFinishing())return;
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_general);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(cancelable);
    unbinder = ButterKnife.bind(this, dialog);
    TypefaceUtil.overrideFonts( dialog.getWindow().getDecorView());

    txtMessage.setText(messageText);
    txtTitle.setText(titleText);
    if (titleText.isEmpty()) {
      txtTitle.setVisibility(View.GONE);
      llTitle.setVisibility(View.GONE);
    }
    if (titleText.isEmpty()) {
      txtTitle.setVisibility(View.GONE);
      txtMessage.setTextSize(20);
    }
    if (messageText.isEmpty()) {
      txtMessage.setVisibility(View.GONE);
    }
    if (firstBtn == null) {
      btnFirst.setVisibility(View.GONE);
    } else {
      btnFirst.setText(firstBtn.getText());
    }
    if (secondBtn == null) {
      btnSecond.setVisibility(View.GONE);
    } else {
      btnSecond.setText(secondBtn.getText());
    }
    if (thirdBtn == null) {
      btnThird.setVisibility(View.GONE);
    } else {
      btnThird.setText(thirdBtn.getText());
    }
    if (thirdBtn == null || secondBtn == null) {
      divider_st.setVisibility(View.GONE);
    }
    if (firstBtn == null || secondBtn == null) {
      divider_fs.setVisibility(View.GONE);
    }
    if (firstBtn == null && secondBtn == null && thirdBtn == null) {
      llBtnView.setVisibility(View.GONE);
    }
    if (visibility == 1) {
      edtMessage.setVisibility(View.VISIBLE);
    } else {
      edtMessage.setVisibility(View.GONE);
    }
    if (bodyRunnable != null)
      bodyRunnable.run();

    dialog.setOnDismissListener(dialog -> {
      if (dismissBody != null)
        dismissBody.run();
    });
    dialog.show();
  }

  // dismiss center control
  public void dismiss() {
    try {
      if (listener != null) {
        listener.onDismiss();
      }
      if (dialog != null)
        dialog.dismiss();
    } catch (Exception e) {
      e.printStackTrace();
    }
    dialog = null;
  }
}
