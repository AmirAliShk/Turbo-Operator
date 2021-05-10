package ir.taxi1880.operatormanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.PendingComplaintAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PendingComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

public class PendingComplaintFragment extends Fragment {
    Unbinder unbinder;
    LocalBroadcastManager broadcaster;

    PendingComplaintAdapter mAdapter;
    ArrayList<PendingComplaintsModel> pendingComplaintsModels;

    @BindView(R.id.refreshPage)
    RecyclerRefreshLayout refreshPage;

    @BindView(R.id.pendingComplaintsList)
    RecyclerView complaintsList;

    @BindView(R.id.vfPendingComplaint)
    ViewFlipper vfPendingComplaint;

    @OnClick(R.id.imgRefresh)
    void onRefresh() {
        getPendingRequests();
    }

    @OnClick(R.id.imgRefreshFail)
    void onRefreshFail() {
        getPendingRequests();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_complaints, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        refreshPage.setOnRefreshListener(() -> getPendingRequests());

        return view;
    }

    private void getPendingRequests() {
        if (vfPendingComplaint != null)
            vfPendingComplaint.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_WEBSERVICE_PATH + 1)// Status = New 0 , admission 1
                .listener(PendingRequestsCallBack)
                .get();
    }

    RequestHelper.Callback PendingRequestsCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    pendingComplaintsModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            PendingComplaintsModel model = new PendingComplaintsModel();
                            if (dataObj.getInt("status") == 5 || dataObj.getInt("status") == 6)
                                continue;
//{"saveDate":"1400/02/12","saveTime":"15:54   ","complaintId":104,"customerName":"فاطمه نوري","complaintType":"عدم تخفيف به مسافر","status":1,"customerId":25098783,"serviceId":28536998}]}

                            model.setSaveDate(dataObj.getString("saveDate"));
                            model.setSaveTime(dataObj.getString("saveTime"));
                            model.setComplaintId(dataObj.getInt("complaintId"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setComplaintType(dataObj.getString("complaintType"));
                            model.setStatus(dataObj.getInt("status"));
                            model.setServiceId(dataObj.getInt("serviceId"));

                            pendingComplaintsModels.add(model);
                        }

                        if (pendingComplaintsModels.size() == 0) {
                            if (vfPendingComplaint != null)
                                vfPendingComplaint.setDisplayedChild(3);
                        } else {
                            if (vfPendingComplaint != null) {
                                vfPendingComplaint.setDisplayedChild(1);
                                mAdapter = new PendingComplaintAdapter(MyApplication.currentActivity, pendingComplaintsModels);
                                complaintsList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_COUNT_PENDING_COMPLAINT);
                        broadcastIntent.putExtra(VALUE_COUNT_PENDING_COMPLAINT, pendingComplaintsModels.size());
                        broadcaster.sendBroadcast(broadcastIntent);

                    } else {
                        if (vfPendingComplaint != null)
                            vfPendingComplaint.setDisplayedChild(2);
                    }

                } catch (Exception e) {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    if (vfPendingComplaint != null)
                        vfPendingComplaint.setDisplayedChild(2);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (refreshPage != null)
                    refreshPage.setRefreshing(false);
                if (vfPendingComplaint != null)
                    vfPendingComplaint.setDisplayedChild(2);
            });
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPendingRequests();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
