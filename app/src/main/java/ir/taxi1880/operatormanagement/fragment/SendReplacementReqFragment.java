package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.SendReplacementAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentSendReplacementReqBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ReplacementModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class SendReplacementReqFragment extends Fragment {
    public static final String TAG = SendReplacementReqFragment.class.getSimpleName();
    FragmentSendReplacementReqBinding binding;
    ArrayList<ReplacementModel> replacementModels;
    SendReplacementAdapter sendReplacementAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendReplacementReqBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        getShiftReplacementRequest();

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    private void getShiftReplacementRequest() {
        if (binding.vfSendReq != null)
            binding.vfSendReq.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.GET_SHIFT_REPLACEMENT_REQUESTS)
                .listener(onGetShiftReplacementRequest)
                .get();
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
                    if (binding.vfSendReq != null)
                        binding.vfSendReq.setDisplayedChild(1);
                    sendReplacementAdapter = new SendReplacementAdapter(replacementModels, position -> {
                        replacementModels.remove(position);
                        sendReplacementAdapter.notifyDataSetChanged();
                    });
                    if (binding.listReplacement != null)
                        binding.listReplacement.setAdapter(sendReplacementAdapter);
                    if (replacementModels.size() == 0) {
                        if (binding.vfSendReq != null)
                            binding.vfSendReq.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    if (binding.vfSendReq != null)
                        binding.vfSendReq.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetShiftReplacementRequest onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfSendReq != null)
                    binding.vfSendReq.setDisplayedChild(3);
            });
        }
    };
}