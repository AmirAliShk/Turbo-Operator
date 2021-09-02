package ir.taxi1880.operatormanagement.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.push.Keys;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_MESSAGE_USER_STATUS;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_REFRESH_USER_STATUS;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_USER_STATUS;

public class PushReceiver extends BroadcastReceiver {
    public static final String TAG = PushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String result = bundle.getString(Keys.KEY_MESSAGE);
            int type = bundle.getInt(Keys.KEY_BROADCAST_TYPE);

            JSONObject object = new JSONObject(result);
            String strMessage = object.getString("message");
            Log.i(TAG, "onReceive: " + strMessage);

            JSONObject messages = new JSONObject(strMessage);
            String typee = messages.getString("type");
//            {"type":"callerInfo","exten":"423","participant":"05138581352","queue":"1880","voipId":"1630394815.772460"}
//            {"messageId":37861454,"message":"{\"type\":\"callerInfo\",\"exten\":\"423\",\"participant\":\"09155598659\",\"queue\":\"1880\",\"voipId\":\"1630487041.856199\"}","projectId":5,"userId":123}
            if (typee.equals("callerInfo")) {
                int exten = messages.getInt("exten");
                String participant = messages.getString("participant");
                String queue = messages.getString("queue");
                String voipId = messages.getString("voipId");

                MyApplication.prefManager.setVoipId(voipId);
                MyApplication.prefManager.setQueue(queue);

//                {"status":true,"pushMessags":[{"messageId":37894239,"message":"{\"type\":\"userStatus\",\"status\":false,\"message\":\"اپراتور کد 123 شما به دلیل جواب ندادن تلفن از صف پاسخگویی خارج شدید  توربو تاکسی \"}","projectId":5,"userId":123}]}
            } else if (typee.equals("userStatus")) {
                boolean status = messages.getBoolean("status");
                String message = messages.getString("message");

                if (status)
                    MyApplication.prefManager.setActivateStatus(true);
                else
                    MyApplication.prefManager.setActivateStatus(false);

                if (MyApplication.prefManager.isAppRun()) {
                    new GeneralDialog()
                            .title("هشدار")
                            .message(message)
                            .cancelable(false)
                            .firstButton("باشه", null)
                            .isSingleMode(true)
                            .show();
                } else {
                    createUserStatusNotification(MyApplication.context, message);
                }

                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                Intent broadcastIntent = new Intent(KEY_REFRESH_USER_STATUS);
                broadcastIntent.putExtra(KEY_USER_STATUS, status);
                broadcastIntent.putExtra(KEY_MESSAGE_USER_STATUS, message);
                broadcaster.sendBroadcast(broadcastIntent);

            } else if (typee.equals("message")) {
                String value = messages.getString("value");
                String messageType = messages.getString("messageType");

                String title = "پیام";
                if (messageType.equals("message"))
                    title = "پیام";
                else if (messageType.equals("announcement"))
                    title = "اطلاعیه";

                if (MyApplication.prefManager.isAppRun()) {
                    new GeneralDialog()
                            .title(title)
                            .message(value)
                            .cancelable(false)
                            .firstButton("باشه", null)
                            .isSingleMode(true)
                            .show();
                } else {
                    createMessageNotification(MyApplication.context, messageType, value);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createMessageNotification(Context context, String type, String value) {

        NotificationManager mNotificationManager;
        String CHANNEL = "pushChannel";
        Intent intent = new Intent(context, SplashActivity.class);
        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);

        if (type.equals(Constant.PUSH_NOTIFICATION_MESSAGE_TYPE)) {
            DataHolder.getInstance().setPushType(Constant.PUSH_NOTIFICATION_MESSAGE_TYPE);
        } else if (type.equals(Constant.PUSH_NOTIFICATION_ANNOUNCEMENT_TYPE)) {
            DataHolder.getInstance().setPushType(Constant.PUSH_NOTIFICATION_ANNOUNCEMENT_TYPE);
        }

        collapsedView.setTextViewText(R.id.txtValue, value);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constant.PUSH_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        collapsedView.setOnClickPendingIntent(R.id.linearNotif, pendingIntent);
//    {"type":"userStatus","status":false,"message":"افتادی بیرون :))"}
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "push")
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContent(collapsedView)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(value))
                .setContentText(value)
                .setOngoing(false)
                .setVibrate(new long[]{200, 200})
                .setAutoCancel(false)
                .setSound(Uri.parse(MyApplication.SOUND + R.raw.short_notification))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "turboTaxi", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(CHANNEL);
        }

        mNotificationManager.notify(Constant.PUSH_NOTIFICATION_ID, mBuilder.build());

    }

    public void createUserStatusNotification(Context context, String value) {

        NotificationManager mNotificationManager;
        String CHANNEL = "pushStatusChannel";
        Intent intent = new Intent(context, SplashActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constant.USER_STATUS_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "statusPush")
                .setSmallIcon(R.drawable.ic_baseline_remove_from_queue_24)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(value))
                .setContentText(value)
                .setOngoing(false)
                .setVibrate(new long[]{200, 200})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse(MyApplication.SOUND + R.raw.short_notification));
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "turboTaxi_userStatus", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(CHANNEL);
        }

        mNotificationManager.notify(Constant.USER_STATUS_NOTIFICATION_ID, mBuilder.build());

    }

}