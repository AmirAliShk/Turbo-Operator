package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.NotificationModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class NotificationAdapter extends BaseAdapter {

    ArrayList<NotificationModel> notificationModels;
    LayoutInflater layoutInflater;
    int notifId;

    public NotificationAdapter(ArrayList<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
        this.layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
    }

    @Override
    public int getCount() {
        return notificationModels.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        try {
            NotificationModel notificationModel = notificationModels.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_notification, null, false);
                TypefaceUtil.overrideFonts(convertView);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Animation anim = AnimationUtils.loadAnimation(MyApplication.context, R.anim.shake_infinati);
            viewHolder.imgNotify.startAnimation(anim);

            if (notificationModel.getSeen() == 0) {
                viewHolder.btnSeenNotify.setVisibility(View.VISIBLE);
            } else {
                viewHolder.btnSeenNotify.setVisibility(View.GONE);
            }

            viewHolder.btnSeenNotify.setOnClickListener(v -> {
                notifId = notificationModel.getId();
                setNewsSeen(notifId);
                notificationModel.setSeen(1);
            });

            viewHolder.txtNotification.setText(notificationModel.getText());
            viewHolder.txtDate.setText(StringHelper.toPersianDigits(notificationModel.getSendDate()));

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "NotificationAdapter class, getView method");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtNotification;
        TextView txtDate;
        ImageView imgNotify;
        Button btnSeenNotify;

        public ViewHolder() {
        }

        ViewHolder(View convertView) {
            txtNotification = convertView.findViewById(R.id.txtNotification);
            imgNotify = convertView.findViewById(R.id.imgNotify);
            btnSeenNotify = convertView.findViewById(R.id.btnSeenNotify);
            txtDate = convertView.findViewById(R.id.txtDate);
        }
    }

    private void setNewsSeen(int newsId) {
        RequestHelper.builder(EndPoints.SET_NEWS_SEEN)
                .addParam("newsId", newsId)
                .listener(onSetNewsSeen)
                .post();
    }

    private RequestHelper.Callback onSetNewsSeen = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    if (status == 1) {
                        if (MyApplication.prefManager.getCountNotification() > 0)
                            MyApplication.prefManager.setCountNotification(MyApplication.prefManager.getCountNotification() - 1);
                        notifyDataSetChanged();
                    } else {
                        new ErrorDialog()
                                .titleText("خطایی رخ داده")
                                .messageText("پردازش داده های ورودی با مشکل مواجه گردید")
                                .tryAgainBtnRunnable("تلاش مجدد", null)
                                .closeBtnRunnable("بستن", () -> MyApplication.currentActivity.onBackPressed())
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "NotificationAdapter class, onSetNewsSeen onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
        }
    };
}
