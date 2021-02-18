package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;

public class AllComplaintAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AllComplaintModel> allComplaintModels;


    public AllComplaintAdapter(Context mContext, ArrayList<AllComplaintModel> allComplaintModels) {
        this.mContext = mContext;
        this.allComplaintModels = allComplaintModels;
    }

    @Override
    public int getCount() {
        return allComplaintModels.size();
    }

    @Override
    public Object getItem(int i) {
        return allComplaintModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_all_complaint, viewGroup, false);
        }

        AllComplaintModel currentAllComplaintModel = (AllComplaintModel) getItem(i);

        TextView txtComplaintDate = view.findViewById(R.id.txtComplaintDate);
        TextView txtComplaintTime = view.findViewById(R.id.txtComplaintTime);

        txtComplaintDate.setText(currentAllComplaintModel.getDate());
        txtComplaintTime.setText(currentAllComplaintModel.getTime());

        return view;
    }
}
