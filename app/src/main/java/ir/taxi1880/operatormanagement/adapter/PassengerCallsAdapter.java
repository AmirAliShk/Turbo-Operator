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
import ir.taxi1880.operatormanagement.model.PassengerCallsModel;

public class PassengerCallsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PassengerCallsModel> passengerCallsModels;


    public PassengerCallsAdapter(Context mContext, ArrayList<PassengerCallsModel> passengerCallsModels) {
        this.mContext = mContext;
        this.passengerCallsModels = passengerCallsModels;
    }

    @Override
    public int getCount() {
        return passengerCallsModels.size();
    }

    @Override
    public Object getItem(int i) {
        return passengerCallsModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_passenger_calls, viewGroup, false);
            TypefaceUtil.overrideFonts(view);
        }

        PassengerCallsModel passengerCallsModels = (PassengerCallsModel) getItem(i);

        TextView txtDate = view.findViewById(R.id.txtDate);
        TextView txtTime = view.findViewById(R.id.txtTime);
        TextView txtTimeRemaining = view.findViewById(R.id.txtTimeRemaining);

        txtDate.setText(passengerCallsModels.getTxtDate());
        txtTime.setText(passengerCallsModels.getTxtTime());
        txtTimeRemaining.setText(passengerCallsModels.getTxtTimeRemaining());

        return view;
    }
}
