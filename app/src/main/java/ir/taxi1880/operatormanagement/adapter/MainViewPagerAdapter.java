package ir.taxi1880.operatormanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ir.taxi1880.operatormanagement.fragment.HomeFragment;
import ir.taxi1880.operatormanagement.fragment.MenuFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment=new HomeFragment();
        switch (position) {
            case 0:
                fragment =new HomeFragment();
                break;
            case 1:
                fragment =new MenuFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
