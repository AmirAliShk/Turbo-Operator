package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.NotificationFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerAddressModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class LastAddressAdapter extends BaseAdapter {

    private ArrayList<PassengerAddressModel> addressModels;
    private LayoutInflater layoutInflater;

    public LastAddressAdapter(ArrayList<PassengerAddressModel> addressModels, Context context) {
        this.addressModels = addressModels;
        this.layoutInflater = LayoutInflater.from(context);
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
            final PassengerAddressModel addressModel = addressModels.get(position);
            if (myView == null) {
                myView = layoutInflater.inflate(R.layout.item_last_address, null);
                TypefaceUtil.overrideFonts(myView);
            }

            TextView txtAddress = myView.findViewById(R.id.txtAddress);
            TextView txtStation = myView.findViewById(R.id.txtStation);
            LinearLayout llStation = myView.findViewById(R.id.llStation);
            ImageView imgArchive = myView.findViewById(R.id.imgArchive);

            txtAddress.setText(addressModel.getAddress());
            txtStation.setText(addressModel.getStation() + "");

            if (addressModel.getStatus() == 1) {
                llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.colorRedLight));
            } else {
                llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.transparent));
            }

            imgArchive.setOnClickListener(view -> {
                new GeneralDialog()
                        .title("هشدار")
                        .message("ایا از انجام عملیات فوق اطمینان دارید؟")
                        .firstButton("بله", () -> {
                            MyApplication.Toast("archive", Toast.LENGTH_SHORT);
                            addressModels.remove(position);
                            notifyDataSetChanged();
                        })
                        .secondButton("خیر",null)
                        .cancelable(false)
                        .show();
            });

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "LastAddressAdapter class, getView method");
        }

        return myView;
    }
}
