package ir.taxi1880.operatormanagement.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.push.Keys;

public class PushReceiver extends BroadcastReceiver {
  public static final String TAG = PushReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    try {
      Bundle bundle = intent.getExtras();
      String result = bundle.getString(Keys.KEY_MESSAGE);
      int type = bundle.getInt(Keys.KEY_BROADCAST_TYPE);

      JSONObject object = new JSONObject(result);
      String message = object.getString("message");
      Log.i(TAG, "onReceive: "+message);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
