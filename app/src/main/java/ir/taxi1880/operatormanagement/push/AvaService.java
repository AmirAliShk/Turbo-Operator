package ir.taxi1880.operatormanagement.push;

import static ir.taxi1880.operatormanagement.services.LinphoneService.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.SplashActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.NotificationSingleton;

public class AvaService extends Service {

    private static final String TAG = AvaService.class.getSimpleName();
    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        AvaLog.i("Push Service BIND");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        AvaLog.i("Push Service UNBIND");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AvaLog.i("Push Service CREATE");
    }

    AvaPref avaPref;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // getAPI socket, if was null create again

        try {
            // if service restart on background we must fill the variable with current context
            if (MyApplication.context == null) {
                MyApplication.context = this;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(NotificationSingleton.getNotificationId(), NotificationSingleton.getNotification(this));
            }
//      MyApplication.prefManager.incrementResetPushServiceCount();

            avaPref = new AvaPref();
            avaPref.setIpRow(0);

            AvaSocket.getConnection(context).checkConnection();
            AvaLog.i("Push Service STARTING");

            if (avaPref.isMissingSocketEnable()) {
                startCheckConnection();
            }

            if (avaPref.isMissingApiEnable()) {
                startGetMissingPush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AvaLog.e("Push Service CRASH", e);
            AvaCrashReporter.send(e, TAG + " class, onStartCommand method");
        }

        return START_STICKY;
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        Bitmap iconNotification = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("LinphoneGroupId", "LinphoneGroupName"));
//        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "LinphoneChannelName",
//                NotificationManager.IMPORTANCE_MIN);
//        notificationChannel.enableLights(false);
//        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//        notificationManager.createNotificationChannel(notificationChannel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "LinphoneChannelName",
                    NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("در حال کار")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(0)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        startForeground(1880, builder.build());
    }

    @Override
    public void onDestroy() {
        stopGetMissingPush();
        stopCheckConnection();
        AvaSocket.getConnection(context).disconnectSocket();
        Intent intent = new Intent("ir.taxi1880.operatormanagement.PUSH_SERVICE_DESTROY");
        sendBroadcast(intent);

        AvaLog.e("push service Stopped");
        super.onDestroy();
    }

    // Send Data every 1 seconds to activity
    private Timer unreadMessageTimer;

    public void startGetMissingPush() {
        if (unreadMessageTimer != null) {
            return;
        }
        unreadMessageTimer = new Timer();
        unreadMessageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new ReadUnreadMessage().getUnreadPush(false, context);
            }
        }, 0, avaPref.getIntervalTime() * 1000);
    }

    public void stopGetMissingPush() {
        AvaLog.w("timeTask stop");

        if (unreadMessageTimer != null)
            unreadMessageTimer.cancel();
        unreadMessageTimer = null;
    }

    private Timer checkConnectionTimer;

    public void startCheckConnection() {
        if (checkConnectionTimer != null) {
            return;
        }

        checkConnectionTimer = new Timer();
        checkConnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AvaSocket.getConnection(context).checkConnection();
            }
        }, 20000, avaPref.getMissingSocketIntervalTime() * 1000);
    }

    public void stopCheckConnection() {
        if (checkConnectionTimer != null) {
            checkConnectionTimer.cancel();
        }
        checkConnectionTimer = null;
    }
}
