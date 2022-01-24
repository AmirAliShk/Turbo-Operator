package ir.taxi1880.operatormanagement.adapter;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PendingComplaintAdapter extends RecyclerView.Adapter<PendingComplaintAdapter.ViewHolder> {
    public static final String TAG = PendingComplaintAdapter.class.getSimpleName();
    private ArrayList<PendingComplaintsModel> pendingComplaintsModels;
    private ArrayList<ComplaintDetailsModel> complaintDetailsModel;
    ViewFlipper vfDetail;
    LocalBroadcastManager broadcaster;

    public PendingComplaintAdapter(ArrayList<PendingComplaintsModel> pendingComplaintsModels) {
        this.pendingComplaintsModels = pendingComplaintsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_pending_complaint, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingComplaintsModel model = pendingComplaintsModels.get(position);

        String date = DateHelper.strPersianTree(DateHelper.parseDate(model.getSaveDate()));
        holder.txtComplaintDate.setText(StringHelper.toPersianDigits(date) + " ساعت " + model.getSaveTime().substring(0, 5));
        holder.txtCustomerName.setText(StringHelper.toPersianDigits(model.getCustomerName()));
        holder.txtComplaintType.setText(StringHelper.toPersianDigits(model.getComplaintType()));

        int res = R.drawable.ic_info_status;
        switch (model.getStatus()) {
            case 1: //accepted request
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_info_status;
                break;

            case 2: //waiting for docs
                holder.imgStatus.setVisibility(View.VISIBLE);
                res = R.drawable.ic_call_status;
                break;

            case 3: // waiting for saveResult
                holder.imgStatus.setVisibility(View.VISIBLE);
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
        ImageView imgStatus;
        ViewFlipper vfDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtComplaintDate = itemView.findViewById(R.id.txtComplaintDate);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtComplaintType = itemView.findViewById(R.id.txtComplaintType);
            vfDetail = itemView.findViewById(R.id.vfDetail);

        }
    }

    private void getAccept(int complaintId) {
        RequestHelper.builder(EndPoints.COMPLAINT_DETAIL + complaintId)
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
                        model.setCarClass(dataObj.getInt("cartype"));
                        model.setCityCode(dataObj.getInt("cityCode"));
                        model.setCountCallCustomer(dataObj.getInt("countCallCustomer"));

                        if (vfDetail != null)
                            vfDetail.setDisplayedChild(0);

                        FragmentHelper.toFragment(MyApplication.currentActivity, new ComplaintDetailFragment(model)).replace();
                    } else {
                        MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
                    }

                    broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

                    Intent broadcastIntent2 = new Intent(KEY_COUNT_PENDING_COMPLAINT);
                    broadcastIntent2.putExtra(VALUE_COUNT_PENDING_COMPLAINT, pendingComplaintsModels.size());
                    broadcaster.sendBroadcast(broadcastIntent2);


                } catch (Exception e) {
                    e.printStackTrace();
                    if (vfDetail != null)
                        vfDetail.setDisplayedChild(0);
                    MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
                    AvaCrashReporter.send(e, TAG + " class, getAccept method ");

                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfDetail != null)
                    vfDetail.setDisplayedChild(0);

                MyApplication.Toast("لطفا دوباره امتحان کنید", Toast.LENGTH_SHORT);
            });
        }
    };
}