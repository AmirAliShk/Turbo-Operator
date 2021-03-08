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
import ir.taxi1880.operatormanagement.model.OperatorRecentCallsModel;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class OperatorRecentCallsAdapter extends RecyclerView.Adapter<OperatorRecentCallsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<OperatorRecentCallsModel> operatorCallsModels;


    public OperatorRecentCallsAdapter(Context mContext, ArrayList<OperatorRecentCallsModel> operatorCallsModels) {
        this.mContext = mContext;
        this.operatorCallsModels = operatorCallsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_operator_recent_calls, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OperatorRecentCallsModel model = operatorCallsModels.get(position);

        holder.txtDate.setText(model.getTxtDate());
        holder.txtTime.setText(model.getTxtTime());
        holder.txtPassengerTell.setText(model.getTxtPassengerTell());
        holder.txtDuration.setText(model.getTxtDuration());
    }

    @Override
    public int getItemCount() {
        return operatorCallsModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        TextView txtPassengerTell;
        TextView txtDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPassengerTell = itemView.findViewById(R.id.txtPassengerTell);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }
}
