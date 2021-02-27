package ir.taxi1880.operatormanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
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
                fragment = new PendingComplaintFragment();
                break;
            case 1:
                fragment = new AllComplaintFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
