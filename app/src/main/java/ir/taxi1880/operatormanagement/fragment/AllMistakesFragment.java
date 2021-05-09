package ir.taxi1880.operatormanagement.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AllMistakesAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.ACTIVE_IN_DRIVER_SUPPORT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_ACTIVE_IN_DRIVER_SUPPORT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;

public class AllMistakesFragment extends Fragment {
    Unbinder unbinder;
    LocalBroadcastManager broadcaster;

    @BindView(R.id.refreshPage)
    RecyclerRefreshLayout refreshPage;

    @BindView(R.id.mistakesList)
    RecyclerView mistakesList;

    @BindView(R.id.vfAllMistake)
    ViewFlipper vfAllMistake;

    @OnClick(R.id.imgRefresh)
    void onRefresh() {
        getListen();
    }

    @OnClick(R.id.imgRefreshFail)
    void onRefreshFail() {
        getListen();
    }

    AllMistakesAdapter mAdapter;
    ArrayList<AllMistakesModel> allMistakesModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_mistakes, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        if (!MyApplication.prefManager.isActiveInSupport()) {
            if (vfAllMistake != null)
                vfAllMistake.setDisplayedChild(4);
        }else {
            getListen();
        }

        refreshPage.setOnRefreshListener(() -> getListen());

        return view;
    }

    private void getListen() {
        if (!MyApplication.prefManager.isActiveInSupport()) {
            if (vfAllMistake != null)
                vfAllMistake.setDisplayedChild(3);
            new GeneralDialog()
                    .title("هشدار")
                    .message("لطفا فعال شوید")
                    .firstButton("باشه", null)
                    .cancelable(false)
                    .show();
            return;
        }
        if (vfAllMistake != null)
            vfAllMistake.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.LISTEN)
                .listener(listenCallBack)
                .get();
    }

    RequestHelper.Callback listenCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    allMistakesModels = new ArrayList<>();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");
                    if (success) {
                        if (vfAllMistake != null)
                            vfAllMistake.setDisplayedChild(1);
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);
                            AllMistakesModel model = new AllMistakesModel();

                            model.setId(dataObj.getInt("id"));
                            model.setServiceCode(dataObj.getInt("serviceCode"));
                            model.setUserCode(dataObj.getInt("userCode"));
                            model.setDate(dataObj.getString("saveDate"));
                            model.setTime(dataObj.getString("saveTime"));
                            model.setDescription(dataObj.getString("Description"));
                            model.setTell(dataObj.getString("tell"));
                            model.setMobile(dataObj.getString("mobile"));
                            model.setUserCodeContact(dataObj.getInt("userCodeContact"));
                            model.setAddress(dataObj.getString("address"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setConDate(dataObj.getString("conDate"));
                            model.setConTime(dataObj.getString("conTime"));
                            model.setSendTime(dataObj.getString("sendTime"));
                            model.setVoipId(dataObj.getString("VoipId"));
                            model.setStationCode(dataObj.getInt("stationCode"));
                            model.setCity(dataObj.getInt("cityId"));
                            model.setDestStation(dataObj.getString("destinationStation"));
                            model.setDestination(dataObj.getString("destinationAddress"));
                            model.setPrice(dataObj.getString("servicePrice"));

                            allMistakesModels.add(model);
                        }

                        if (allMistakesModels.size() == 0) {
                            if (vfAllMistake != null)
                                vfAllMistake.setDisplayedChild(3);
                        } else {
                            if (vfAllMistake != null) {
                                vfAllMistake.setDisplayedChild(1);
                                mAdapter = new AllMistakesAdapter(MyApplication.currentActivity, allMistakesModels);
                                mistakesList.setAdapter(mAdapter);
                            }
                        }

                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_NEW_MISTAKE_COUNT);
                        broadcastIntent.putExtra(NEW_MISTAKE_COUNT, allMistakesModels.size());
                        broadcaster.sendBroadcast(broadcastIntent);

                    } else {
                        if (vfAllMistake != null)
                            vfAllMistake.setDisplayedChild(2);
                    }

                } catch (Exception e) {
                    if (refreshPage != null)
                        refreshPage.setRefreshing(false);
                    if (vfAllMistake != null)
                        vfAllMistake.setDisplayedChild(2);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (refreshPage != null)
                    refreshPage.setRefreshing(false);
                if (vfAllMistake != null)
                    vfAllMistake.setDisplayedChild(2);
            });
        }
    };

    BroadcastReceiver counterReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(ACTIVE_IN_DRIVER_SUPPORT);
            if (state.equals("active")) {
                getListen();
            } else {
                if (vfAllMistake != null)
                    vfAllMistake.setDisplayedChild(4);
            }

        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (counterReceiverNew != null) {
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).unregisterReceiver(counterReceiverNew);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.currentActivity.registerReceiver(counterReceiverNew, new IntentFilter());
        LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((counterReceiverNew), new IntentFilter(KEY_ACTIVE_IN_DRIVER_SUPPORT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
