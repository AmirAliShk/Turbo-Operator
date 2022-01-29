package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;

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

import ir.taxi1880.operatormanagement.adapter.AllComplaintAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentAllComplaintsBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class AllComplaintFragment extends Fragment {
    public static final String TAG = AllComplaintFragment.class.getSimpleName();
    FragmentAllComplaintsBinding binding;
    LocalBroadcastManager broadcaster;
    AllComplaintAdapter mAdapter;
    ArrayList<AllComplaintsModel> allComplaintsModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllComplaintsBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        binding.refreshPage.setOnRefreshListener(this::getAllComplaints);

        binding.imgRefresh.setOnClickListener(view -> getAllComplaints());

        binding.imgRefreshFail.setOnClickListener(view -> getAllComplaints());

        return binding.getRoot();
    }

    private void getAllComplaints() {
        if (binding.vfAllComplaint != null)
            binding.vfAllComplaint.setDisplayedChild(0);
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

                    if (binding.refreshPage != null)
                        binding.refreshPage.setRefreshing(false);
                    allComplaintsModels = new ArrayList<AllComplaintsModel>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (binding.vfAllComplaint != null)
                            binding.vfAllComplaint.setDisplayedChild(1);
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
                            if (binding.vfAllComplaint != null)
                                binding.vfAllComplaint.setDisplayedChild(3);
                        } else {
                            if (binding.vfAllComplaint != null) {
                                binding.vfAllComplaint.setDisplayedChild(1);
                                mAdapter = new AllComplaintAdapter(allComplaintsModels);
                                binding.allComplaintsList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_COUNT_ALL_COMPLAINT);
                        broadcastIntent.putExtra(VALUE_COUNT_ALL_COMPLAINT, allComplaintsModels.size());
                        broadcaster.sendBroadcast(broadcastIntent);

                    } else {
                        if (binding.vfAllComplaint != null)
                            binding.vfAllComplaint.setDisplayedChild(2);
                    }

                } catch (Exception e) {
                    if (binding.refreshPage != null)
                        binding.refreshPage.setRefreshing(false);
                    if (binding.vfAllComplaint != null)
                        binding.vfAllComplaint.setDisplayedChild(2);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, allComplaintsRequestCallBack method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.refreshPage != null)
                    binding.refreshPage.setRefreshing(false);
                if (binding.vfAllComplaint != null)
                    binding.vfAllComplaint.setDisplayedChild(2);
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getAllComplaints();
    }
}