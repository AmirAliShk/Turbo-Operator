package ir.taxi1880.operatormanagement.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.NotificationAdapter;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentNotificationBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.NotificationModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class NotificationFragment extends Fragment {

    public static final String TAG = NotificationFragment.class.getSimpleName();
    FragmentNotificationBinding binding;
    ArrayList<NotificationModel> notificationModels;
    NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        NotificationManager notificationManager = (NotificationManager) MyApplication.currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constant.PUSH_NOTIFICATION_ID);

        notificationModels = new ArrayList<>();
        getNews();

        binding.imgBack.setOnClickListener(view -> {
            MyApplication.currentActivity.onBackPressed();
        });

        return binding.getRoot();
    }

    private void getNews() {
        MyApplication.currentActivity.runOnUiThread(() -> {
            if (binding.vfNoti != null)
                binding.vfNoti.setDisplayedChild(0);
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
                    if (binding.vfNoti != null)
                        binding.vfNoti.setDisplayedChild(1);
                    notificationAdapter = new NotificationAdapter(notificationModels);
                    if (binding.listNotification != null)
                        binding.listNotification.setAdapter(notificationAdapter);
                    if (notificationModels.size() == 0) {
                        if (binding.vfNoti != null)
                            binding.vfNoti.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetNews onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfNoti != null)
                    binding.vfNoti.setDisplayedChild(3);
            });
        }
    };

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