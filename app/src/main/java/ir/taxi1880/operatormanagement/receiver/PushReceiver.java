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
      Log.i(TAG, "AMIRREZA=> onReceive: " + strMessage);


      JSONObject message = new JSONObject(strMessage);
      String typee = message.getString("type");
      int exten = message.getInt("exten");
      String participant = message.getString("participant");
      String queue = message.getString("queue");
      String voipId = message.getString("voipId");

      LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
      Intent broadcastIntent = new Intent(KEY_BROADCAST_PUSH);
      broadcastIntent.putExtra(KEY_MESSAGE, result);
      broadcaster.sendBroadcast(broadcastIntent);

      MyApplication.prefManager.setVoipId(voipId);
      MyApplication.prefManager.setQueue(queue);


    } catch (JSONException e) {
      e.printStackTrace();
      AvaCrashReporter.send(e,"PushReceiver class, onReceive method ");
    }
  }

}