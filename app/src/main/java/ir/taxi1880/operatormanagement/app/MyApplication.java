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
import org.acra.BuildConfig;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;
import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;
import org.linphone.core.TransportType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.push.AvaFactory;
import ir.taxi1880.operatormanagement.services.LinphoneService;

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
    public static final String DIR_MAIN_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TurboOperator/";
    public static final String VOICE_FOLDER_NAME = "Voice/";
    public static final String image_path_save = DIR_MAIN_FOLDER + "Image/";
    public static final String SOUND = "android.resource://ir.taxi1880.operatormanagement/";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        initTypeface();

        prefManager = new PrefManager(context);

        File file = new File(DIR_MAIN_FOLDER + VOICE_FOLDER_NAME + ".nomedia");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onCreate method ");
        }

        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        initACRA();

        if (MyApplication.prefManager.getUserCode() != 0) {
            avaStart();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void initACRA() {
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);

        Map<String, String> authHeaderMap = new HashMap<>();
        authHeaderMap.put("Authorization", MyApplication.prefManager.getAuthorization());
        authHeaderMap.put("id_token", MyApplication.prefManager.getIdToken());

        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .setUri(EndPoints.ACRA_PATH)
                .setHttpMethod(HttpSender.Method.POST)
                .setHttpHeaders(authHeaderMap)
                .setEnabled(true);
//        if (!BuildConfig.DEBUG)
        ACRA.init(this, builder);
    }

    public static void avaStart() {
        if (prefManager.getUserCode() == 0) return;
        if (prefManager.getPushId() == 0) return;
        if (prefManager.getPushToken() == null) return;

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
        handler.post((Runnable) () -> {
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
        });
    }

    public static void configureAccount() {
        try {
            Core core = LinphoneService.getCore();
            core.clearAllAuthInfo();
            core.clearProxyConfig();

            // No account configured, we display the configuration activity
            AccountCreator mAccountCreator = LinphoneService.getCore().createAccountCreator(null);

            mAccountCreator.setDomain((BuildConfig.DEBUG)
//              ? "172.16.2.216:4060"
                    ? prefManager.getSipServer()
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
            AvaCrashReporter.send(e, TAG + " class, configureAccount method");
        }
    }
}