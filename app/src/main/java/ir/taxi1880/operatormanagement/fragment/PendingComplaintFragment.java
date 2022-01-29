package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.PendingComplaintAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentPendingComplaintsBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PendingComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PendingComplaintFragment extends Fragment {
    public static final String TAG = PendingComplaintFragment.class.getSimpleName();
    FragmentPendingComplaintsBinding binding;
    LocalBroadcastManager broadcaster;
    PendingComplaintAdapter mAdapter;
    ArrayList<PendingComplaintsModel> pendingComplaintsModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPendingComplaintsBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        getPendingRequests();

        binding.refreshPage.setOnRefreshListener(this::getPendingRequests);

        binding.imgRefreshFail.setOnClickListener(view -> getPendingRequests());

        binding.imgRefresh.setOnClickListener(view -> getPendingRequests());

        return binding.getRoot();
    }

    private void getPendingRequests() {
        if (binding.vfPendingComplaint != null)
            binding.vfPendingComplaint.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_WEBSERVICE_PATH + 1)// Status = New 0 , admission 1
                .listener(PendingRequestsCallBack)
                .get();
    }

    RequestHelper.Callback PendingRequestsCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (binding.refreshPage != null)
                        binding.refreshPage.setRefreshing(false);
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
                            if (binding.vfPendingComplaint != null)
                                binding.vfPendingComplaint.setDisplayedChild(3);
                        } else {
                            if (binding.vfPendingComplaint != null) {
                                binding.vfPendingComplaint.setDisplayedChild(1);
                                mAdapter = new PendingComplaintAdapter(pendingComplaintsModels);
                                binding.pendingComplaintsList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

                        Intent broadcastIntent2 = new Intent(KEY_COUNT_PENDING_COMPLAINT);
                        broadcastIntent2.putExtra(VALUE_COUNT_PENDING_COMPLAINT, pendingComplaintsModels.size());
                        broadcaster.sendBroadcast(broadcastIntent2);
                    } else {
                        if (binding.vfPendingComplaint != null)
                            binding.vfPendingComplaint.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    if (binding.refreshPage != null)
                        binding.refreshPage.setRefreshing(false);
                    if (binding.vfPendingComplaint != null)
                        binding.vfPendingComplaint.setDisplayedChild(2);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, PendingRequestsCallBack onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.refreshPage != null)
                    binding.refreshPage.setRefreshing(false);
                if (binding.vfPendingComplaint != null)
                    binding.vfPendingComplaint.setDisplayedChild(2);
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getPendingRequests();
    }
}