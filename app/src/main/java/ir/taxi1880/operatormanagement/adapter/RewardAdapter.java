package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.RewardsModel;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<RewardsModel> rewardsModels;

    public RewardAdapter(ArrayList<RewardsModel> rewardsModels) {
        this.layoutInflater = LayoutInflater.from(MyApplication.context);
        this.rewardsModels = rewardsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_reward, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtComment.setText(rewardsModels.get(position).getComment());
        holder.txtScore.setText(rewardsModels.get(position).getScore()+"");
        holder.txtExpireDate.setText(rewardsModels.get(position).getExpireDate());
        holder.txtExpireTime.setText(rewardsModels.get(position).getexpireTime());
    }

    @Override
    public int getItemCount() {
        return rewardsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtComment;
        TextView txtScore;
        TextView txtExpireDate;
        TextView txtExpireTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComment= itemView.findViewById(R.id.txtRewardComment);
            txtScore= itemView.findViewById(R.id.txtRewardScore);
            txtExpireDate = itemView.findViewById(R.id.txtRewardExpireDate);
            txtExpireTime= itemView.findViewById(R.id.txtRewardExpireTime);
        }
    }

}
