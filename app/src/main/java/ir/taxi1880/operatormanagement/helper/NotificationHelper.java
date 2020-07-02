package ir.taxi1880.operatormanagement.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;

public class NotificationHelper {

    private String channelName = "turboTaxi";
    private String activityName = "";
    private String channelId = "";
    private String message = "";
    private int collapsedView = 0;
    private int expandedView = 0;
    private int textView = 0;
    private int smallIcon = 0;
    private int notificationId = 0;
    private int clickableView = 0;
    private Boolean autoCancel = false;
    private NotificationManager notificationManager;

    public NotificationHelper channelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public NotificationHelper channelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public NotificationHelper notificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public NotificationHelper collapsedView(int collapsedView) {
        this.collapsedView = collapsedView;
        return this;
    }


    public NotificationHelper expandedView(int expandedView) {
        this.expandedView = expandedView;
        return this;
    }

    public NotificationHelper smallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public NotificationHelper clickableView(int clickableView) {
        this.clickableView = clickableView;
        return this;
    }

    public NotificationHelper startActivity(String activityName) {
        this.activityName = activityName;
        return this;
    }

    public NotificationHelper autoCancel(Boolean autoCancel) {
        this.autoCancel = autoCancel;
        return this;
    }

    public NotificationHelper notificationMessage(int textViewId, String message) {
        this.textView = textViewId;
        this.message = message;
        return this;
    }

    public void show() {

        RemoteViews collapse = new RemoteViews(MyApplication.currentActivity.getPackageName(), collapsedView);

        if (expandedView != 0) {
            int expandedLayout = expandedView;
            RemoteViews expandedView = new RemoteViews(MyApplication.currentActivity.getPackageName(), expandedLayout);
        }

        if (message != null && textView != 0){
            collapse.setTextViewText(textView, message);
        }

        if (activityName != null) {
            Intent intent = new Intent(MyApplication.context, activityName.getClass());
            collapse.setOnClickPendingIntent(clickableView, PendingIntent.getActivity(MyApplication.context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.context.getApplicationContext(), channelId)
                /*TODO: small icon shoudnt be null*/
                .setSmallIcon(smallIcon)
//                .setCustomContentView(collapsedView)
                .setAutoCancel(autoCancel)
//                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        notificationManager = (NotificationManager) MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelName, "lkjhgf", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    public void cancel(int cancelNotificationId){

        notificationManager.cancel(cancelNotificationId);
    }

}
