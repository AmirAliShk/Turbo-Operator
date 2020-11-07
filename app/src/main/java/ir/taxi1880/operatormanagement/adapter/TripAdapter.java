package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.taxi1880.operatormanagement.R;
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
    TypefaceUtil.overrideFonts(view);
    return new TripViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
    final TripModel tripModel=tripModels.get(position);
    holder.txtCallTime.setText(tripModel.getCallTime());
    holder.txtSendTime.setText(tripModel.getSendTime());
    holder.txtCarType.setText(tripModel.getCarType());
    holder.txtCity.setText(tripModel.getCity());
    holder.txtCustomerAddress.setText(tripModel.getAddress());
    holder.txtCustomerMobile.setText(tripModel.getCustomerMob());
    holder.txtCustomerName.setText(tripModel.getCustomerName());
    holder.txtCustomerTell.setText(tripModel.getCustomerTell());
    holder.txtDriverMobile.setText(tripModel.getDriverMobile());
  }

  @Override
  public int getItemCount() {
    return tripModels.size();
  }

  public class TripViewHolder extends RecyclerView.ViewHolder {
    TextView txtCallTime;
    TextView txtSendTime;
    TextView txtCustomerName;
    TextView txtCustomerTell;
    TextView txtCustomerMobile;
    TextView txtCity;
    TextView txtCustomerAddress;
    TextView txtCarType;
    TextView txtDriverMobile;

    public TripViewHolder(@NonNull View itemView) {
      super(itemView);
      txtCallTime=itemView.findViewById(R.id.txtCallTime);
      txtSendTime=itemView.findViewById(R.id.txtSendTime);
      txtCustomerName=itemView.findViewById(R.id.txtCustomerName);
      txtCustomerTell=itemView.findViewById(R.id.txtCustomerTell);
      txtCustomerMobile=itemView.findViewById(R.id.txtCustomerMobile);
      txtCity=itemView.findViewById(R.id.txtCity);
      txtCustomerAddress=itemView.findViewById(R.id.txtCustomerAddress);
      txtCarType=itemView.findViewById(R.id.txtCarType);
      txtDriverMobile=itemView.findViewById(R.id.txtDriverMobile);
    }
  }
}
