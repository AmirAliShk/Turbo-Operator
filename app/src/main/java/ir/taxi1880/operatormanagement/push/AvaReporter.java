package ir.taxi1880.operatormanagement.push;

import static android.content.Context.ALARM_SERVICE;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_BROADCAST_PUSH;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_MESSAGE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.receiver.PushReceiver;

public class AvaReporter {

    public static final String TAG = AvaReporter.class.getSimpleName();

    public static void Message(Context context, int type, String msg) {
        if (context == null) return;
        try {
            Intent intent = new Intent(context, PushReceiver.class);
            intent.putExtra(Keys.KEY_MESSAGE, msg);
            intent.putExtra(Keys.KEY_BROADCAST_TYPE, type);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), Keys.ALARM_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

            MyApplication.handler.postDelayed(() -> {
                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                Intent broadcastIntent = new Intent(KEY_BROADCAST_PUSH);
                broadcastIntent.putExtra(KEY_MESSAGE, msg);
                broadcaster.sendBroadcast(broadcastIntent);
            }, 1000);

            AvaLog.i("Message receive : " + msg);

            MyApplication.prefManager.setLastNotification(msg);
        } catch (Exception e1) {
            e1.printStackTrace();
            AvaCrashReporter.send(e1, TAG + " class, Message method");
        }
    }

    public static void MessageLog(String msg) {
//    try {
//      Intent intent = new Intent(AvaFactory.getContext().getPackageName() + "." + Keys.KEY_ACTION_RECEIVE_MESSAGE);
//      intent.putExtra(Keys.KEY_MESSAGE, msg);
//      intent.putExtra(Keys.KEY_BROADCAST_TYPE, 1);
//      AvaFactory.getContext().sendBroadcast(intent);
//    } catch (Exception e1) {
//      AvaCrashReporter.send(e1,110);
//    }
    }
}