package ir.taxi1880.operatormanagement.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

import java.util.Locale;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    public static Context context;
    public static Activity currentActivity;
    public static Handler handler;
    private static final String IRANSANS = "fonts/IRANSans.otf";
    public static Typeface iranSance;
    public static PrefManager prefManager;
    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String DIR_DOWNLOAD;
    public static String DIR_ROOT;
    public static FragmentManager fragmentManagerV4;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        initTypeface();

        prefManager = new PrefManager(context);
        DIR_ROOT = DIR_SDCARD + "/Android/data/" + context.getPackageName() + "/";
        DIR_DOWNLOAD = DIR_SDCARD + "/Android/data/" + context.getPackageName() + "/files/";
        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }

    private void initTypeface() {
        iranSance = Typeface.createFromAsset(getAssets(), IRANSANS);
    }

    public static void ErrorToast(String message, int duration) {
//    LayoutInflater inflater = LayoutInflater.from(currentActivity);
//    View v = inflater.inflate(R.layout.toast,null,false);
//    TypefaceUtil.overrideFonts(v);
//
//    TextView text = (TextView) v.findViewById(R.id.text);
//    text.setText(message);
//
//    Toast toast = new Toast(currentActivity);
//    toast.setGravity(Gravity.BOTTOM, 0, 0);
//    toast.setDuration(duration);
//    toast.setView(v);
//    toast.show();
    }

    public static void Toast(String message, int duration) {
        LayoutInflater layoutInflater = LayoutInflater.from(currentActivity);
        View v=layoutInflater.inflate(R.layout.item_toast,null);
        TypefaceUtil.overrideFonts(v);
        TextView text = (TextView) v.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(currentActivity);
        toast.setGravity(Gravity.BOTTOM, 0, 80);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}
