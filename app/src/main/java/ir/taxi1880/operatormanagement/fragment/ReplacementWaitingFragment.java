package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ReplacementWaitingAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ReplacementModel;

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
public class ReplacementWaitingFragment extends Fragment {
    public static final String TAG = ReplacementWaitingAdapter.class.getSimpleName();
    Unbinder unbinder;
    ArrayList<ReplacementModel> replacementModels;
    ReplacementWaitingAdapter replacementWaitingAdapter;

    @BindView(R.id.listReplacement)
    ListView listReplacement;

    @BindView(R.id.vfGetReq)
    ViewFlipper vfGetReq;

    @BindView(R.id.txtNull)
    TextView txtNull;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replacement_waiting, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        getShiftReplacementRequest(MyApplication.prefManager.getUserCode());

        return view;
    }

    private void getShiftReplacementRequest(int operatorId) {
        vfGetReq.setDisplayedChild(0);
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
                        int status = object.getInt("status");
                        if (type == 1 && status == 0) {
                            replacementModel.setReplaceStatus(status);
                            replacementModel.setReplaceType(type);
                            replacementModel.setReplaceId(object.getInt("id"));
                            replacementModel.setReplaceDate(object.getString("date"));
                            replacementModel.setReplaceShiftName(object.getString("shiftname"));
                            replacementModel.setReplaceOperatorName(object.getString("lastName"));
                            replacementModel.setStatusStr(object.getString("statusStr"));
                            replacementModels.add(replacementModel);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                vfGetReq.setDisplayedChild(1);
                replacementWaitingAdapter = new ReplacementWaitingAdapter(replacementModels, MyApplication.context, position -> {
                    replacementModels.remove(position);
                    replacementWaitingAdapter.notifyDataSetChanged();
                });
                listReplacement.setAdapter(replacementWaitingAdapter);
                if (replacementModels.size() == 0) {
                    vfGetReq.setDisplayedChild(2);
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
