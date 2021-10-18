package ir.taxi1880.operatormanagement.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class AllMistakesAdapter extends RecyclerView.Adapter<AllMistakesAdapter.ViewHolder> {
    LocalBroadcastManager broadcaster;
    private ArrayList<AllMistakesModel> allMistakesModels;
    DataBase dataBase;
    ViewFlipper viewFlipper;
    int position;

    public AllMistakesAdapter(ArrayList<AllMistakesModel> allMistakesModels) {
        this.allMistakesModels = allMistakesModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_all_mistakes, parent, false);
        TypefaceUtil.overrideFonts(view);
        dataBase = new DataBase(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllMistakesModel model = allMistakesModels.get(position);

        holder.txtComplaintDate.setText(StringHelper.toPersianDigits(model.getDate()));
        holder.txtComplaintTime.setText(StringHelper.toPersianDigits(model.getTime().substring(0, 5)));

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
        ViewFlipper viewFlipper;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComplaintDate = itemView.findViewById(R.id.txtMistakesDate);
            txtComplaintTime = itemView.findViewById(R.id.txtMistakesTime);
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
                    Log.i("TAF",obj.toString());
                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        int status = data.getInt("status");
                        if (status) {
                            new GeneralDialog()
                                    .message("با موفقیت به لیست در حال بررسی اضافه شد")
                                    .cancelable(false)
                                    .firstButton("باشه", () -> {
                                        dataBase.insertMistakes(allMistakesModels.get(position));
                                        allMistakesModels.remove(position);
                                        notifyDataSetChanged();

                                        broadcaster = LocalBroadcastManager.getInstance(context);

                                        Intent broadcastIntent1 = new Intent(KEY_PENDING_MISTAKE_COUNT);
                                        broadcastIntent1.putExtra(PENDING_MISTAKE_COUNT, dataBase.getMistakesCount());
                                        broadcaster.sendBroadcast(broadcastIntent1);

                                        Intent broadcastIntent2 = new Intent(KEY_NEW_MISTAKE_COUNT);
                                        broadcastIntent2.putExtra(NEW_MISTAKE_COUNT, allMistakesModels.size());
                                        broadcaster.sendBroadcast(broadcastIntent2);

                                    })
                                    .show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e,"AllMistakesAdapter,getAccept");
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
