package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SupportViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SupportFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    Unbinder unbinder;
    SupportViewPagerAdapter supportViewPagerAdapter;

    @BindView(R.id.vpSupport)
    ViewPager2 vpSupport;

    @BindView(R.id.tbLayout)
    TabLayout tbLayout;

    @OnClick(R.id.imgBack)
    void onBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.btnActivate)
    Button btnActivate;

    @BindView(R.id.btnDeActivate)
    Button btnDeActivate;

    @OnClick(R.id.btnActivate)
    void onActivePress() {
        KeyBoardHelper.hideKeyboard();
        new GeneralDialog()
                .title("هشدار")
                .cancelable(false)
                .message("مطمئنی میخوای وارد صف بشی؟")
                .firstButton("مطمئنم", () -> {
                    setActivate();
//                MyApplication.Toast("activated",Toast.LENGTH_SHORT);
                })
                .secondButton("نیستم", null)
                .show();

    }

    @OnClick(R.id.btnDeActivate)
    void onDeActivePress() {
        KeyBoardHelper.hideKeyboard();
        new GeneralDialog()
                .title("هشدار")
                .cancelable(false)
                .message("مطمئنی میخوای خارج بشی؟")
                .firstButton("مطمئنم", () -> {
                    if (MyApplication.prefManager.isCallIncoming()) {
                        MyApplication.Toast(getString(R.string.exit), Toast.LENGTH_SHORT);
                    } else {
                        setDeActivate();
                    }
                })
                .secondButton("نیستم", null)
                .show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        supportViewPagerAdapter = new SupportViewPagerAdapter(this);
        vpSupport.setAdapter(supportViewPagerAdapter);
        tbLayout.addOnTabSelectedListener(this);

        new TabLayoutMediator(tbLayout, vpSupport, (tab, position) -> {
            vpSupport.setCurrentItem(tab.getPosition(), true);
            if (position == 0) {
                tab.setText("جدید");
            } else {
                tab.setText("در حال بررسی");
            }
        }).attach();

        if (MyApplication.prefManager.getActivateStatus()) {
            btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
            btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void setActivate() {
        if (btnActivate != null)
            btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
        MyApplication.prefManager.setActivateStatus(true);
        if (btnDeActivate != null) {
            btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public void setDeActivate() {
        MyApplication.prefManager.setActivateStatus(false);
        if (btnActivate != null)
            btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        if (btnDeActivate != null) {
            btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
            btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
        }
    }
}
