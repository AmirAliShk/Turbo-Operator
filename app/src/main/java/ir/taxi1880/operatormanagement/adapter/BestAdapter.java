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
import ir.taxi1880.operatormanagement.model.BestModel;

public class BestAdapter extends RecyclerView.Adapter<BestAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<BestModel> bestModels;

    public BestAdapter(ArrayList<BestModel> bestModels) {
        this.layoutInflater = LayoutInflater.from(MyApplication.context);
        this.bestModels = bestModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_best, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtScore.setText(StringHelper.toPersianDigits(bestModels.get(position).getScore() + ""));
        holder.txtName.setText(StringHelper.toPersianDigits(bestModels.get(position).getRowNumber() + "") + " .  " + bestModels.get(position).getName() + " " + bestModels.get(position).getLastName());
    }

    @Override
    public int getItemCount() {
        return bestModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtScore;
        TextView txtName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }

}
