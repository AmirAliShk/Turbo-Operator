package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.NotificationModel;

public class NotificationAdapter extends BaseAdapter {

    ArrayList<NotificationModel> notificationModels ;
    LayoutInflater layoutInflater;

    public NotificationAdapter(ArrayList<NotificationModel> notificationModels, Context context) {
        this.notificationModels = notificationModels;
        this.layoutInflater = LayoutInflater.from(context);
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
            final NotificationModel notificationModel = notificationModels.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_notification, parent,false);
                TypefaceUtil.overrideFonts(convertView);
                viewHolder=new ViewHolder(convertView);
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
                setNewsSeen(notificationModel.getId());
                notificationModel.setSeen(1);
            });

            viewHolder.txtNotification.setText(notificationModel.getText());
            viewHolder.txtDate.setText(notificationModel.getSendDate());

        } catch (Exception e) {
            e.printStackTrace();
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
            txtDate=convertView.findViewById(R.id.txtDate);
        }
    }

    private void setNewsSeen(int newsId) {
        JSONObject params = new JSONObject();
        try {
            params.put("newsId", newsId);

            RequestHelper.builder(EndPoints.SET_NEWS_SEEN)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onSetNewsSeen)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RequestHelper.Callback onSetNewsSeen = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    if (status == 1) {
//                        notificationModels.get(0).setSeen(1);
                        if (MyApplication.prefManager.getCountNotification() > 0)
                            MyApplication.prefManager.setCountNotification(MyApplication.prefManager.getCountNotification() - 1);
                        notifyDataSetChanged();
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
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };
}
