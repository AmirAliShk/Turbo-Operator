package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ReplacementModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ReplacementWaitingAdapter extends BaseAdapter {

    private ArrayList<ReplacementModel> replacementModels;
    private LayoutInflater layoutInflater;
    int positionn;
    int answer = -1;
    int replaceId = 0;

    public interface Listener {
        void onRemoveItem(int position);
    }

    Listener listener;

    public ReplacementWaitingAdapter(ArrayList<ReplacementModel> replacementModels, Context context, Listener listener) {
        this.replacementModels = replacementModels;
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return replacementModels.size();
    }

    @Override
    public Object getItem(int position) {
        return replacementModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = convertView;
        try {
            final ReplacementModel replacementModel = replacementModels.get(position);
            if (myView == null) {
                myView = layoutInflater.inflate(R.layout.item_wait_replacement, null);
                TypefaceUtil.overrideFonts(myView);
            }
            positionn = position;
            TextView txtDate = myView.findViewById(R.id.txtDate);
            TextView txtShift = myView.findViewById(R.id.txtShift);
            TextView txtOperator = myView.findViewById(R.id.txtOperator);
            LinearLayout llConfirm = myView.findViewById(R.id.llConfirm);
            LinearLayout llReject = myView.findViewById(R.id.llReject);

            llConfirm.setOnClickListener(v -> {
                new GeneralDialog()
                        .title("هشدار")
                        .message("آیا از انجام عملیات فوق اطمینان دارید؟")
                        .cancelable(false)
                        .firstButton("بله", () -> {
                            answer = 1;
                            replaceId = replacementModel.getReplaceId();
                            answerShiftReplacementRequest();
                            notifyDataSetChanged();
                        }).secondButton("خیر", null)
                        .show();
            });

            llReject.setOnClickListener(v -> {
                new GeneralDialog()
                        .title("هشدار")
                        .message("آیا از انجام عملیات فوق اطمینان دارید؟")
                        .cancelable(false)
                        .firstButton("بله", () -> {
                            answer = 0;
                            replaceId = replacementModel.getReplaceId();
                            answerShiftReplacementRequest();
                            notifyDataSetChanged();
                        }).secondButton("خیر", null)
                        .show();
            });

            txtDate.setText(replacementModel.getReplaceDate());
            txtShift.setText(replacementModel.getReplaceShiftName());
            txtOperator.setText(replacementModel.getReplaceOperatorName());

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "ReplacementWaitingAdapter class, getView method");
        }
        return myView;
    }

    private void answerShiftReplacementRequest() {
        RequestHelper.builder(EndPoints.ANSWER_SHIFT_REPLACEMENT_REQUEST)
                .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
                .addHeader("id_token", MyApplication.prefManager.getIdToken())
                .addParam("operatorId", MyApplication.prefManager.getUserCode())
                .addParam("replacementRequestId", replaceId)
                .addParam("answer", answer)
                .listener(onAnswerShiftReplacementRequest)
                .post();
    }

    RequestHelper.Callback onAnswerShiftReplacementRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    if (status == 1) {
                        MyApplication.Toast("عملیات با موفقیت انجام شد", Toast.LENGTH_SHORT);
                        listener.onRemoveItem(positionn);
                        if (MyApplication.prefManager.getCountRequest() > 0) {
                            MyApplication.prefManager.setCountRequest(MyApplication.prefManager.getCountRequest() - 1);
                        }
                    } else {
                        new ErrorDialog()
                                .titleText("خطایی رخ داده")
                                .messageText("پردازش داده های ورودی با مشکل مواجه گردید")
                                .tryAgainBtnRunnable("تلاش مجدد", () -> MyApplication.currentActivity.onBackPressed())
                                .closeBtnRunnable("بستن", () -> MyApplication.currentActivity.onBackPressed())
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "ReplacementWaitingAdapter class, onAnswerShiftReplacementRequest onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }
    };
}
