package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsHistoryModel;

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
        TextView txtComplaintType;
        TextView txtName;
        TextView txtCulprit;
        ImageView imgStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtComplaintType = itemView.findViewById(R.id.txtComplaintType);
            txtName = itemView.findViewById(R.id.txtName);
            txtCulprit = itemView.findViewById(R.id.txtCulprit);
            imgStatus = itemView.findViewById(R.id.imgStatus);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsHistoryAdapter.ViewHolder holder, int position) {
        ComplaintsHistoryModel model = complaintsHistoryModels.get(position);
        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getSaveDate()));
        holder.txtDate.setText(date + " ساعت " + model.getSaveTime());
        holder.txtComplaintType.setText(model.getComplaintType());
        holder.txtName.setText(model.getCustomerName());
        holder.txtCulprit.setText(model.getTypeResultDes());

        int res = R.drawable.ic_new_complaint;
        switch (model.getStatus()) {
            case 0: //new complaint
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_new_complaint;
                break;

            case 1: //accepted complaint
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_info_status;
                break;

            case 2: //waiting for call
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_call_status;
                break;

            case 3: // waiting for result
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_conclusion_status;
                break;

            case 4: // ended
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_finished_complaint;
                break;
        }
        holder.imgStatus.setImageResource(res);
    }

    @Override
    public int getItemCount() {
        return complaintsHistoryModels.size();
    }
}
