package ir.taxi1880.operatormanagement.push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.app.MyApplication;

//import ir.efsp.ava.io.core.client.Socket;

/***
 * Created by Amirreza Erfanian on 30/march/2019.
 */

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
      AvaLog.e("Push Service CRASH", e);
      AvaCrashReporter.send(e, 100);
    }

    return START_STICKY;
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
        getUnreadPush();
      }
    }, 0, avaPref.getIntervalTime() * 1000);
  }

  public void stopGetMissingPush() {
    AvaLog.w("timeTask stop");

    if (unreadMessageTimer != null)
      unreadMessageTimer.cancel();
    unreadMessageTimer = null;
  }

  private void getUnreadPush() {
    if (avaPref.getMissingApiRequestTime() + 5000 > Calendar.getInstance().getTimeInMillis())
      return;
    avaPref.setMissingApiRequestTime(Calendar.getInstance().getTimeInMillis());
    if (avaPref.getMissingApiUrl() == null) return;

    JSONObject params = new JSONObject();
    try {
      params.put("projectId", avaPref.getProjectId());
      params.put("userId", avaPref.getUserId());
      RequestHelper
              .builder(avaPref.getMissingApiUrl())
              .params(params)
              .listener(onGetMissingPush)
              .request();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  RequestHelper.Callback onGetMissingPush = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      try {
        JSONObject result = new JSONObject(args[0].toString());
        boolean status = result.getBoolean("status");
        if (status) {
          JSONArray arrayMessage = result.getJSONArray("pushMessags");
          for (int i = 0; i < arrayMessage.length(); i++) {
            AvaLog.i("Message receive : " + arrayMessage.getJSONObject(i).toString());
            AvaReporter.Message(context, Keys.PUSH_RECEIVE, arrayMessage.getJSONObject(i).toString());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        AvaCrashReporter.send(e, 101);

      }
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

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
