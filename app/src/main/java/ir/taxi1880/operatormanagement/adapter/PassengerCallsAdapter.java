package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerCallsModel;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class PassengerCallsAdapter extends RecyclerView.Adapter<PassengerCallsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PassengerCallsModel> passengerCallsModels;


    public PassengerCallsAdapter(Context mContext, ArrayList<PassengerCallsModel> passengerCallsModels) {
        this.mContext = mContext;
        this.passengerCallsModels = passengerCallsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_passenger_calls, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PassengerCallsModel model = passengerCallsModels.get(position);

        holder.txtDate.setText(model.getTxtDate());
        holder.txtTime.setText(model.getTxtTime());
        holder.txtTimeRemaining.setText(model.getTxtTimeRemaining());
    }

    @Override
    public int getItemCount() {
        return passengerCallsModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        TextView txtTimeRemaining;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTimeRemaining = itemView.findViewById(R.id.txtTimeRemaining);

        }
    }
}
