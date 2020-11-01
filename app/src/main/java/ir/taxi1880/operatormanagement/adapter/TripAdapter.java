package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.dataBase.TripModel;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

  ArrayList<TripModel> tripModels;

  @NonNull
  @Override
  public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
    TypefaceUtil.overrideFonts(view);
    return new TripViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
    TripModel tripModel=new TripModel();
    //fill view here
  }

  @Override
  public int getItemCount() {
    return tripModels.size();
  }

  public class TripViewHolder extends RecyclerView.ViewHolder {

    public TripViewHolder(@NonNull View itemView) {
      super(itemView);
    }
  }
}
