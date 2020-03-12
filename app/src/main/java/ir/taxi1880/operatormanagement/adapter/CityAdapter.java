package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;

public class CityAdapter extends BaseAdapter {

  private ArrayList<CityModel> cityModels;
  private LayoutInflater layoutInflater;

  public CityAdapter(ArrayList<CityModel> cityModels, Context context) {
    this.cityModels = cityModels;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return cityModels.size();
  }

  @Override
  public Object getItem(int position) {
    return cityModels.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View myView = convertView;

    try {
      final CityModel cityModel = cityModels.get(position);
      if (myView == null) {
        myView = layoutInflater.inflate(R.layout.item_city, null);
        TypefaceUtil.overrideFonts(myView);
      }

      TextView txtCity = myView.findViewById(R.id.txtCity);

      txtCity.setText(cityModel.getCity());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return myView;
  }
}
