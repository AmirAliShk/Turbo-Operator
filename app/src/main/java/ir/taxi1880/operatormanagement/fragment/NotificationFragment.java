package ir.taxi1880.operatormanagement.fragment;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.NotificationAdapter;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.NotificationModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    Unbinder unbinder;
    public static final String TAG = NotificationFragment.class.getSimpleName();
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

        NotificationManager notificationManager = (NotificationManager) MyApplication.currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constant.PUSH_NOTIFICATION_ID);

        notificationModels = new ArrayList<>();
        getNews();

        return view;
    }

    private void getNews() {
        MyApplication.currentActivity.runOnUiThread(() -> {
            if (vfNoti != null)
                vfNoti.setDisplayedChild(0);
        });

        RequestHelper.builder(EndPoints.GET_NEWS)
                .listener(onGetNews)
                .post();
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
                    if (vfNoti != null)
                        vfNoti.setDisplayedChild(1);
                    notificationAdapter = new NotificationAdapter(notificationModels, MyApplication.context);
                    if (listNotification != null)
                        listNotification.setAdapter(notificationAdapter);
                    if (notificationModels.size() == 0) {
                        if (vfNoti != null)
                            vfNoti.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "NotificationFragment class, onGetNews onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfNoti != null)
                    vfNoti.setDisplayedChild(3);
            });
        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private RefreshNotificationCount refreshListener;

    public interface RefreshNotificationCount {
        void refreshNotification();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        refreshListener = (RefreshNotificationCount) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (refreshListener != null) {
            refreshListener.refreshNotification();
        }
    }
}
