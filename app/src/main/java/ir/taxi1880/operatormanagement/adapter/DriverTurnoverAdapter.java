package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.DriverTurnoverModel;

public class DriverTurnoverAdapter extends BaseAdapter {
    ArrayList<DriverTurnoverModel> driverTurnoverModels;

    public DriverTurnoverAdapter(ArrayList<DriverTurnoverModel> driverTurnoverModels) {
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
            view = LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_driver_turnover, null);
            TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        }

        DriverTurnoverModel driverTurnoverModel = (DriverTurnoverModel) getItem(i);

        TextView txtTripDate = view.findViewById(R.id.txtTripDate);
        TextView txtTripTime = view.findViewById(R.id.txtTripTime);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtAmount = view.findViewById(R.id.txtAmount);
        ImageView imgDepositStatus = view.findViewById(R.id.imgDepositStatus);

        txtTripDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianTen(DateHelper.parseDate(driverTurnoverModel.getDate()))));
        txtTripTime.setText(StringHelper.toPersianDigits(driverTurnoverModel.getTime().substring(0, 5)));
        txtDescription.setText(StringHelper.toPersianDigits(driverTurnoverModel.getDescription()));
        if (driverTurnoverModel.getCredit().equals("0")) { // بدهی
            txtDescription.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorRedDark));
            txtAmount.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorRedDark));
            imgDepositStatus.setImageResource(R.drawable.ic_baseline_horizontal_rule_24);
            txtAmount.setText(StringHelper.toPersianDigits(StringHelper.setComma(driverTurnoverModel.getDebit())));
        } else if (driverTurnoverModel.getDebit().equals("0")) { // بستانکاری
            txtDescription.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorGreen));
            txtAmount.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorGreen));
            imgDepositStatus.setImageResource(R.drawable.ic_baseline_add_24);
            txtAmount.setText(StringHelper.toPersianDigits(StringHelper.setComma(driverTurnoverModel.getCredit())));
        }

        return view;
    }
}
