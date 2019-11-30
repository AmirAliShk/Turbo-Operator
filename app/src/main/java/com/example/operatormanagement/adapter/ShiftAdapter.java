package com.example.operatormanagement.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.operatormanagement.R;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.fragment.ReplacementFragment;
import com.example.operatormanagement.helper.FragmentHelper;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.NotificationModel;
import com.example.operatormanagement.model.ShiftModel;

import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class ShiftAdapter extends BaseAdapter {

    private ArrayList<ShiftModel> shiftModels;
    private LayoutInflater layoutInflater;

    public ShiftAdapter(ArrayList<ShiftModel> shiftModels, Context context) {
        this.shiftModels = shiftModels;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shiftModels.size();
    }

    @Override
    public Object getItem(int position) {
        return shiftModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        try {
            final ShiftModel shiftModel = shiftModels.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_shift, parent,false);
                TypefaceUtil.overrideFonts(convertView);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.btnReplacementRequest.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("shiftDate", shiftModel.getShiftDate());
                bundle.putString("shiftName", shiftModel.getShiftName());
                FragmentHelper.toFragment(MyApplication.currentActivity, new ReplacementFragment())
                        .setArguments(bundle)
                        .replace();
            });

            viewHolder.txtShiftDate.setText(shiftModel.getShiftDate());
            viewHolder.txtShiftName.setText(shiftModel.getShiftName());
            viewHolder.txtShiftTime.setText(shiftModel.getShiftTime());

            int backIcon = R.drawable.bg_shift_morning;
            int replaceIcon = R.drawable.bg_button_morning;

            switch (shiftModel.getShiftName()) {
                case "صبح":
                    backIcon = R.drawable.bg_shift_morning;
                    replaceIcon = R.drawable.bg_button_morning;
                    viewHolder.llTime.setVisibility(View.VISIBLE);
                    break;
                case "عصر":
                    backIcon = R.drawable.bg_shift_evening;
                    replaceIcon = R.drawable.bg_button_evening;
                    viewHolder.llTime.setVisibility(View.VISIBLE);
                    break;
                case "شب":
                    backIcon = R.drawable.bg_shift_night;
                    replaceIcon = R.drawable.bg_button_night;
                    viewHolder.llTime.setVisibility(View.VISIBLE);
                    break;
                case "استراحت":
                    backIcon = R.drawable.bg_shift_rest;
                    replaceIcon = R.drawable.bg_button_rest;
                    viewHolder.llTime.setVisibility(View.GONE);
                    break;
            }

            viewHolder.rlBack.setBackgroundResource(backIcon);
            viewHolder.btnReplacementRequest.setBackgroundResource(replaceIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtShiftDate;
        TextView txtShiftName;
        TextView txtShiftTime;
        RelativeLayout rlBack;
        LinearLayout llTime;
        Button btnReplacementRequest;

        ViewHolder(View convertView) {
            txtShiftDate = convertView.findViewById(R.id.txtShiftDate);
            txtShiftName = convertView.findViewById(R.id.txtShiftName);
            txtShiftTime = convertView.findViewById(R.id.txtShiftTime);
            rlBack = convertView.findViewById(R.id.rlBack);
            llTime = convertView.findViewById(R.id.llTime);
            btnReplacementRequest = convertView.findViewById(R.id.btnReplacementRequest);
        }
    }

}
