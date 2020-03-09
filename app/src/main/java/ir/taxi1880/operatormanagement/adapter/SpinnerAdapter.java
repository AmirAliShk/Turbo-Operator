package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SpinnerAdapter extends ArrayAdapter<String> {

  public SpinnerAdapter(@NonNull Context context, int resource, List<String> items) {
    super(context, resource, items);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View v = super.getView(position, convertView, parent);
    TypefaceUtil.overrideFonts(v);
    return v;
  }

  @Override
  public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View v = super.getDropDownView(position, convertView, parent);
    TypefaceUtil.overrideFonts(v);
    return v;
  }


}
