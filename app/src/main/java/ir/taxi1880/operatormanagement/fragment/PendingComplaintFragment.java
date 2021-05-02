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
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

public class PendingComplaintFragment extends Fragment {
    Unbinder unbinder;
    LocalBroadcastManager broadcaster;

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

    PendingComplaintAdapter mAdapter;
    ArrayList<ComplaintsModel> complaintsModels;

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
//        RequestHelper.builder(EndPoints.)//todo
//                .listener(PendingRequestsCallBack)
//                .get();
    }

    RequestHelper.Callback PendingRequestsCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    complaintsModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            ComplaintsModel model = new ComplaintsModel();
                            if (dataObj.getInt("status") == 5 || dataObj.getInt("status") == 6)
                                continue;

                            model.setId(dataObj.getInt("id"));
                            model.setName(dataObj.getString("name"));
                            model.setTell(dataObj.getString("tell"));
                            model.setComment(dataObj.getString("comment"));
                            model.setDate(dataObj.getString("saveDate"));
                            model.setTime(dataObj.getString("saveTime"));
                            model.setCity(dataObj.getInt("cityCode"));
                            model.setJobPosition(dataObj.getString("jobPosition"));
                            model.setStatus(dataObj.getInt("status"));
                            complaintsModels.add(model);
                        }

                        if (complaintsModels.size() == 0) {
                            if (vfPendingComplaint != null)
                                vfPendingComplaint.setDisplayedChild(3);
                        } else {
                            if (vfPendingComplaint != null) {
                                vfPendingComplaint.setDisplayedChild(1);
                                mAdapter = new PendingComplaintAdapter(MyApplication.currentActivity, complaintsModels);
                                complaintsList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_COUNT_PENDING_COMPLAINT);
                        broadcastIntent.putExtra(VALUE_COUNT_PENDING_COMPLAINT, complaintsModels.size());
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
