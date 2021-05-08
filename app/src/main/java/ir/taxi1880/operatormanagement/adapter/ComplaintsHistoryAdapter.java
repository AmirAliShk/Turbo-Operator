package ir.taxi1880.operatormanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.Date;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsHistoryModel;
import ir.taxi1880.operatormanagement.model.RecentCallsModel;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class ComplaintsHistoryAdapter extends RecyclerView.Adapter<ComplaintsHistoryAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<ComplaintsHistoryModel> complaintsHistoryModels;
    View view;

    public ComplaintsHistoryAdapter(Context mContext, ArrayList<ComplaintsHistoryModel> complaintsHistoryModels) {
        this.mContext = mContext;
        this.complaintsHistoryModels = complaintsHistoryModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_complaint_history, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ComplaintsHistoryAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        TextView txtComplaintType;
        TextView txtName;
        TextView txtComplaintStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtComplaintType = itemView.findViewById(R.id.txtComplaintType);
            txtName = itemView.findViewById(R.id.txtName);
            txtComplaintStatus = itemView.findViewById(R.id.txtComplaintStatus);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsHistoryAdapter.ViewHolder holder, int position) {
        ComplaintsHistoryModel model = complaintsHistoryModels.get(position);
        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getSaveDate()));
        holder.txtDate.setText(date);
        holder.txtTime.setText(model.getSaveTime());
        holder.txtComplaintType.setText(model.getComplaintType());
        holder.txtName.setText(model.getCustomerName());
        holder.txtComplaintStatus.setText(model.getTypeResultDes());

    }

    @Override
    public int getItemCount() {
        return complaintsHistoryModels.size();
    }
}
