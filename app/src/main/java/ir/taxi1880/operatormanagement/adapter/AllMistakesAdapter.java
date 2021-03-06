package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class AllMistakesAdapter extends RecyclerView.Adapter<AllMistakesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AllMistakesModel> allMistakesModels;
    DataBase dataBase;
    ViewFlipper viewFlipper;
    int position;

    public AllMistakesAdapter(Context mContext, ArrayList<AllMistakesModel> allMistakesModels) {
        this.mContext = mContext;
        this.allMistakesModels = allMistakesModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_mistakes, parent, false);
        TypefaceUtil.overrideFonts(view);
        dataBase = new DataBase(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllMistakesModel model = allMistakesModels.get(position);

        holder.txtComplaintDate.setText(StringHelper.toPersianDigits(model.getDate()));
        holder.txtComplaintTime.setText(StringHelper.toPersianDigits(model.getTime().substring(0, 5)));
        holder.txtComplaintId.setText("  id:  " + model.getId() + "");
        holder.txtComplaintVoipId.setText("  voipId:  " + model.getVoipId() + "");

        holder.btn.setOnClickListener(view1 -> {
            this.viewFlipper = holder.viewFlipper;
            this.position = position;
            if (holder.viewFlipper != null) {
                holder.viewFlipper.setDisplayedChild(1);
                Log.i(String.valueOf(position), "vf position: " + position);
            }
            Log.i(String.valueOf(position), "btn position: " + position);
            getAccept(allMistakesModels.get(position).getId());
        });
    }

    @Override
    public int getItemCount() {
        return allMistakesModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtComplaintDate;
        TextView txtComplaintTime;
        TextView txtComplaintId;
        TextView txtComplaintVoipId;
        ViewFlipper viewFlipper;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComplaintDate = itemView.findViewById(R.id.txtMistakesDate);
            txtComplaintTime = itemView.findViewById(R.id.txtMistakesTime);
            txtComplaintId = itemView.findViewById(R.id.txtMistakesId);
            txtComplaintVoipId = itemView.findViewById(R.id.txtMistakesVoipId);
            viewFlipper = itemView.findViewById(R.id.vfAccept);
            btn = itemView.findViewById(R.id.btnAccept);

        }
    }

    private void getAccept(int id) {

        RequestHelper.builder(EndPoints.ACCEPT_LISTEN)
                .addParam("listenId", id)
                .listener(getAccept)
                .put();
    }

    RequestHelper.Callback getAccept = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject obj = new JSONObject(args[0].toString());
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (status) {
                            new GeneralDialog()
                                    .message("با موفقیت به لیست در حال بررسی اضافه شد")
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {
                                        dataBase.insertMistakes(allMistakesModels.get(position));
                                        allMistakesModels.remove(position);
                                        notifyDataSetChanged();
                                    })
                                    .show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (viewFlipper != null) {
                    viewFlipper.setDisplayedChild(0);
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (viewFlipper != null) {
                    viewFlipper.setDisplayedChild(0);
                }
            });
        }
    };
}
