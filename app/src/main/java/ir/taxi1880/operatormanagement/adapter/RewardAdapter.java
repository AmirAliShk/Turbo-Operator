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
import ir.taxi1880.operatormanagement.helper.StringHelper;
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
    holder.txtScore.setText(StringHelper.toPersianDigits(rewardsModels.get(position).getScore() + "  امتیاز"));
    holder.txtExpireDate.setText(StringHelper.toPersianDigits(rewardsModels.get(position).getExpireDate()));
    String subject = rewardsModels.get(position).getSubject();
    if (subject.equals("")) {
      holder.txtSubject.setText("توضیح ندارد");
    } else {
      holder.txtSubject.setText(rewardsModels.get(position).getSubject());
    }
  }

  @Override
  public int getItemCount() {
    return rewardsModels.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView txtComment;
    TextView txtScore;
    TextView txtExpireDate;
    TextView txtSubject;

    ViewHolder(@NonNull View itemView) {
      super(itemView);
      txtComment = itemView.findViewById(R.id.txtTitle);
      txtScore = itemView.findViewById(R.id.txtScore);
      txtExpireDate = itemView.findViewById(R.id.txtExpireDate);
      txtSubject = itemView.findViewById(R.id.txtSubject);
    }
  }

}
