package ir.taxi1880.operatormanagement.helper;

import android.app.Activity;
import android.content.Intent;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;

public class ThemeHelper {
    private static boolean _dark = MyApplication.prefManager.isDarkMode();

    public static void changeToTheme(Activity activity, boolean dark) {
        _dark = dark;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        if (_dark) {
            activity.setTheme(R.style.AppThemeDark);
        } else {
            activity.setTheme(R.style.AppThemeLite);
        }
    }
}
