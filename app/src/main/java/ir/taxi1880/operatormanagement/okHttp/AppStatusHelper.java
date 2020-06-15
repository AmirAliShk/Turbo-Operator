package ir.taxi1880.operatormanagement.okHttp;
import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/***
 * Created by Amirreza Erfanian on 2018/July/26.
 * v : 1.0.0
 */
public class AppStatusHelper extends AsyncTask<Context, Void, Boolean> {

  private static final String TAG = AppStatusHelper.class.getSimpleName();
  public static boolean appIsRun(Context context){
    Boolean status = false;
    try {
      status = new AppStatusHelper().execute(context).get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      AvaCrashReporter.send(e,"AppStatusHelper class, appIsRun method InterruptedException");
    } catch (ExecutionException e) {
      e.printStackTrace();
      AvaCrashReporter.send(e,"AppStatusHelper class, appIsRun method ExecutionException");
    }
    return status;
  }

  @Override
  protected Boolean doInBackground(Context... params) {
    final Context context = params[0].getApplicationContext();
    return isAppOnForeground(context);
  }

  private boolean isAppOnForeground(Context context) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
    if (appProcesses == null) {
      return false;
    }
    final String packageName = context.getPackageName();
    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
      if ((appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
              ||appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) && appProcess.processName.equals(packageName)) {
        return true;
      }
    }

    return false;
  }

}
