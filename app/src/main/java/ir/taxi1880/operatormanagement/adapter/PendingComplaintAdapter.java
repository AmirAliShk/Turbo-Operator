package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
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

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getSaveDate()));
        holder.txtComplaintDate.setText(StringHelper.toPersianDigits(date) + " ساعت " + model.getSaveTime());
        holder.txtCustomerName.setText(StringHelper.toPersianDigits(model.getCustomerName()));
        holder.txtComplaintType.setText(StringHelper.toPersianDigits(model.getComplaintType()));

        int res = R.drawable.ic_info_status;
        switch (model.getStatus()) {
            case 1: //accepted request
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.txtComplaintStatus.setText("اطلاعات سفر");
                res = R.drawable.ic_info_status;
                break;

            case 2: //waiting for docs
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.txtComplaintStatus.setText("تماس");
                res = R.drawable.ic_call_status;
                break;

            case 3: // waiting for saveResult
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.txtComplaintStatus.setText("نتیجه‌گیری");
                res = R.drawable.ic_conclusion_status;
                break;
        }
        holder.imgStatus.setImageResource(res);


        holder.vfDetail.setOnClickListener(view1 -> {
            this.vfDetail = holder.vfDetail;
            if (holder.vfDetail != null) {
                holder.vfDetail.setDisplayedChild(1);
            }
            getAccept(model.getComplaintId());
        });

    }

    @Override
    public int getItemCount() {
        return pendingComplaintsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtComplaintDate;
        TextView txtCustomerName;
        TextView txtComplaintType;
        TextView txtComplaintStatus;
        ImageView imgStatus;
        ViewFlipper vfDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtComplaintStatus = itemView.findViewById(R.id.txtComplaintStatus);
            txtComplaintDate = itemView.findViewById(R.id.txtComplaintDate);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtComplaintType = itemView.findViewById(R.id.txtComplaintType);
            vfDetail = itemView.findViewById(R.id.vfDetail);

        }
    }

    private void getAccept(int id) {
        RequestHelper.builder(EndPoints.COMPLAINT_DETAIL + id)//todo
                .listener(getAccept)
                .get();
    }

    RequestHelper.Callback getAccept = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {

//{"saveDate":"1400/02/12","saveTime":"15:54   ","complaintId":104,"customerName":"فاطمه نوري","complaintType":"عدم تخفيف به مسافر","status":1,"statusDes":"در حال بررسی","price":50000,"serviceDate":"1400/02/12","customerPhoneNumber":"9015693808",
// "Mobile":"9015693808 ","driverName":"فاطمه","driverLastName":"نوري","driverMobile":"09015693808","driverMobile2":"09015693808","customerAddress":"تست واحد برنامه نويسي ايستگاه 199 ثبت کنيد",
// "serviceId":28536998,"taxicode":7650,"serviceVoipId":"0","complaintVoipId":"0"}}

                    complaintDetailsModel = new ArrayList<ComplaintDetailsModel>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONObject dataObj = listenObj.getJSONObject("data");
                        ComplaintDetailsModel model = new ComplaintDetailsModel();

                        model.setSaveDate(dataObj.getString("saveDate"));
                        model.setSaveTime(dataObj.getString("saveTime"));
                        model.setComplaintId(dataObj.getInt("complaintId"));
                        model.setCustomerName(dataObj.getString("customerName"));
                        model.setComplaintType(dataObj.getString("complaintType"));
                        model.setStatus(dataObj.getInt("status"));
                        model.setPrice(dataObj.getInt("price"));
                        model.setServiceDate(dataObj.getString("serviceDate"));
                        model.setCustomerPhoneNumber(dataObj.getString("customerPhoneNumber"));
                        model.setCustomerMobileNumber(dataObj.getString("Mobile"));
                        model.setDriverName(dataObj.getString("driverName"));
                        model.setDriverLastName(dataObj.getString("driverLastName"));
                        model.setDriverMobile(dataObj.getString("driverMobile"));
                        model.setDriverMobile2(dataObj.getString("driverMobile2"));
                        model.setAddress(dataObj.getString("customerAddress"));
                        model.setServiceId(dataObj.getInt("serviceId"));
                        model.setTaxicode(dataObj.getInt("taxicode"));
                        model.setServiceVoipId(dataObj.getString("serviceVoipId"));
                        model.setComplaintVoipId(dataObj.getString("complaintVoipId"));

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
