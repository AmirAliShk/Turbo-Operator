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
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.ComplaintDetailFragment;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintDetailsModel;
import ir.taxi1880.operatormanagement.model.PendingComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class PendingComplaintAdapter extends RecyclerView.Adapter<PendingComplaintAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PendingComplaintsModel> pendingComplaintsModels;
    private ArrayList<ComplaintDetailsModel> complaintDetailsModel;
    ViewFlipper vfDetail;

    public PendingComplaintAdapter(Context mContext, ArrayList<PendingComplaintsModel> pendingComplaintsModels) {
        this.mContext = mContext;
        this.pendingComplaintsModels = pendingComplaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.context).inflate(R.layout.item_pending_complaint, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingComplaintsModel model = pendingComplaintsModels.get(position);

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getDate()));
        holder.txtSendDate.setText(StringHelper.toPersianDigits(date) + " ساعت " + model.getTime());
//        holder.txtName.setText(StringHelper.toPersianDigits(model.getName()));
//        holder.txtTell.setText(StringHelper.toPersianDigits(model.getTell()));

        holder.vfDetail.setOnClickListener(view1 -> {
            this.vfDetail = holder.vfDetail;
            if (holder.vfDetail != null) {
                holder.vfDetail.setDisplayedChild(1);
            }
            getAccept(model.getId());
        });

        String status = "#f09a37";
//        int res = R.drawable.ic_call_hire;//todo
//        switch (model.getStatus()) {
//            case 1: //accepted request
//                holder.imgStatus.setVisibility(View.VISIBLE);
////                res = R.drawable.ic_call_hire;
//                status = "#f09a37";
//                break;
//
//            case 2: //waiting for docs
//                holder.imgStatus.setVisibility(View.VISIBLE);
////                res = R.drawable.ic_documents;
//                status = "#3478f6";
//                break;
//
//            case 3: // waiting for confirm
//                holder.imgStatus.setVisibility(View.VISIBLE);
////                res = R.drawable.ic_registration;
//                status = "#10ad79";
//                break;
//        }
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
        return pendingComplaintsModels.size();
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
//            txtTell = itemView.findViewById(R.id.txtTell);
            vfDetail = itemView.findViewById(R.id.vfDetail);

        }
    }

    private void getAccept(int id) {
        RequestHelper.builder(EndPoints.COMPLAINT_DETAIL + id )//todo
                .listener(getAccept)
                .get();
    }

    RequestHelper.Callback getAccept = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                        "status":true, "message":"عملیات با موفقیت انجام شد", data:{
//                        {
//                                saveDate:'1400/02/12'///
//                                saveTime:'15:54   ',///
//                                complaintId:104,///
//                                MoshName:'فاطمه نوري',
//                                ShektypeSharh:'عدم تخفيف به مسافر',
//                                status:2,///
//                                description:' عع',
//                                statusDes:null,
//                                price:50000,
//                                serviceDate:'1400/02/12',
//                                customerPhoneNumber:'9015693808',
//                                Mobile:'9015693808 ',
//                                driverName:null,
//                                driverLastName:null,
//                                driverMobile:'09015693808',
//                                customerAddress:'تست واحد برنامه نويسي ايستگاه 199 ثبت کنيد',
//                                serviceId:28536998,
//                                taxicode:7650
//                        }
//                    }
                    complaintDetailsModel = new ArrayList<ComplaintDetailsModel>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONObject dataObj = listenObj.getJSONObject("data");
                        ComplaintDetailsModel model = new ComplaintDetailsModel();

                        model.setId(dataObj.getInt("id"));//todo it's not complete
                        model.setPassengerName(dataObj.getString("name"));
                        model.setSaveDate(dataObj.getString("saveDate"));
                        model.setSaveTime(dataObj.getString("saveTime"));
                        model.setStatus(dataObj.getInt("status"));

                        if (vfDetail != null)
                            vfDetail.setDisplayedChild(0);

                        FragmentHelper.toFragment(MyApplication.currentActivity, new ComplaintDetailFragment(model)).setNavigationBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryLighter)).replace();
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
