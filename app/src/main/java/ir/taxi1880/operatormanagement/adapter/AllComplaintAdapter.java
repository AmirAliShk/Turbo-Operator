package ir.taxi1880.operatormanagement.adapter;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.CallIncomingActivity;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AllComplaintAdapter extends RecyclerView.Adapter<AllComplaintAdapter.ViewHolder> {

    public static final String TAG = AllComplaintAdapter.class.getSimpleName();
    private Context mContext;
    LocalBroadcastManager broadcaster;
    private ArrayList<AllComplaintsModel> allComplaintsModels;
    ViewFlipper viewFlipper;
    int position;

    public AllComplaintAdapter(ArrayList<AllComplaintsModel> allComplaintsModels) {
        this.allComplaintsModels = allComplaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_all_complaints, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllComplaintsModel model = allComplaintsModels.get(position);

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getDate()));
        holder.txtComplaintDate.setText(StringHelper.toPersianDigits(date) + " ساعت " + model.getTime().substring(0, 5));

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
        RequestHelper.builder(EndPoints.COMPLAINT_ACCEPT)
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
//                    {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"message":"اجازه پذيرش بيشتر از 4 مورد را نداريد","status":0}}
//                     {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"message":"با موفقيت به شما تعلق گرفت","status":1}}
                    if (success) {
                        JSONObject data = obj.getJSONObject("data");
                        int status = data.getInt("status");
                        String message = data.getString("message");
                        if (status == 1) {
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

                                    })
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .secondButton("باشه", null)
                                    .show();
                        }
                    } else {
                        MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
                    AvaCrashReporter.send(e, TAG + " class, getAccept method ");
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
                MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
            });
        }
    };
}