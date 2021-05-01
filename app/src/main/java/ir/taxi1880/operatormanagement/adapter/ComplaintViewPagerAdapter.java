package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.AllComplaintFragment;
import ir.taxi1880.operatormanagement.fragment.PendingComplaintFragment;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class ComplaintViewPagerAdapter extends FragmentStateAdapter {

    public ComplaintViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new AllComplaintFragment();
        switch (position) {
            case 0:
                fragment = new AllComplaintFragment();
                break;
            case 1:
                fragment = new PendingComplaintFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public View getTabView(int position, int newCount, int pendingCount) {
        View v = LayoutInflater.from(MyApplication.context).inflate(R.layout.support_item_tab, null);
        TextView txtTabTitle = v.findViewById(R.id.txtTabTitle);
        TextView txtBadgeCount = v.findViewById(R.id.txtBadgeCount);
        txtBadgeCount.setVisibility(View.GONE);
        TypefaceUtil.overrideFonts(v);

        txtTabTitle.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorBlack));
        txtBadgeCount.setBackgroundResource(R.drawable.badge_unselected);

        if (position == 0) {
            txtTabTitle.setText("جدید");
//            txtTabTitle.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorAccent));
            if (newCount == 0) {
                txtBadgeCount.setVisibility(View.GONE);
            } else {
                txtBadgeCount.setVisibility(View.VISIBLE);
                txtBadgeCount.setText(newCount + "");
//                txtBadgeCount.setBackgroundResource(R.drawable.badge_selected);
            }
        } else {
            txtTabTitle.setText("درحال بررسی");
//            txtTabTitle.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.grayMedium));
            if (pendingCount == 0) {
                txtBadgeCount.setVisibility(View.GONE);
            } else {
                txtBadgeCount.setVisibility(View.VISIBLE);
                txtBadgeCount.setText(pendingCount + "");
//                txtBadgeCount.setBackgroundResource(R.drawable.badge_unselected);
            }
        }

        return v;
    }

    public void setSelectView(TabLayout tabLayout, int position, String type) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View view = tab.getCustomView();
        TextView txtTabTitle = view.findViewById(R.id.txtTabTitle);
        TextView txtBadgeCount = view.findViewById(R.id.txtBadgeCount);
//        if (type.equals("select")) {
//            txtTabTitle.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.colorAccent));
//            txtBadgeCount.setBackgroundResource(R.drawable.badge_selected);
//        } else if (type.equals("unSelect")) {
//            txtTabTitle.setTextColor(MyApplication.currentActivity.getResources().getColor(R.color.grayMedium));
//            txtBadgeCount.setBackgroundResource(R.drawable.badge_unselected);
//        }
    }

}
