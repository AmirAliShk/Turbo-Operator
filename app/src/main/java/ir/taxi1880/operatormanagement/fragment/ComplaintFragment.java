package ir.taxi1880.operatormanagement.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ComplaintViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

public class ComplaintFragment extends Fragment {
    public static final String TAG = ComplaintFragment.class.getSimpleName();
    Unbinder unbinder;
    ComplaintViewPagerAdapter complaintViewPagerAdapter;
    int complaintCountNew;
    int complaintCountPending;
    LocalBroadcastManager broadcaster;

    @BindView(R.id.vpSupport)
    ViewPager2 vpSupport;

    @BindView(R.id.tbLayout)
    TabLayout tbLayout;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_complaint, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        complaintViewPagerAdapter = new ComplaintViewPagerAdapter(this);
        vpSupport.setAdapter(complaintViewPagerAdapter);
        vpSupport.setUserInputEnabled(false);

        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

        new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
            tab.setCustomView(complaintViewPagerAdapter.getTabView(position, 0, 0));
        }).attach();

        tbLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                complaintViewPagerAdapter.setSelectView(tbLayout, tab.getPosition(), "select");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                complaintViewPagerAdapter.setSelectView(tbLayout, tab.getPosition(), "unSelect");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    BroadcastReceiver counterReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            complaintCountNew = intent.getIntExtra(VALUE_COUNT_ALL_COMPLAINT, 0);
            if (vpSupport != null) {
                new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
                    tab.setCustomView(complaintViewPagerAdapter.getTabView(position, complaintCountNew, complaintCountPending));
                }).attach();
            }
        }
    };

    BroadcastReceiver counterReceiverPending = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            complaintCountPending = intent.getIntExtra(VALUE_COUNT_PENDING_COMPLAINT, 0);
            if (vpSupport != null) {
                new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
                    tab.setCustomView(complaintViewPagerAdapter.getTabView(position, complaintCountNew, complaintCountPending));
                }).attach();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (counterReceiverNew != null) {
            MyApplication.currentActivity.registerReceiver(counterReceiverNew, new IntentFilter());
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((counterReceiverNew), new IntentFilter(KEY_COUNT_ALL_COMPLAINT));
        }
        if (counterReceiverPending != null) {
            MyApplication.currentActivity.registerReceiver(counterReceiverPending, new IntentFilter());
            LocalBroadcastManager.getInstance(MyApplication.currentActivity).registerReceiver((counterReceiverPending), new IntentFilter(KEY_COUNT_PENDING_COMPLAINT));
        }
    }

}
