package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;


public class DriverTurnoverAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<DriverTurnoverModel> driverTurnoverModels;

    public DriverTurnoverAdapter(Context mContext, ArrayList<DriverTurnoverModel> driverTurnoverModels) {
        this.mContext = mContext;
        this.driverTurnoverModels = driverTurnoverModels;
    }

    @Override
    public int getCount() {
        return driverTurnoverModels.size();
    }

    @Override
    public Object getItem(int i) {
        return driverTurnoverModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_driver_turnover, null);
            TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        }

        DriverTurnoverModel driverTurnoverModel = (DriverTurnoverModel) getItem(i);

        TextView txtTripDate = view.findViewById(R.id.txtTripDate);
        TextView txtTripTime = view.findViewById(R.id.txtTripTime);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtAmount = view.findViewById(R.id.txtAmount);
        ViewFlipper vfTurnoverStatus = view.findViewById(R.id.vfTurnoverStatus);

        txtTripDate.setText(StringHelper.toPersianDigits(driverTurnoverModel.getDate()));
        txtTripTime.setText(StringHelper.toPersianDigits(driverTurnoverModel.getTime()));
        txtDescription.setText(StringHelper.toPersianDigits(driverTurnoverModel.getDescription()));
        if (driverTurnoverModel.getCredit().equals("0")) {
            vfTurnoverStatus.setDisplayedChild(1);
            txtAmount.setText(StringHelper.toPersianDigits(StringHelper.setComma(driverTurnoverModel.getDebit())));
        } else if (driverTurnoverModel.getDebit().equals("0")) {
            vfTurnoverStatus.setDisplayedChild(0);
            txtAmount.setText(StringHelper.toPersianDigits(driverTurnoverModel.getCredit()));
        }

        return view;
    }
}
