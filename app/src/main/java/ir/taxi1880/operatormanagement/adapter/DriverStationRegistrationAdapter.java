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

public class DriverStationRegistrationAdapter extends BaseAdapter {

    ArrayList<DriverStationRegistrationModel> driverStationRegistrationModels;

    public DriverStationRegistrationAdapter(ArrayList<DriverStationRegistrationModel> driverStationRegistrationModels) {
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
            view = LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_driver_station_registration, null);
            TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        }

        DriverStationRegistrationModel driverStationRegistrationModels = (DriverStationRegistrationModel) getItem(i);

        TextView txtInDate = view.findViewById(R.id.txtInDate);
        TextView txtInTime = view.findViewById(R.id.txtInTime);
        TextView txtStationCode = view.findViewById(R.id.txtStationCode);
        TextView txtOutType = view.findViewById(R.id.txtOutType);
        TextView txtOutDate = view.findViewById(R.id.txtOutDate);
        TextView txtOutTime = view.findViewById(R.id.txtOutTime);

        String inTime = driverStationRegistrationModels.getInTime().substring(0, 5);
        String inDate = driverStationRegistrationModels.getInDate().substring(5);

        txtInDate.setText(StringHelper.toPersianDigits(inDate));
        txtInTime.setText(StringHelper.toPersianDigits(inTime));
        txtStationCode.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getStationCode()));
        if (driverStationRegistrationModels.getOutType().equals("null")) {
            txtOutType.setText(StringHelper.toPersianDigits("خارج نشده"));
            txtOutDate.setText(StringHelper.toPersianDigits("-"));
            txtOutTime.setText(StringHelper.toPersianDigits("-"));
        } else {
            String outDate = driverStationRegistrationModels.getOutDate().substring(5);
            String outTime = driverStationRegistrationModels.getOutTime().substring(0, 5);
            txtOutType.setText(StringHelper.toPersianDigits(driverStationRegistrationModels.getOutType()));
            txtOutTime.setText(StringHelper.toPersianDigits(outTime));
            txtOutDate.setText(StringHelper.toPersianDigits(outDate));
        }

        return view;
    }
}
