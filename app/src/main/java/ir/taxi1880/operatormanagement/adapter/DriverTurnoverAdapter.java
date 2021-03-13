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
            TypefaceUtil.overrideFonts(view);
        }


        DriverTurnoverModel driverTurnoverModel = (DriverTurnoverModel) getItem(i);

        TextView txtTripDate = view.findViewById(R.id.txtTripDate);
        TextView txtTripTime = view.findViewById(R.id.txtTripTime);
        TextView txtDocumentType = view.findViewById(R.id.txtDocumentType);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtAmount = view.findViewById(R.id.txtAmount);

        txtTripDate.setText(driverTurnoverModel.getDate());
        txtTripTime.setText(driverTurnoverModel.getTime());
        txtDocumentType.setText(driverTurnoverModel.getDocumentType());
        txtDescription.setText(driverTurnoverModel.getDescription());
        txtAmount.setText(driverTurnoverModel.getAmount());

        return view;
    }
}
