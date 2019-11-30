package com.example.operatormanagement.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.operatormanagement.app.MyApplication;

public class TypefaceUtil {

    /**
     * @param v is root view or just root view group <br>
     * <b>Ex in activity :  <b/><br>
     * <b> ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
     .findViewById(android.R.id.content)).getChildAt(0);</b>
     * <br>
     * <b>Ex in fragment :  just use view of fragment <b/><br>
     *  mohsen1 mostafaei 2014
    * */

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
        }
        catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

}