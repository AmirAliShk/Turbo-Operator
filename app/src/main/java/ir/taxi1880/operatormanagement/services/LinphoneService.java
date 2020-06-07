package ir.taxi1880.operatormanagement.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.acra.ACRA;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.LogCollectionState;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.CallIncomingActivity;
import ir.taxi1880.operatormanagement.activity.TripRegisterActivity;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.ServiceHelper;
import ir.taxi1880.operatormanagement.helper.SoundHelper;
import ir.taxi1880.operatormanagement.helper.VibratorHelper;
import ir.taxi1880.operatormanagement.push.AvaFactory;

import static ir.taxi1880.operatormanagement.helper.SoundHelper.stop;
import static java.lang.Thread.sleep;

public class LinphoneService extends Service {
  private static final String START_LINPHONE_LOGS = " ==== Device information dump ====";
  // Keep a static reference to the Service so we can access it from anywhere in the app
  private static LinphoneService sInstance;
  private static final Handler sHandler = new Handler(Looper.getMainLooper());

  private Handler mHandler;
  private Timer mTimer;

  private Core mCore;
  private CoreListenerStub mCoreListener;
  long[] pattern = {0,70,70};
  public static final String CHANNEL_ID = "ForegroundServiceChannel";

  public static boolean isReady() {
    return sInstance != null;
  }

  public static LinphoneService getInstance() {
    return sInstance;
  }

