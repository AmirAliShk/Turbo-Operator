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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.push.Keys;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_MESSAGE_USER_STATUS;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_REFRESH_USER_STATUS;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_USER_STATUS;

public class PushReceiver extends BroadcastReceiver {
  public static final String TAG = PushReceiver.class.getSimpleName();

  String res;

  @Override
  public void onReceive(Context context, Intent intent) {
    try {

      Bundle bundle = intent.getExtras();
      String result = bundle.getString(Keys.KEY_MESSAGE);
      res = result;
      int type = bundle.getInt(Keys.KEY_BROADCAST_TYPE);

      JSONObject object = new JSONObject(result);
      String strMessage = object.getString("message");
      Log.i(TAG, "AMIRREZA=> onReceive: " + strMessage);

      JSONObject messages = new JSONObject(strMessage);
      String typee = messages.getString("type");

      if (typee.equals("callerInfo")) {
        int exten = messages.getInt("exten");
        String participant = messages.getString("participant");
        String queue = messages.getString("queue");
        String voipId = messages.getString("voipId");

        MyApplication.prefManager.setVoipId(voipId);
        MyApplication.prefManager.setQueue(queue);

      } else if (typee.equals("userStatus")) {
        boolean status = messages.getBoolean("status");
        String message = messages.getString("message");
        //todo remove toast
        MyApplication.Toast(message, Toast.LENGTH_SHORT);
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
        Intent broadcastIntent = new Intent(KEY_REFRESH_USER_STATUS);
        broadcastIntent.putExtra(KEY_USER_STATUS, status);
        broadcastIntent.putExtra(KEY_MESSAGE_USER_STATUS, message);
        broadcaster.sendBroadcast(broadcastIntent);

      } else if (typee.equals("message")) {
        String value = messages.getString("value");
        String messageType = messages.getString("messageType");
        createMessageNotification(MyApplication.context, messageType, value);
      }

    } catch (JSONException e) {
      e.printStackTrace();
      if (res == null)
        res = "res is null !";
      AvaCrashReporter.send(e, "PushReceiver class, onReceive method, info : " + res);
    }
  }

  public void createMessageNotification(Context context, String type, String value) {

    NotificationManager mNotificationManager;
    String CHANNEL = "pushChannel";
    Intent intent;
    RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);

    if (MyApplication.prefManager.isAppRun()) {
      new GeneralDialog()
              .title("پیام")
              .message(value)
              .cancelable(true)
              .show();
      //todo check it (empty intent is true?)
      intent = new Intent();
    } else {
      intent = new Intent(context, SplashActivity.class);
    }

    if (type.equals(Constant.PUSH_NOTIFICATION_MESSAGE_TYPE)) {
      intent.putExtra(Constant.PUSH_NOTIFICATION_EXTRA_NAME, Constant.PUSH_NOTIFICATION_MESSAGE_TYPE);
    } else if (type.equals(Constant.PUSH_NOTIFICATION_ANNOUNCEMENT_TYPE)) {
      intent.putExtra(Constant.PUSH_NOTIFICATION_EXTRA_NAME, Constant.PUSH_NOTIFICATION_ANNOUNCEMENT_TYPE);
    }

    collapsedView.setTextViewText(R.id.txtValue, value);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, Constant.PUSH_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    collapsedView.setOnClickPendingIntent(R.id.linearNotif, pendingIntent);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "push")
            .setSmallIcon(R.drawable.ic_operator_user)
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


}