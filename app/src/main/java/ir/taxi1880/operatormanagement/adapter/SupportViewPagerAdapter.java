package ir.taxi1880.operatormanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.AllMistakesFragment;
import ir.taxi1880.operatormanagement.fragment.PendingMistakesFragment;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SupportViewPagerAdapter extends FragmentStateAdapter {

    public SupportViewPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new AllMistakesFragment();
        switch (position) {
            case 0:
                fragment = new AllMistakesFragment();
                break;
            case 1:
                fragment = new PendingMistakesFragment();
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
        txtBadgeCount.setVisibility(View.GONE);
        TypefaceUtil.overrideFonts(v);
        if (position == 0) {
            txtTabTitle.setText("جدید");
//            if (counter == 0) {
//                txtBadgeCount.setVisibility(View.GONE);
//            } else {
//                txtBadgeCount.setVisibility(View.VISIBLE);
//                txtBadgeCount.setText(counter + "");
//            }
        } else {
            txtTabTitle.setText("درحال بررسی");
            txtBadgeCount.setVisibility(View.GONE);
        }

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
