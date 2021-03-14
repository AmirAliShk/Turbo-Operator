package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverStationRegistrationModel;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;

public class DriverStationRegistrationAdapter extends BaseAdapter {


    Context mContext;
    ArrayList<DriverStationRegistrationModel> driverStationRegistrationModels;

    public DriverStationRegistrationAdapter(Context mContext, ArrayList<DriverStationRegistrationModel> driverStationRegistrationModels) {
        this.mContext = mContext;
        this.driverStationRegistrationModels = driverStationRegistrationModels;
    }

    @Override
    public int getCount() {
        return driverStationRegistrationModels.size();
    }

    @Override
    public Object getItem(int i) {
        return driverStationRegistrationModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_driver_station_registration, null);
            TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        }

        DriverStationRegistrationModel driverStationRegistrationModels = (DriverStationRegistrationModel) getItem(i);

        TextView txtInDate = view.findViewById(R.id.txtInDate);
        TextView txtInTime = view.findViewById(R.id.txtInTime);
        TextView txtStationCode = view.findViewById(R.id.txtStationCode);
        TextView txtOutType = view.findViewById(R.id.txtOutType);
        TextView txtOutDate = view.findViewById(R.id.txtOutDate);
        TextView txtOutTime = view.findViewById(R.id.txtOutTime);

        txtInDate.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getInDate()));
        txtInTime.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getInTime()));
        txtStationCode.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getStationCode()));
        txtOutType.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getOutType()));
        txtOutDate.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getOutDate()));
        txtOutTime.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getOutTime()));

        return view;
    }
}
