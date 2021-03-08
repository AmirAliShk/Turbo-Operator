package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.OperatorRecentCallsAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.OperatorRecentCallsModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class OperatorRecentCallsDialog {

    Dialog dialog;
    Unbinder unbinder;

    @BindView(R.id.listOperatorCalls)
    RecyclerView listOperatorCalls;

    @OnClick(R.id.imgClose)
    void onClose() {
        dismiss();
    }

    @BindView(R.id.vfDownload)
    ViewFlipper vfDownload;

    @BindView(R.id.progressDownload)
    ProgressBar progressDownload;

    @BindView(R.id.textProgress)
    TextView textProgress;

    OperatorRecentCallsAdapter mAdapter;
    ArrayList<OperatorRecentCallsModel> operatorCallsModels;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_operator_recent_calls);
        unbinder = ButterKnife.bind(this, dialog.getWindow().getDecorView());
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView(), MyApplication.IraSanSMedume);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(false);

        operatorCallsModels = new ArrayList<>();

        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));
        operatorCallsModels.add(new OperatorRecentCallsModel("99/12/24", "12:23","9351144009" ,"00:30"));

        mAdapter = new OperatorRecentCallsAdapter(MyApplication.currentActivity, operatorCallsModels);
        listOperatorCalls.setAdapter(mAdapter);

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
