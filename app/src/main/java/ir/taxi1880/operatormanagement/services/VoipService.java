package ir.taxi1880.operatormanagement.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.linphone.core.Core;
import org.linphone.core.Factory;

import androidx.annotation.Nullable;
import ir.taxi1880.operatormanagement.activity.CallIncomingActivity;

public class VoipService extends Service {
  private Core mCore;
  private static VoipService sInstance;


  public static Core getCore() {
    return sInstance.mCore;
  }

  public static boolean isReady() {
    return sInstance != null;
  }

  public static VoipService getInstance() {
    return sInstance;
  }

  @Override
  public void onCreate() {
    String basePath = getFilesDir().getAbsolutePath();
    mCore = Factory.instance().createCore(basePath + "/.linphonerc", basePath + "/linphonerc", this);
//    mCore.addListener(mCoreListener);
    super.onCreate();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void onIncomingReceived() {
    Intent intent = new Intent(this, CallIncomingActivity.class);
    // This flag is required to start an Activity from a Service context
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}
