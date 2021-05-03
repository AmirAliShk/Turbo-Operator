package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.content.Intent;
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
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;

public class AllComplaintAdapter extends RecyclerView.Adapter<AllComplaintAdapter.ViewHolder> {
    private Context mContext;
    LocalBroadcastManager broadcaster;
    private ArrayList<AllComplaintsModel> allComplaintsModels;
    ViewFlipper viewFlipper;
    int position;

    public AllComplaintAdapter(Context mContext, ArrayList<AllComplaintsModel> allComplaintsModels) {
        this.mContext = mContext;
        this.allComplaintsModels = allComplaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_complaints, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllComplaintsModel model = allComplaintsModels.get(position);

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getDate()));
        holder.txtComplaintDate.setText("تاریخ " + StringHelper.toPersianDigits(date) + " ساعت " + model.getTime());

        holder.btn.setOnClickListener(view1 -> {
            this.viewFlipper = holder.viewFlipper;
            this.position = position;
            if (holder.viewFlipper != null) {
                holder.viewFlipper.setDisplayedChild(1);
            }
            getAccept(model.getId());
        });
    }

    @Override
    public int getItemCount() {
        return allComplaintsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewFlipper viewFlipper;
        Button btn;
        TextView txtComplaintDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewFlipper = itemView.findViewById(R.id.vfAccept);
            btn = itemView.findViewById(R.id.btnAccept);
            txtComplaintDate = itemView.findViewById(R.id.txtComplaintDate);

        }
    }

    private void getAccept(int id) {
        RequestHelper.builder(EndPoints.COMPLAINT_ACCEPT)//todo
                .addParam("complaintId", id)
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
//                    String message = obj.getString("message");

                    if (success) {
                        new GeneralDialog()
                                .message("با موفقیت به لیست شما اضافه شد.")
                                .cancelable(false)
                                .firstButton("باشه", () -> {
                                    if (position != -1)
                                        allComplaintsModels.remove(position);

                                    notifyDataSetChanged();

                                    broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

                                    Intent broadcastIntent1 = new Intent(KEY_COUNT_ALL_COMPLAINT);
                                    broadcastIntent1.putExtra(VALUE_COUNT_ALL_COMPLAINT, allComplaintsModels.size());
                                    broadcaster.sendBroadcast(broadcastIntent1);

//                                        Intent broadcastIntent2 = new Intent(KEY_COUNT_PENDING_HIRE);
//                                        broadcastIntent2.putExtra(KEY_COUNT_PENDING_HIRE, hiresModels.size());
//                                        broadcaster.sendBroadcast(broadcastIntent2);

                                })
                                .show();
                    } else {
                        new GeneralDialog()
                                .message("message")//todo
                                .cancelable(false)
                                .secondButton("باشه", null)
                                .show();
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
