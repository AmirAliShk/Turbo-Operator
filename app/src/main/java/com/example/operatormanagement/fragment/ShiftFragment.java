package com.example.operatormanagement.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.operatormanagement.OkHttp.RequestHelper;
import com.example.operatormanagement.R;
import com.example.operatormanagement.adapter.ShiftAdapter;
import com.example.operatormanagement.app.EndPoints;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.NotificationModel;
import com.example.operatormanagement.model.ReplacementModel;
import com.example.operatormanagement.model.ShiftModel;

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
public class ShiftFragment extends Fragment {
    public static final String TAG= ShiftFragment.class.getSimpleName();
    private Unbinder unbinder;
    private ArrayList<ShiftModel> shiftModels;
    private ShiftAdapter shiftAdapter;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.gridShift)
    ListView gridShift;

    @BindView(R.id.txtNull)
    TextView txtNull;

    @BindView(R.id.vfShift)
    ViewFlipper vfShift;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        shiftModels = new ArrayList<>();

        getShifts(MyApplication.prefManager.getUserCode());

        return view;
    }

    private void getShifts(int operatorId) {
        vfShift.setDisplayedChild(0);
        JSONObject params = new JSONObject();
        try {

            params.put("operatorId", operatorId);

            RequestHelper.builder(EndPoints.GET_SHIFTS)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onGetShifts)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RequestHelper.Callback onGetShifts = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONArray arr = new JSONArray(args[0].toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        ShiftModel shiftModel = new ShiftModel();
                        shiftModel.setShiftId(object.getInt("id"));
                        shiftModel.setShiftDate(object.getString("date"));
                        shiftModel.setShiftName(object.getString("shiftName"));
                        shiftModel.setShiftTime(object.getString("time"));
                        shiftModels.add(shiftModel);
                    }
                    vfShift.setDisplayedChild(1);
                    shiftAdapter = new ShiftAdapter(shiftModels, MyApplication.context);
                    gridShift.setAdapter(shiftAdapter);

                    if (shiftModels.size()==0){
                        vfShift.setDisplayedChild(3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
