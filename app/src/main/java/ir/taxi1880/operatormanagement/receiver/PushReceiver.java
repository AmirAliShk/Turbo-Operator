package ir.taxi1880.operatormanagement.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import ir.taxi1880.operatormanagement.app.MyApplication;

import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.push.Keys;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_BROADCAST_PUSH;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_MESSAGE;
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
      if (typee.equals("callerInfo")){
        int exten = messages.getInt("exten");
        String participant = messages.getString("participant");
        String queue = messages.getString("queue");
        String voipId = messages.getString("voipId");

        MyApplication.prefManager.setVoipId(voipId);
        MyApplication.prefManager.setQueue(queue);

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
        Intent broadcastIntent = new Intent(KEY_BROADCAST_PUSH);
        broadcastIntent.putExtra(KEY_MESSAGE, result);
        broadcaster.sendBroadcast(broadcastIntent);

      }else if (typee.equals("userStatus")){
        int status = messages.getInt("status");
        String message = messages.getString("message");

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
        Intent broadcastIntent = new Intent(KEY_REFRESH_USER_STATUS);
        broadcastIntent.putExtra(KEY_USER_STATUS, status);
        broadcastIntent.putExtra(KEY_MESSAGE_USER_STATUS, message);
        broadcaster.sendBroadcast(broadcastIntent);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      if (res == null)
        res = "res is null !";
      AvaCrashReporter.send(e,"PushReceiver class, onReceive method, info : " + res );
    }
  }

}