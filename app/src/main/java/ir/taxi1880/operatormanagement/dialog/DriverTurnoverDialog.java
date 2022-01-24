package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.DriverTurnoverAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DriverTurnoverDialog {

    public static final String TAG = DriverTurnoverDialog.class.getSimpleName();
    Dialog dialog;
    Unbinder unbinder;

    ArrayList<DriverTurnoverModel> driverTurnoverModels;
    DriverTurnoverAdapter adapter;

    @BindView(R.id.vfFinancial)
    ViewFlipper vfFinancial;

    @BindView(R.id.listDriverTurnover)
    ListView listDriverTurnover;

    @OnClick(R.id.imgClose)
    void onPressClose() {
        dismiss();
    }

    public void show(JSONArray data) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_driver_turnover);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        fillList(data);

        dialog.show();
    }

    void fillList(JSONArray dataArr) {
        driverTurnoverModels = new ArrayList<>();
        try {
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                DriverTurnoverModel model = new DriverTurnoverModel();
                model.setDate(dataObj.getString("date"));
                model.setTime(dataObj.getString("time"));
                model.setDescription(dataObj.getString("sharh"));
                model.setDebit(dataObj.getString("debit"));
                model.setCredit(dataObj.getString("credit"));
                driverTurnoverModels.add(model);
            }

            if (driverTurnoverModels.size() == 0) {
                if (vfFinancial != null)
                    vfFinancial.setDisplayedChild(1);
            } else {
                if (vfFinancial != null)
                    vfFinancial.setDisplayedChild(0);
                adapter = new DriverTurnoverAdapter(driverTurnoverModels);
                listDriverTurnover.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, fillList method");
        }
    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
        unbinder.unbind();
    }
}