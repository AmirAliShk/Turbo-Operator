package ir.taxi1880.operatormanagement.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SearchLocationDialog {

  private static final String TAG = SearchLocationDialog.class.getSimpleName();

  public interface Listener {
    void description(String description);
  }

  private Listener listener;

 static InputMethodManager inputMethodManager;
  static Dialog dialog;

  public void show(Listener listener, String title) {
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_search_location);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    inputMethodManager = (InputMethodManager) MyApplication.currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);


    openKeyBoaredAuto();

    EditText edtSearchPlace = dialog.findViewById(R.id.edtSearchPlace);
    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
    ListView listPlace = dialog.findViewById(R.id.listPlace);

    edtSearchPlace.requestFocus();

    edtSearchPlace.setHint(title);
    txtTitle.setText(title);

    txtTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.description("ttt");
        dismiss();
      }
    });

    dialog.show();

  }

  private void openKeyBoaredAuto() {

    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private static void hideKeyboard(Activity activity) { View view = activity.getCurrentFocus();
    if (view == null) {
      view = new View(activity);
    }
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
    }
    dialog = null;
    hideKeyboard(MyApplication.currentActivity);
  }

}
