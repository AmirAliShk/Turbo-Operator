package ir.taxi1880.operatormanagement.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.annotation.AcraHttpSender;
import org.acra.sender.HttpSender;
import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;
import org.linphone.core.TransportType;

import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import ir.taxi1880.operatormanagement.BuildConfig;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaFactory;
import ir.taxi1880.operatormanagement.services.LinphoneService;

@AcraHttpSender(uri = "http://turbotaxi.ir:6061/api/crashReport", httpMethod = HttpSender.Method.POST)
public class MyApplication extends Application {

  private static final String TAG = MyApplication.class.getSimpleName();
  public static Context context;
  public static Activity currentActivity;
  public static Handler handler;
  private static final String IRANSANS = "fonts/IRANSans.otf";
  private static final String IRANSANS_MEDUME = "fonts/IRANSANSMOBILE_MEDIUM.TTF";
  private static final String IRANSANS_BOLD = "fonts/IRANSANSMOBILE_BOLD.TTF";
  private static final String IRANSANS_LIGHT = "fonts/IRANSANSMOBILE_LIGHT.TTF";
  public static Typeface iranSance;
  public static Typeface IraSanSMedume;
  public static Typeface IraSanSBold;
  public static Typeface IraSanSLight;
  public static PrefManager prefManager;
  public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
  public static String DIR_DOWNLOAD;
  public static String DIR_ROOT;
  public static FragmentManager fragmentManagerV4;
  public static final String SOUND = "android.resource://ir.taxi1880.operatormanagement/";

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

    if (MyApplication.prefManager.getUserCode() != 0)
      avaStart();

    ACRA.init(this);

  }

  public static void avaStart() {
//        if (prefManager.getLineCode().equals("0")) return;
//        if (prefManager.getAvaPID()==0) return;
//        if (prefManager.getAvaToken()==null) return;

    Log.i(TAG, "avaStart: " + MyApplication.prefManager.getUserCode());
    Log.i(TAG, "avaStart: " + MyApplication.prefManager.getPushId());
    Log.i(TAG, "avaStart: " + MyApplication.prefManager.getPushToken());
    AvaFactory.getInstance(context)
            .setUserID(MyApplication.prefManager.getUserCode() + "")
            .setProjectID(MyApplication.prefManager.getPushId())
            .setToken(MyApplication.prefManager.getPushToken())
            .setAddress(EndPoints.PUSH_ADDRESS)
            .start();
  }

  private void initTypeface() {
    iranSance = Typeface.createFromAsset(getAssets(), IRANSANS);
    IraSanSMedume = Typeface.createFromAsset(getAssets(), IRANSANS_MEDUME);
    IraSanSLight = Typeface.createFromAsset(getAssets(), IRANSANS_LIGHT);
    IraSanSBold = Typeface.createFromAsset(getAssets(), IRANSANS_BOLD);
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
    View v = layoutInflater.inflate(R.layout.item_toast, null);
    TypefaceUtil.overrideFonts(v);
    TextView text = (TextView) v.findViewById(R.id.text);
    text.setText(message);
    Toast toast = new Toast(currentActivity);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.setDuration(duration);

    toast.setView(v);
    toast.show();
  }

  public static void configureAccount() {
    try {
      Core core = LinphoneService.getCore();
      core.clearAllAuthInfo();
      core.clearProxyConfig();

      // No account configured, we display the configuration activity
      AccountCreator mAccountCreator = LinphoneService.getCore().createAccountCreator(null);

      mAccountCreator.setDomain((BuildConfig.DEBUG)
              ? "172.16.2.216:4060"
              : prefManager.getSipServer());
      mAccountCreator.setUsername(prefManager.getSipNumber() + "");
      mAccountCreator.setPassword(prefManager.getSipPassword());
      mAccountCreator.setTransport(TransportType.Udp);

      // This will automatically create the proxy config and auth info and add them to the Core
      ProxyConfig cfg = mAccountCreator.createProxyConfig();

      // Make sure the newly created one is the default
      core.setDefaultProxyConfig(cfg);

      // At least the 3 below values are required
    } catch (Exception e) {
      e.printStackTrace();
    }


  }


}