  public static Core getCore() {
    if (sInstance != null && sInstance.mCore != null) {
      return sInstance.mCore;
    } else {
      ServiceHelper.start(MyApplication.context, LinphoneService.class);
      while (!LinphoneService.isReady()) {
        try {
          sleep(30);
        } catch (InterruptedException e) {
          throw new RuntimeException("waiting thread sleep() has been interrupted");
        }
      }
      return sInstance.mCore;
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // The first call to liblinphone SDK MUST BE to a Factory method
    // So let's enable the library debug logs & log collection
    String basePath = getFilesDir().getAbsolutePath();
    Factory.instance().setLogCollectionPath(basePath);
    Factory.instance().enableLogCollection(LogCollectionState.Enabled);
    Factory.instance().setDebugMode(false, "AMIR SIP => ");

    // Dump some useful information about the device we're running on
    Log.i(START_LINPHONE_LOGS);
    dumpDeviceInformation();
    dumpInstalledLinphoneInformation();

    mHandler = new Handler();
    // This will be our main Core listener, it will change activities depending on events
    mCoreListener = new CoreListenerStub() {
      @Override
      public void onCallStateChanged(Core core, final Call call, Call.State state, String message) {

        if (state == Call.State.End) {
          MyApplication.prefManager.setCallIncoming(false);
          DataHolder.getInstance().setEndCall(true);
          MyApplication.prefManager.setConnectedCall(false);
          NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.cancel(0);
          VibratorHelper.setVibrator(MyApplication.context,pattern);
          stop();
        } else {
          DataHolder.getInstance().setEndCall(false);
        }

        if (state == Call.State.Released) {
          stop();
        }

        if (state == Call.State.IncomingReceived) {
          onIncomingReceived();
          MyApplication.prefManager.setCallIncoming(true);
        }

        if (state == Call.State.Connected) {
          if (MyApplication.prefManager.isCallIncoming()){
            Address address = call.getRemoteAddress();
            MyApplication.prefManager.setLastCall(address.getUsername());
          }
          MyApplication.prefManager.setConnectedCall(true);
          VibratorHelper.setVibrator(MyApplication.context,pattern);
          //if don't receive push notification from server we call missingPushApi
          AvaFactory.getInstance(getApplicationContext()).readMissingPush();
          stop();
        }
      }
    };

    try {
      // Let's copy some RAW resources to the device
      // The default config file must only be installed once (the first time)
      copyIfNotExist(R.raw.linphonerc_default, basePath + "/.linphonerc");
      // The factory config is used to override any other setting, let's copy it each time
      copyFromPackage(R.raw.linphonerc_factory, "linphonerc");
    } catch (IOException ioe) {
      Log.e(ioe);
    }

    // Create the Core and add our listener
    mCore = Factory.instance()
            .createCore(basePath + "/.linphonerc", basePath + "/linphonerc", this);
    mCore.addListener(mCoreListener);
    // Core is ready to be configured
    configureCore();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    // If our Service is already running, no need to continue
    if (sInstance != null) {
      return START_STICKY;
    }

    // Our Service has been started, we can keep our reference on it
    // From now one the Launcher will be able to call onServiceReady()
    sInstance = this;
    mCore.setUserAgent("mohsen@123", null);

    // Core must be started after being created and configured
    mCore.start();
    // We also MUST call the iterate() method of the Core on a regular basis
    TimerTask lTask =
            new TimerTask() {
              @Override
              public void run() {
                mHandler.post(
                        new Runnable() {
                          @Override
                          public void run() {
                            if (mCore != null) {
                              mCore.iterate();
                            }
                          }
                        });
              }
            };
    mTimer = new Timer("Linphone scheduler");
    mTimer.schedule(lTask, 0, 20);

    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    mCore.removeListener(mCoreListener);
    if (mTimer != null)
      mTimer.cancel();
    mCore.stop();
    // A stopped Core can be started again
    // To ensure resources are freed, we must ensure it will be garbage collected
    mCore = null;
    // Don't forget to free the singleton as well
    sInstance = null;

    super.onDestroy();
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    // For this sample we will kill the Service at the same time we kill the app
    stopSelf();

    super.onTaskRemoved(rootIntent);
  }

  private void configureCore() {
    // We will create a directory for user signed certificates if needed
    String basePath = getFilesDir().getAbsolutePath();
    String userCerts = basePath + "/user-certs";
    File f = new File(userCerts);
    if (!f.exists()) {
      if (!f.mkdir()) {
        Log.e(userCerts + " can't be created.");
      }
    }
    mCore.setUserCertificatesPath(userCerts);
  }

  private void dumpDeviceInformation() {
    StringBuilder sb = new StringBuilder();
    sb.append("DEVICE=").append(Build.DEVICE).append("\n");
    sb.append("MODEL=").append(Build.MODEL).append("\n");
    sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
    sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
    sb.append("Supported ABIs=");
    for (String abi : Version.getCpuAbis()) {
      sb.append(abi).append(", ");
    }
    sb.append("\n");
    Log.i("AMIR : " + sb.toString());
  }

  private void dumpInstalledLinphoneInformation() {
    PackageInfo info = null;
    try {
      info = getPackageManager().getPackageInfo(getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException nnfe) {
      Log.e(nnfe);
    }

    if (info != null) {
      Log.i("[Service] Linphone version is ",
              info.versionName + " (" + info.versionCode + ")");
    } else {
      Log.i("[Service] Linphone version is unknown");
    }
  }

  private void copyIfNotExist(int ressourceId, String target) throws IOException {
    File lFileToCopy = new File(target);
    if (!lFileToCopy.exists()) {
      copyFromPackage(ressourceId, lFileToCopy.getName());
    }
  }

  private void copyFromPackage(int ressourceId, String target) throws IOException {

    FileOutputStream lOutputStream = openFileOutput(target, 0);
    InputStream lInputStream = getResources().openRawResource(ressourceId);
    int readByte;
    byte[] buff = new byte[8048];
    while ((readByte = lInputStream.read(buff)) != -1) {
      lOutputStream.write(buff, 0, readByte);
    }
    lOutputStream.flush();
    lOutputStream.close();
    lInputStream.close();
  }

  private void onIncomingReceived() {
    SoundHelper.ringing(R.raw.ring);
    if (TripRegisterActivity.isRunning) return;
    Intent intent = new Intent(this, CallIncomingActivity.class);
    // This flag is required to start an Activity from a Service context
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

    startActivity(intent);
  }

  public static void removeFromUIThreadDispatcher(Runnable r) {
    sHandler.removeCallbacks(r);
  }

  public static void dispatchOnUIThreadAfter(Runnable r, long after) {
    sHandler.postDelayed(r, after);
  }

  public static void trackBreadcrumb(String event) {
    ACRA.getErrorReporter().putCustomData("Event details", event);
  }
}
