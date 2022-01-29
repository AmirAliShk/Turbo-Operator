package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogGeneralBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class GeneralDialog {

    public static final String TAG = GeneralDialog.class.getSimpleName();
    DialogGeneralBinding binding;
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
    private boolean singleInstance = false;
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

    public GeneralDialog isSingleMode(boolean singleInstance) {
        this.singleInstance = singleInstance;
        return this;
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

    private Dialog dialog;
    private Dialog staticDialog = null;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        Dialog tempDialog = null;
        if (singleInstance) {
            if (staticDialog != null) {
                staticDialog.dismiss();
                staticDialog = null;
            }
            staticDialog = new Dialog(MyApplication.currentActivity);
            tempDialog = staticDialog;
        } else {
            dialog = new Dialog(MyApplication.currentActivity);
            tempDialog = dialog;
        }
        tempDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = DialogGeneralBinding.inflate(LayoutInflater.from(dialog.getContext()));
        tempDialog.setContentView(binding.getRoot());
        tempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = tempDialog.getWindow().getAttributes();
        tempDialog.getWindow().setAttributes(wlp);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempDialog.setCancelable(cancelable);
        TypefaceUtil.overrideFonts(tempDialog.getWindow().getDecorView());

        binding.txtMessage.setText(messageText);
        binding.txtTitle.setText(titleText);
        if (titleText.isEmpty()) {
            binding.txtTitle.setVisibility(View.GONE);
            binding.llTitle.setVisibility(View.GONE);
        }
        if (titleText.isEmpty()) {
            binding.txtTitle.setVisibility(View.GONE);
            binding.txtMessage.setTextSize(20);
        }
        if (messageText.isEmpty()) {
            binding.txtMessage.setVisibility(View.GONE);
        }
        if (firstBtn == null) {
            binding.btnFirst.setVisibility(View.GONE);
        } else {
            binding.btnFirst.setText(firstBtn.getText());
        }
        if (secondBtn == null) {
            binding.btnSecond.setVisibility(View.GONE);
            binding.imgSpace.setVisibility(View.GONE);
        } else {
            binding.btnSecond.setText(secondBtn.getText());
            binding.imgSpace.setVisibility(View.VISIBLE);
        }
        if (thirdBtn == null) {
            binding.btnThird.setVisibility(View.GONE);
        } else {
            binding.btnThird.setText(thirdBtn.getText());
        }

        if (firstBtn == null && secondBtn == null && thirdBtn == null) {
            binding.llBtnView.setVisibility(View.GONE);
        }
        if (visibility == 1) {
            binding.edtMessage.setVisibility(View.VISIBLE);
        } else {
            binding.edtMessage.setVisibility(View.GONE);
        }
        if (bodyRunnable != null)
            bodyRunnable.run();

        binding.btnFirst.setOnClickListener(view -> {
            dismiss();
            if (binding.edtMessage.getVisibility() == View.VISIBLE) {
                descListener.onDescription(binding.edtMessage.getText().toString());
            }
            if (firstBtn != null) {
                if (firstBtn.getBody() != null) {
                    firstBtn.getBody().run();
                }
            }
        });

        binding.btnSecond.setOnClickListener(view -> {
            dismiss();
            if (secondBtn != null) {
                if (secondBtn.getBody() != null)
                    secondBtn.getBody().run();
            }
        });

        binding.btnThird.setOnClickListener(view -> {
            dismiss();
            if (thirdBtn != null) {
                if (thirdBtn.getBody() != null)
                    thirdBtn.getBody().run();
            }
        });

        tempDialog.setOnDismissListener(dialog -> {
            if (dismissBody != null)
                dismissBody.run();
        });
        tempDialog.show();
    }

    // dismiss center control
    public void dismiss() {
        try {
            if (listener != null) {
                listener.onDismiss();
            }
            if (singleInstance) {
                if (staticDialog != null) {
                    staticDialog.dismiss();
                    staticDialog = null;
                }
            } else {
                if (dialog != null)
                    if (dialog.isShowing())
                        dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}