package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.HomeFragment;
import ir.taxi1880.operatormanagement.fragment.MenuFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new HomeFragment();
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new MenuFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(MyApplication.context).inflate(R.layout.custom_tab, null);
        ImageView img = v.findViewById(R.id.imgCustomTab);
        if (position == 0) {
            img.setImageResource(R.mipmap.home_selected);
        } else {
            img.setImageResource(R.drawable.ic_menu_unselected);
        }
        return v;
    }

    public void setSelectView(TabLayout tabLayout, int position, String type) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View view = tab.getCustomView();
        ImageView imgCustomTab = view.findViewById(R.id.imgCustomTab);
        if (type.equals("select")) {
            if (tab.getPosition() == 0) {
                imgCustomTab.setImageResource(R.mipmap.home_selected);
            } else {
                imgCustomTab.setImageResource(R.mipmap.menu_selected);
            }
        } else if (type.equals("unSelect")) {
            if (tab.getPosition() == 0) {
                imgCustomTab.setImageResource(R.drawable.ic_home_unselected);
            } else {
                imgCustomTab.setImageResource(R.drawable.ic_menu_unselected);
            }
        }
    }

}
