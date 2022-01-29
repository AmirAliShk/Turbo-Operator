package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_COUNT_PENDING_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_ALL_COMPLAINT;
import static ir.taxi1880.operatormanagement.app.Keys.VALUE_COUNT_PENDING_COMPLAINT;

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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ir.taxi1880.operatormanagement.adapter.ComplaintViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentComplaintBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class ComplaintFragment extends Fragment {
    public static final String TAG = ComplaintFragment.class.getSimpleName();
    FragmentComplaintBinding binding;
    ComplaintViewPagerAdapter complaintViewPagerAdapter;
    int complaintCountNew;
    int complaintCountPending;
    LocalBroadcastManager broadcaster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComplaintBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        complaintViewPagerAdapter = new ComplaintViewPagerAdapter(this);
        binding.vpSupport.setAdapter(complaintViewPagerAdapter);
        binding.vpSupport.setUserInputEnabled(false);

        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);

        new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(complaintViewPagerAdapter.getTabView(position, 0, 0))).attach();

        binding.tbLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                complaintViewPagerAdapter.setSelectView(binding.tbLayout, tab.getPosition(), "select");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                complaintViewPagerAdapter.setSelectView(binding.tbLayout, tab.getPosition(), "unSelect");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.imgBack.setOnClickListener(view -> {
            MyApplication.currentActivity.onBackPressed();
        });

        return binding.getRoot();
    }

    BroadcastReceiver counterReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            complaintCountNew = intent.getIntExtra(VALUE_COUNT_ALL_COMPLAINT, 0);
            if (binding.vpSupport != null) {
                new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(complaintViewPagerAdapter.getTabView(position, complaintCountNew, complaintCountPending))).attach();
            }
        }
    };

    BroadcastReceiver counterReceiverPending = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            complaintCountPending = intent.getIntExtra(VALUE_COUNT_PENDING_COMPLAINT, 0);
            if (binding.vpSupport != null) {
                new TabLayoutMediator(binding.tbLayout, binding.vpSupport, (tab, position) -> tab.setCustomView(complaintViewPagerAdapter.getTabView(position, complaintCountNew, complaintCountPending))).attach();
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