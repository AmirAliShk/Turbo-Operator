package com.example.operatormanagement.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.operatormanagement.OkHttp.RequestHelper;
import com.example.operatormanagement.R;
import com.example.operatormanagement.adapter.NotificationAdapter;
import com.example.operatormanagement.app.EndPoints;
import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.helper.TypefaceUtil;
import com.example.operatormanagement.model.NotificationModel;

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
public class NotificationFragment extends Fragment {
    public static final String TAG= NotificationFragment.class.getSimpleName();
    Unbinder unbinder;
    ArrayList<NotificationModel> notificationModels;
    NotificationAdapter notificationAdapter;

    @BindView(R.id.listNotification)
    ListView listNotification;

    @BindView(R.id.txtNull)
    TextView txtNull;

    @BindView(R.id.vfNoti)
    ViewFlipper vfNoti;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        notificationModels = new ArrayList<>();
        getNews(MyApplication.prefManager.getUserCode());

        return view;
    }

    private void getNews(int operatorId) {
        vfNoti.setDisplayedChild(0);
        JSONObject params = new JSONObject();
        try {
            params.put("operatorId", operatorId);

            RequestHelper.builder(EndPoints.GET_NEWS)
                    .params(params)
                    .method(RequestHelper.POST)
                    .listener(onGetNews)
                    .request();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onGetNews = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONArray arr = new JSONArray(args[0].toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        NotificationModel notificationModel = new NotificationModel();
                        notificationModel.setText(object.getString("message"));
                        notificationModel.setId(object.getInt("id"));
                        notificationModel.setSendDate(object.getString("sendDate"));
                        notificationModel.setSeen(object.getInt("seen"));
                        notificationModels.add(notificationModel);
                    }
                    vfNoti.setDisplayedChild(1);
                    notificationAdapter = new NotificationAdapter(notificationModels, MyApplication.context);
                    listNotification.setAdapter(notificationAdapter);
                    if (notificationModels.size() == 0) {
                       vfNoti.setDisplayedChild(3);
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
