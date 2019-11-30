package com.example.operatormanagement.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.operatormanagement.OkHttp.RequestHelper;
import com.example.operatormanagement.R;
import com.example.operatormanagement.adapter.ReplacementWaitingAdapter;
import com.example.operatormanagement.adapter.SendReplacementAdapter;
import com.example.operatormanagement.app.EndPoints;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.ReplacementModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendReplacementReqFragment extends Fragment {
    Unbinder unbinder;
    ArrayList<ReplacementModel> replacementModels;
    SendReplacementAdapter sendReplacementAdapter;

    @BindView(R.id.listReplacement)
    ListView listReplacement;

    @BindView(R.id.vfSendReq)
    ViewFlipper vfSendReq;

    @BindView(R.id.txtNull)
    TextView txtNull;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_replacement_req, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        getShiftReplacementRequest(MyApplication.prefManager.getUserCode());

        return view;
    }

    private void getShiftReplacementRequest(int operatorId) {
        vfSendReq.setDisplayedChild(0);
        JSONObject params = new JSONObject();
        try {
            params.put("operatorId", operatorId);

            RequestHelper.builder(EndPoints.GET_SHIFT_REPLACEMENT_REQUESTS)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onGetShiftReplacementRequest)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RequestHelper.Callback onGetShiftReplacementRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    replacementModels = new ArrayList<>();
                    JSONArray arr = new JSONArray(args[0].toString());
                    MyApplication.prefManager.setRequestList(arr.toString());

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        ReplacementModel replacementModel = new ReplacementModel();
                        int type = object.getInt("type");
                        if (type == 2) {
                            replacementModel.setReplaceType(object.getInt("type"));
                            replacementModel.setReplaceId(object.getInt("id"));
                            replacementModel.setReplaceDate(object.getString("date"));
                            replacementModel.setReplaceShiftName(object.getString("shiftname"));
                            replacementModel.setReplaceOperatorName(object.getString("lastName"));
                            replacementModel.setStatusStr(object.getString("statusStr"));
                            replacementModel.setReplaceStatus(object.getInt("status"));
                            replacementModel.setReplaceOperatorNameChange(object.getString("lastnameChange"));
                            replacementModels.add(replacementModel);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                vfSendReq.setDisplayedChild(1);
                sendReplacementAdapter = new SendReplacementAdapter(replacementModels, MyApplication.context, position -> {
                    replacementModels.remove(position);
                    sendReplacementAdapter.notifyDataSetChanged();
                });
                listReplacement.setAdapter(sendReplacementAdapter);
                if (replacementModels.size() == 0) {
                   vfSendReq.setDisplayedChild(2);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
