package ir.taxi1880.operatormanagement.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.TripDetailsFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TripModel;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

  ArrayList<TripModel> tripModels;

  public TripAdapter(ArrayList<TripModel> tripModels) {
    this.tripModels = tripModels;
  }

  @NonNull
  @Override
  public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
    TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
    return new TripViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
    final TripModel tripModel = tripModels.get(position);
    holder.txtCallTime.setText(StringHelper.toPersianDigits(tripModel.getCallTime()));
    holder.txtSendTime.setText(tripModel.getSendTime() == null ? "ثبت نشده" : StringHelper.toPersianDigits(tripModel.getSendTime()));
    holder.txtCarType.setText(tripModel.getCarType() == null ? "ثبت نشده" : StringHelper.toPersianDigits(tripModel.getCarType()));
    holder.txtCity.setText(StringHelper.toPersianDigits(tripModel.getCity()));
    holder.txtCustomerAddress.setText(StringHelper.toPersianDigits(tripModel.getAddress()));
    holder.txtCustomerMobile.setText(StringHelper.toPersianDigits(tripModel.getCustomerMob()));
    holder.txtCustomerName.setText(StringHelper.toPersianDigits(tripModel.getCustomerName()));
    holder.txtCustomerTell.setText(StringHelper.toPersianDigits(tripModel.getCustomerTell()));
    holder.txtDriverMobile.setText(tripModel.getDriverMobile() == null ? "ثبت نشده" : StringHelper.toPersianDigits(tripModel.getDriverMobile()));

    int headerColor = R.drawable.header_blue;
    String statusTitle = "درحال انتظار";

    if (tripModel.getFinished() == 1) {
      headerColor = R.drawable.header_green;
      statusTitle = "اتمام یافته";
    }

    switch (tripModel.getStatus()) {
      case 0:
        headerColor = R.drawable.header_blue;
        statusTitle = "درحال انتظار";
        break;

      case 6:
        headerColor = R.drawable.header_red;
        statusTitle = "کنسل شده";
        break;

      case 1:
        headerColor = R.drawable.header_yellow;
        statusTitle = "اعزام شده";
        break;
    }
    holder.llHeaderStatus.setBackgroundResource(headerColor);
    holder.txtStatus.setText(statusTitle);

    holder.itemView.setOnClickListener(view -> {
      Bundle bundle = new Bundle();
      bundle.putString("id", tripModel.getServiceId());
      FragmentHelper.toFragment(MyApplication.currentActivity, new TripDetailsFragment()).setArguments(bundle).replace();
      KeyBoardHelper.hideKeyboard();
    });
  }

  @Override
  public int getItemCount() {
    return tripModels.size();
  }

  public class TripViewHolder extends RecyclerView.ViewHolder {
    TextView txtCallTime;
    TextView txtSendTime;
    TextView txtStatus;
    TextView txtCustomerName;
    TextView txtCustomerTell;
    TextView txtCustomerMobile;
    TextView txtCity;
    TextView txtCustomerAddress;
    TextView txtCarType;
    TextView txtDriverMobile;
    LinearLayout llHeaderStatus;

    public TripViewHolder(@NonNull View itemView) {
      super(itemView);
      txtCallTime = itemView.findViewById(R.id.txtCallTime);
      txtStatus = itemView.findViewById(R.id.txtStatus);
      txtSendTime = itemView.findViewById(R.id.txtSendTime);
      txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
      txtCustomerTell = itemView.findViewById(R.id.txtCustomerTell);
      txtCustomerMobile = itemView.findViewById(R.id.txtCustomerMobile);
      txtCity = itemView.findViewById(R.id.txtCity);
      txtCustomerAddress = itemView.findViewById(R.id.txtCustomerAddress);
      txtCarType = itemView.findViewById(R.id.txtCarType);
      txtDriverMobile = itemView.findViewById(R.id.txtDriverMobile);
      llHeaderStatus = itemView.findViewById(R.id.llHeaderStatus);
    }
  }
}
