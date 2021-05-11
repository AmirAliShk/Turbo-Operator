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
import ir.taxi1880.operatormanagement.adapter.AllComplaintAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;

public class AllComplaintFragment extends Fragment {
    Unbinder unbinder;
    LocalBroadcastManager broadcaster;

    AllComplaintAdapter mAdapter;
    ArrayList<AllComplaintsModel> allComplaintsModels;

    @BindView(R.id.refreshPage)
    RecyclerRefreshLayout refreshPage;

    @BindView(R.id.allComplaintsList)
    RecyclerView complaintsList;

    @BindView(R.id.vfAllComplaint)
    ViewFlipper vfAllComplaint;

    @OnClick(R.id.imgRefresh)
    void onRefresh() {
        getAllComplaints();
    }

    @OnClick(R.id.imgRefreshFail)
    void onRefreshFail() {
        getAllComplaints();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_complaints, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        refreshPage.setOnRefreshListener(() -> getAllComplaints());

        return view;
    }

    private void getAllComplaints() {
        if (vfAllComplaint != null)
            vfAllComplaint.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.COMPLAINT_WEBSERVICE_PATH + 0)// Status = New 0 , admission 1
                .listener(allComplaintsRequestCallBack)
                .get();
    }

    RequestHelper.Callback allComplaintsRequestCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
//                    { saveDate: '1400/02/11', saveTime: '15:29   ', id: 1 }

                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    allComplaintsModels = new ArrayList<AllComplaintsModel>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfAllComplaint != null)
                            vfAllComplaint.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            AllComplaintsModel model = new AllComplaintsModel();

                            model.setId(dataObj.getInt("id"));
                            model.setDate(dataObj.getString("saveDate"));
                            model.setTime(dataObj.getString("saveTime"));

                            allComplaintsModels.add(model);
                        }

                        if (allComplaintsModels.size() == 0) {
                            if (vfAllComplaint != null)
                                vfAllComplaint.setDisplayedChild(3);
                        } else {
                            if (vfAllComplaint != null) {
                                vfAllComplaint.setDisplayedChild(1);
                                mAdapter = new AllComplaintAdapter(MyApplication.currentActivity, allComplaintsModels);
                                complaintsList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_COUNT_ALL_COMPLAINT);
                        broadcastIntent.putExtra(VALUE_COUNT_ALL_COMPLAINT, allComplaintsModels.size());
                        broadcaster.sendBroadcast(broadcastIntent);

                    } else {
                        if (vfAllComplaint != null)
                            vfAllComplaint.setDisplayedChild(2);
                    }

                } catch (Exception e) {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    if (vfAllComplaint != null)
                        vfAllComplaint.setDisplayedChild(2);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (refreshPage != null)
                    refreshPage.setRefreshing(false);
                if (vfAllComplaint != null)
                    vfAllComplaint.setDisplayedChild(2);
            });
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllComplaints();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
