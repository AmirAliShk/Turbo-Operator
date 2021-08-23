package ir.taxi1880.operatormanagement.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class ResourceHelper {


  public static int getResIdFromAttribute(final Activity activity, final int attr) {
    if (attr == 0)
      return 0;
    final TypedValue typedvalueattr = new TypedValue();
    activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
    return typedvalueattr.resourceId;
  }

  public static int getAttributeColor(Context context, int attributeId) {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(attributeId, typedValue, true);
    int colorRes = typedValue.resourceId;
    int color = -1;
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        color = context.getColor(colorRes);
      } else {
        color = context.getResources().getColor(colorRes);
      }
    } catch (Resources.NotFoundException e) {
      Log.w("LOG", "Not found color resource by id: " + colorRes);
    }
    return color;
  }


  public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
    Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
    vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.draw(canvas);
    return BitmapDescriptorFactory.fromBitmap(bitmap);
  }
}
