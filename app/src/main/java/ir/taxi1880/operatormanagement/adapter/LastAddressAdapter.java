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
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;

public class LastAddressAdapter extends BaseAdapter {

  private ArrayList<PassengerAddressModel> addressModels;
  private LayoutInflater layoutInflater;

  public LastAddressAdapter(ArrayList<PassengerAddressModel> addressModels, Context context) {
    this.addressModels = addressModels;
    this.layoutInflater=LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return addressModels.size();
  }

  @Override
  public Object getItem(int position) {
    return addressModels.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View myView = convertView;

    try {
      final PassengerAddressModel addressModel = addressModels.get(position);
      if (myView == null) {
        myView = layoutInflater.inflate(R.layout.item_last_address, null);
        TypefaceUtil.overrideFonts(myView);
      }

      TextView txtAddress=myView.findViewById(R.id.txtAddress);
      TextView txtStation=myView.findViewById(R.id.txtStationCode);

      txtAddress.setText(addressModel.getAddress());
      txtStation.setText(addressModel.getStation());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return myView;
  }
}
