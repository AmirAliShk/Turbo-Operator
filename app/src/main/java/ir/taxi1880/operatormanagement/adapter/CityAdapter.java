package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class CityAdapter extends BaseAdapter {

    private ArrayList<CityModel> cityModels;
    private LayoutInflater layoutInflater;

    public CityAdapter(ArrayList<CityModel> cityModels) {
        this.cityModels = cityModels;
        this.layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
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
                myView = layoutInflater.inflate(R.layout.item_city, null, false);
                TypefaceUtil.overrideFonts(myView);
            }

            TextView txtCity = myView.findViewById(R.id.txtCity);

            txtCity.setText(cityModel.getCity());

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "TripRegisterActivity class, getView method");
        }

        return myView;
    }
}
