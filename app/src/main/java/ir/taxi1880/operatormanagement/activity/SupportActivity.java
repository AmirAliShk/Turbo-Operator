package ir.taxi1880.operatormanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SupportViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.MessageFragment;
import ir.taxi1880.operatormanagement.fragment.NotificationFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SupportActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        MyApplication.configureAccount();
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        supportViewPagerAdapter = new SupportViewPagerAdapter(this);
        vpSupport.setAdapter(supportViewPagerAdapter);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.prefManager.setAppRun(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.currentActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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