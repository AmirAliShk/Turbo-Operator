package ir.taxi1880.operatormanagement.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * Created by mohsen on 18/10/2016.
 */
public class AppVersionHelper {

  private static final String TAG = AppVersionHelper.class.getSimpleName();
  Context context;
  PackageInfo pInfo = null;

  public AppVersionHelper(Context context) {
    this.context = context;
    initiate();
  }

  private void initiate() {
    try {
      pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      AvaCrashReporter.send(e,"AppVersionHelper class, initiate method");
    }
  }

  public int getVerionCode() {
    return pInfo.versionCode;
  }

  public String getVerionName() {
    return pInfo.versionName;
  }


}
