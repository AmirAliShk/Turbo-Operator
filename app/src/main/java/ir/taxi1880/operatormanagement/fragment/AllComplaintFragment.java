package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AllComplaintAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class AllComplaintFragment extends Fragment {
    Unbinder unbinder;

    @BindView(R.id.complaintList)
    ListView complaintList;

    AllComplaintAdapter mAdapter;
    ArrayList<AllComplaintModel> allComplaintModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_complaint, container, false);
        unbinder = ButterKnife.bind(this, view);

        getListen();

        return view;
    }

    private void getListen() {
        RequestHelper.builder(EndPoints.LISTEN)
                .listener(listenCallBack)
                .get();
    }

    RequestHelper.Callback listenCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    allComplaintModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            AllComplaintModel model = new AllComplaintModel();
                            model.setId(dataObj.getInt("id"));
                            model.setServiceCode(dataObj.getInt("serviceCode"));
                            model.setUserCode(dataObj.getInt("userCode"));
                            model.setDate(dataObj.getString("saveDate"));
                            model.setTime(dataObj.getString("saveTime"));
                            model.setDescription(dataObj.getString("Description"));
                            model.setTell(dataObj.getString("tell"));
                            model.setUserCodeContact(dataObj.getInt("userCodeContact"));
                            model.setTypeResult(dataObj.getInt("typeResult")); //TODO check variable type
                            model.setInspectorUser(dataObj.getInt("inspectorUser"));
                            model.setAddress(dataObj.getString("address"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setConDate(dataObj.getString("conDate"));
                            model.setConTime(dataObj.getString("conTime"));
                            model.setSendTime(dataObj.getString("sendTime"));
                            model.setVoipId(dataObj.getString("VoipId"));
                            model.setResult(dataObj.getString("result"));
                            model.setIscheck(dataObj.getBoolean("ischeck"));

                            allComplaintModels.add(model);
                        }

                        mAdapter = new AllComplaintAdapter(MyApplication.currentActivity, allComplaintModels);
                        complaintList.setAdapter(mAdapter);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {

            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
