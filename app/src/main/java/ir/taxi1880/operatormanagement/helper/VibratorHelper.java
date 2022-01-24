package ir.taxi1880.operatormanagement.helper;

import android.content.Context;
import android.os.Vibrator;

import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class VibratorHelper {
    public static final String TAG = VibratorHelper.class.getSimpleName();
    public static Vibrator vibrator;

    public static void setVibrator(Context context, long[] pattern, int repeat) {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, repeat);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, setVibrator method");
        }
    }

    public static void setVibrator(Context context, long[] pattern) {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, -1);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, setVibrator method");
        }
    }

    public static void setVibrator(Context context) {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//      long[] pattern = {0, 1000, 800, 1000, 800, 1000, 800, 1000, 800, 1000, 800};
            vibrator.vibrate(50);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, setVibrator method");
        }
    }

    public static void stopVibrator() {
        if (vibrator != null)
            vibrator.cancel();
    }
}