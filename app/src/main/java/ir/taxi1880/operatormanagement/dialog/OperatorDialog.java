package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.OperatorAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.OperatorModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class OperatorDialog {

    private static final String TAG = OperatorDialog.class.getSimpleName();

    public interface Listener {
        void selected(OperatorModel op);
    }

    private Listener listener;

    private ArrayList<OperatorModel> operatorModels;
    private OperatorAdapter operatorAdapter;
    static Dialog dialog;
    ListView listOperator;
    View blrView;

    String opName;

    public void show(Listener listener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_operator);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);
        this.listener = listener;

        EditText edtSearchOperator = dialog.findViewById(R.id.edtSearchOperator);
        listOperator = dialog.findViewById(R.id.listOperator);
        blrView = dialog.findViewById(R.id.blrView);
        operatorModels = getOperatorList();

        blrView.setOnClickListener(view -> dismiss());

        listOperator.setOnItemClickListener((parent, view, position, id) -> {
            OperatorModel op = operatorAdapter.getOperator(position);
            listener.selected(op);
            KeyBoardHelper.hideKeyboard();
            dismiss();
        });

        edtSearchOperator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                operatorAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
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
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }

    private ArrayList<OperatorModel> getOperatorList() {
        operatorModels = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(MyApplication.prefManager.getOperatorList());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                OperatorModel operatorModel = new OperatorModel();
                operatorModel.setOperatorId(object.getInt("id"));
                operatorModel.setOperatorName(object.getString("name"));
                operatorModel.setOperatorShift(object.getString("shift"));
                operatorModels.add(operatorModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, getOperatorList method");
        }
        operatorAdapter = new OperatorAdapter(operatorModels);
        listOperator.setAdapter(operatorAdapter);
        return operatorModels;
    }
}