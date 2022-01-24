package ir.taxi1880.operatormanagement.helper;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class TypefaceUtil {

    public static final String TAG = TypefaceUtil.class.getSimpleName();

    /**
     * @param v is root view or just root view group <br>
     *          <b>Ex in activity :  <b/><br>
     *          <b> ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
     *          .findViewById(android.R.id.content)).getChildAt(0);</b>
     *          <br>
     *          <b>Ex in fragment :  just use view of fragment <b/><br>
     *          mohsen1 mostafaei 2014
     */

    public static void overrideFonts(final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(MyApplication.iranSance);
            }
        } catch (Exception e) {
            AvaCrashReporter.send(e, TAG + " class, overrideFonts method");
            e.printStackTrace();
        }
    }

    public static void overrideFonts(final View v, Typeface typeface) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child, typeface);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(typeface);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, overrideFonts method");
        }
    }
}