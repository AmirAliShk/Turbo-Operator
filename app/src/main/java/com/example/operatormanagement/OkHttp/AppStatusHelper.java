package com.example.operatormanagement.OkHttp;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/***
 * Created by Amirreza Erfanian on 2018/July/26.
 * v : 1.0.0
 */
public class AppStatusHelper extends AsyncTask<Context, Void, Boolean>{

  public boolean appIsRun(Context context){
    Boolean status = false;
    try {
      status = this.execute(context).get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
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
      if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
        return true;
      }
    }
    return false;
  }

}
