package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AddressesModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class LastAddressAdapter extends BaseAdapter {

    private ArrayList<AddressesModel> addressModels;
    private LayoutInflater layoutInflater;
    boolean isFromOrigin;
    String passengerId;

    public LastAddressAdapter(boolean isFromOrigin, String passengerId, ArrayList<AddressesModel> addressModels) {
        this.addressModels = addressModels;
        this.isFromOrigin = isFromOrigin;
        this.passengerId = passengerId;
        this.layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
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
            final AddressesModel addressModel = addressModels.get(position);
            if (myView == null) {
                myView = layoutInflater.inflate(R.layout.item_last_address, null);
                TypefaceUtil.overrideFonts(myView);
            }

            TextView txtAddress = myView.findViewById(R.id.txtAddress);
            TextView txtStation = myView.findViewById(R.id.txtStation);
//            LinearLayout llStation = myView.findViewById(R.id.llStation);
            ImageView imgArchive = myView.findViewById(R.id.imgArchive);

            txtAddress.setText(addressModel.getAddress());
            txtStation.setText(addressModel.getStation() + "");


//            if (addressModel.getStatus() == 1) {
//                llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.colorRedLight));
//            } else {
//                llStation.setBackgroundColor(MyApplication.currentActivity.getResources().getColor(R.color.transparent));
//            }

            imgArchive.setOnClickListener(view -> {
                new GeneralDialog()
                        .title("هشدار")
                        .message("ایا از انجام عملیات فوق اطمینان دارید؟")
                        .firstButton("بله", () -> {
                            archiveOrigin(addressModels.get(position).getAddressId(), isFromOrigin);
                            addressModels.remove(position);
                            notifyDataSetChanged();
                        })
                        .secondButton("خیر", null)
                        .cancelable(false)
                        .show();
            });

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "LastAddressAdapter class, getView method");
        }

        return myView;
    }

    private void archiveOrigin(String addressId, boolean isOrigin) {
        String oOrd;
        if (isOrigin) oOrd = "origin";
        else oOrd = "destination";

        RequestHelper.builder(EndPoints.DELETE_ADDRESS)
                .addParam("passengerId", passengerId)
                .addParam("addressId", addressId)
                .addParam("type", oOrd)
                .listener(onArchiveAddress)
                .delete();
    }

    RequestHelper.Callback onArchiveAddress = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    //{"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    // no need to show the response to the user
                    // just call the API
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
            });
        }
    };

}
