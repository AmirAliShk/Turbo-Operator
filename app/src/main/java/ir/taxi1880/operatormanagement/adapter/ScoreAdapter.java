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
import ir.taxi1880.operatormanagement.model.ScoreModel;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<ScoreModel> scoreModels;

    public ScoreAdapter(ArrayList<ScoreModel> scoreModels) {
        this.layoutInflater = LayoutInflater.from(MyApplication.currentActivity);
        this.scoreModels = scoreModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_score, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtHour.setText(StringHelper.toPersianDigits(scoreModels.get(position).getHour() + ":00"));
        holder.txtScore.setText(StringHelper.toPersianDigits(scoreModels.get(position).getScore() + ""));
    }

    @Override
    public int getItemCount() {
        return scoreModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtScore;
        TextView txtHour;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtHour = itemView.findViewById(R.id.txtHour);
        }
    }

}

