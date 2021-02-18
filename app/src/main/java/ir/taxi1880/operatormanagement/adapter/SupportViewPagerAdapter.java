package ir.taxi1880.operatormanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import ir.taxi1880.operatormanagement.fragment.AllComplaintFragment;
import ir.taxi1880.operatormanagement.fragment.PendingComplaintFragment;

public class SupportViewPagerAdapter extends FragmentStateAdapter {

    public SupportViewPagerAdapter(@NonNull Fragment fragment) {
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
}
