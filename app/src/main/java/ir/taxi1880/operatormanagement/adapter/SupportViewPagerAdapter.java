package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.AllComplaintFragment;
import ir.taxi1880.operatormanagement.fragment.PendingComplaintFragment;

public class SupportViewPagerAdapter extends FragmentStateAdapter {

    public SupportViewPagerAdapter(@NonNull FragmentActivity fragment) {
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

    public View getTabView(int position) {
        View v = LayoutInflater.from(MyApplication.context).inflate(R.layout.support_item_tab, null);
        TextView txtTabTitle = v.findViewById(R.id.txtTabTitle);
        TextView txtBadgeCount = v.findViewById(R.id.txtBadgeCount);
        if (position == 0) {
            txtTabTitle.setText("جدید");
        } else {
            txtTabTitle.setText("درحال بررسی");
        }

//        if (showBadge){
//           txtBadgeCount.setVisibility(View.VISIBLE);
//        }else {
//            txtBadgeCount.setVisibility(View.GONE);
//        }

        return v;
    }

//    public void setSelectView(TabLayout tabLayout, int position, String type) {
//        TabLayout.Tab tab = tabLayout.getTabAt(position);
//        View view = tab.getCustomView();
//        ImageView imgCustomTab = view.findViewById(R.id.imgCustomTab);
//        if (type.equals("select")) {
//            if (tab.getPosition() == 0) {
//                imgCustomTab.setImageResource(R.mipmap.home_selected);
//            } else {
//                imgCustomTab.setImageResource(R.mipmap.menu_selected);
//            }
//        } else if (type.equals("unSelect")) {
//            if (tab.getPosition() == 0) {
//                imgCustomTab.setImageResource(R.drawable.ic_home_unselected);
//            } else {
//                imgCustomTab.setImageResource(R.drawable.ic_menu_unselected);
//            }
//        }
//    }

}
