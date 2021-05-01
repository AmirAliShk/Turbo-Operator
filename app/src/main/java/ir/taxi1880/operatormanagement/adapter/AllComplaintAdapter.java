package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;

public class AllComplaintAdapter extends RecyclerView.Adapter<AllComplaintAdapter.ViewHolder> {
    private Context mContext;
    LocalBroadcastManager broadcaster;
    private ArrayList<ComplaintsModel> complaintsModels;
    ViewFlipper viewFlipper;
    int position;

    public AllComplaintAdapter(Context mContext, ArrayList<ComplaintsModel> complaintsModels) {
        this.mContext = mContext;
        this.complaintsModels = complaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_complaints, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintsModel model = complaintsModels.get(position);

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
        return complaintsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewFlipper viewFlipper;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewFlipper = itemView.findViewById(R.id.vfAccept);
            btn = itemView.findViewById(R.id.btnAccept);

        }
    }

    private void getAccept(int id) {
//        RequestHelper.builder(EndPoints.)//todo
//                .addParam("id", id)
//                .listener(getAccept)
//                .put();
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
//                        JSONObject data = obj.getJSONObject("data");
//                        boolean status = data.getBoolean("status");

//                        if (status) {
                        new GeneralDialog()
                                .message(message)
                                .cancelable(false)
                                .firstButton("باشه", () -> {
                                    if (position != -1)
                                        complaintsModels.remove(position);

                                    notifyDataSetChanged();

                                    broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

                                    Intent broadcastIntent1 = new Intent(KEY_COUNT_ALL_COMPLAINT);
                                    broadcastIntent1.putExtra(KEY_COUNT_ALL_COMPLAINT, complaintsModels.size());
                                    broadcaster.sendBroadcast(broadcastIntent1);

//                                        Intent broadcastIntent2 = new Intent(KEY_COUNT_PENDING_HIRE);
//                                        broadcastIntent2.putExtra(KEY_COUNT_PENDING_HIRE, hiresModels.size());
//                                        broadcaster.sendBroadcast(broadcastIntent2);

                                })
                                .show();
//                        } else {
//                            new GeneralDialog()
//                                    .message(message)
//                                    .cancelable(false)
//                                    .type(3)
//                                    .firstButton("باشه", null)
//                                    .show();
//                        }
                    } else {
                        new GeneralDialog()
                                .message(message)
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
