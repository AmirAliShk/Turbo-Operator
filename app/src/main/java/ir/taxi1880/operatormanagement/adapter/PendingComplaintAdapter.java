package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.ComplaintDetailFragment;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class PendingComplaintAdapter extends RecyclerView.Adapter<PendingComplaintAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ComplaintsModel> complaintsModels;
    ViewFlipper vfDetail;

    public PendingComplaintAdapter(Context mContext, ArrayList<ComplaintsModel> pendingComplaintsModels) {
        this.mContext = mContext;
        this.complaintsModels = pendingComplaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.context).inflate(R.layout.item_pending_complaint, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintsModel model = complaintsModels.get(position);

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getDate()));
        holder.txtSendDate.setText(StringHelper.toPersianDigits(date));
        holder.txtName.setText(StringHelper.toPersianDigits(model.getName()));
        holder.txtTell.setText(StringHelper.toPersianDigits(model.getTell()));

        holder.vfDetail.setOnClickListener(view1 -> {
            this.vfDetail = holder.vfDetail;
            if (holder.vfDetail != null) {
                holder.vfDetail.setDisplayedChild(1);
            }
            getAccept(model.getId());
        });

//        0 'جدید'
//        1 'پذیرش شده'
//        2 'منتظر مدارک'
//        3 'در انتظار تایید'
//        4 'تایید شده'
//        5 'حذف یا رد شده'

        String status = "#f09a37";
//        int res = R.drawable.ic_call_hire;//todo
        switch (model.getStatus()) {
            case 1: //accepted request
                holder.imgStatus.setVisibility(View.VISIBLE);
//                res = R.drawable.ic_call_hire;
                status = "#f09a37";
                break;

            case 2: //waiting for docs
                holder.imgStatus.setVisibility(View.VISIBLE);
//                res = R.drawable.ic_documents;
                status = "#3478f6";
                break;

            case 3: // waiting for confirm
                holder.imgStatus.setVisibility(View.VISIBLE);
//                res = R.drawable.ic_registration;
                status = "#10ad79";
                break;
        }
//        holder.imgStatus.setImageResource(res);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable bg_btn_disable = AppCompatResources.getDrawable(mContext, R.drawable.bg_btn_disable);
            DrawableCompat.setTint(bg_btn_disable, Color.parseColor(status));
            holder.imgStatus.setBackground(bg_btn_disable);
        } else {
            holder.imgStatus.setBackgroundColor(Color.parseColor(status));
        }
    }

    @Override
    public int getItemCount() {
        return complaintsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSendDate;
        TextView txtName;
        TextView txtTell;
        LinearLayout llPendingComplaint;
        ImageView imgStatus;
        ViewFlipper vfDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llPendingComplaint = itemView.findViewById(R.id.llPendingComplaint);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtSendDate = itemView.findViewById(R.id.txtSendDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtTell = itemView.findViewById(R.id.txtTell);
            vfDetail = itemView.findViewById(R.id.vfDetail);

        }
    }

    private void getAccept(int id) {
//        RequestHelper.builder(EndPoints. )//todo
//                .listener(getAccept)
//                .get();
    }

    RequestHelper.Callback getAccept = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    complaintsModels = new ArrayList<ComplaintsModel>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONObject dataObj = listenObj.getJSONObject("data");
                        ComplaintsModel model = new ComplaintsModel();

//                        {
//                            "id":4207,
//                                "name":"بهبودي فر ",
//                                "comment":"فرهاد بهروزي",
//                                "admissionUser":329,
//                                "cityCode":4,
//                                "saveDate":"1400/02/06",
//                                "saveTime":"11:59:31",
//                                "status":1,
//                                "tell":"9106960852",
//                                "type":6,
//                                "userId":340,
//                                "jobPosition":"راننده",
//                                "admissionPersianDate":"1400/02/06",
//                                "CityName":"کاشمر"
//                        }

                        model.setId(dataObj.getInt("id"));
                        model.setName(dataObj.getString("name"));
                        model.setComment(dataObj.getString("comment"));
                        model.setCity(dataObj.getInt("cityCode"));
                        model.setDate(dataObj.getString("saveDate"));
                        model.setTime(dataObj.getString("saveTime"));
                        model.setStatus(dataObj.getInt("status"));
                        model.setTell(dataObj.getString("tell"));
                        model.setJobPosition(dataObj.getString("jobPosition"));

                        if (vfDetail != null)
                            vfDetail.setDisplayedChild(0);

                        FragmentHelper.toFragment(MyApplication.currentActivity, new ComplaintDetailFragment(model)).setNavigationBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimary)).replace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (vfDetail != null)
                        vfDetail.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfDetail != null)
                    vfDetail.setDisplayedChild(0);
            });
        }
    };
}
