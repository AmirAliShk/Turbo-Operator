package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationModel;

public class StationAdapter extends BaseAdapter {

  private ArrayList<StationModel> stationModels;
  private LayoutInflater layoutInflater;

  public StationAdapter(ArrayList<StationModel> stationModels, Context context) {
    this.stationModels = stationModels;
    this.layoutInflater=LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return stationModels.size();
  }

  @Override
  public Object getItem(int position) {
    return stationModels.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View myView = convertView;

    try {
      final StationModel stationModel = stationModels.get(position);
      if (myView == null) {
        myView = layoutInflater.inflate(R.layout.item_station, null);
        TypefaceUtil.overrideFonts(myView);
      }

      TextView txtAddress=myView.findViewById(R.id.txtAddress);
      TextView txtStationName=myView.findViewById(R.id.txtStationName);
      TextView txtStationCode=myView.findViewById(R.id.txtStationCode);
      LinearLayout llStation=myView.findViewById(R.id.llStation);

      txtAddress.setText(stationModel.getAddress());
      txtStationName.setText(stationModel.getName());
      txtStationCode.setText(stationModel.getCode()+"");
      if (stationModel.getCountrySide()==1){
        llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPurpleLight));
      }else {
        llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.colorWhite));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return myView;
  }
}
