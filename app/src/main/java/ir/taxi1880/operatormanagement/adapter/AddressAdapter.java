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
import ir.taxi1880.operatormanagement.model.AddressModel;

public class AddressAdapter extends BaseAdapter {

  private ArrayList<AddressModel> addressModels;
  private LayoutInflater layoutInflater;

  public AddressAdapter(ArrayList<AddressModel> addressModels, Context context) {
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
      final AddressModel addressModel = addressModels.get(position);
      if (myView == null) {
        myView = layoutInflater.inflate(R.layout.item_address, null);
        TypefaceUtil.overrideFonts(myView);
      }

      TextView txtAddress=myView.findViewById(R.id.txtAddress);

      txtAddress.setText(addressModel.getAddress());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return myView;
  }
}
