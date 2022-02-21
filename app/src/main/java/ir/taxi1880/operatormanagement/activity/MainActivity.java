package ir.taxi1880.operatormanagement.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.MainViewPagerAdapter;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.databinding.ActivityMainBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.AccountFragment;
import ir.taxi1880.operatormanagement.fragment.MessageFragment;
import ir.taxi1880.operatormanagement.fragment.NotificationFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.ThemeHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class MainActivity extends AppCompatActivity implements NotificationFragment.RefreshNotificationCount {

    public static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    MainViewPagerAdapter mainViewPagerAdapter;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.onActivityCreateSetTheme(this);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (MyApplication.prefManager.isDarkMode()) {
                window.setNavigationBarColor(getResources().getColor(R.color.dark_navigation_bar));
                window.setStatusBarColor(getResources().getColor(R.color.dark_action_bar));
            } else {
                window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        MyApplication.configureAccount();

        mainViewPagerAdapter = new MainViewPagerAdapter(this);
        binding.vpMain.setAdapter(mainViewPagerAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.imgTheme.setVisibility(View.VISIBLE);
        } else {
            binding.imgTheme.setVisibility(View.GONE);
        }

        if (MyApplication.prefManager.isDarkMode()) {
            binding.imgTheme.setImageResource(R.drawable.ic_dark);
        } else {
            binding.imgTheme.setImageResource(R.drawable.ic_light);
        }

        new TabLayoutMediator(binding.tabMain, binding.vpMain, (tab, position) -> tab.setCustomView(mainViewPagerAdapter.getTabView(position))).attach();

        binding.tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPagerAdapter.setSelectView(binding.tabMain, tab.getPosition(), "select");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mainViewPagerAdapter.setSelectView(binding.tabMain, tab.getPosition(), "unSelect");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.imgNotification.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new NotificationFragment())
                .replace());

        binding.imgTheme.setOnClickListener(view -> {
            if (MyApplication.prefManager.isDarkMode()) {
                ThemeHelper.changeToTheme(MyApplication.currentActivity);
                MyApplication.prefManager.setDarkMode(false);
            } else {
                ThemeHelper.changeToTheme(MyApplication.currentActivity);
                MyApplication.prefManager.setDarkMode(true);
            }
        });

        binding.imgMessage.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new MessageFragment())
                .replace());

        binding.imgProfile.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new AccountFragment())
                .replace());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.currentActivity = this;
        MyApplication.prefManager.setAppRun(true);

        if (MyApplication.prefManager.getCountNotification() == 0) {
            binding.txtBadgeCount.setVisibility(View.GONE);
        } else {
            String message = " شما " + MyApplication.prefManager.getCountNotification() + " اطلاعیه جدید دارید. ";
            new GeneralDialog()
                    .title("هشدار")
                    .message(message)
                    .firstButton("باشه", () -> FragmentHelper.toFragment(MyApplication.currentActivity, new NotificationFragment()).replace())
                    .cancelable(false)
                    .show();
            binding.txtBadgeCount.setVisibility(View.VISIBLE);
            binding.txtBadgeCount.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getCountNotification() + ""));
        }

        if (DataHolder.getInstance().getPushType() != null) {
            if (DataHolder.getInstance().getPushType().equals(Constant.PUSH_NOTIFICATION_MESSAGE_TYPE)) {
                FragmentHelper.toFragment(this, new MessageFragment())
                        .replace();
                DataHolder.getInstance().setPushType(null);
            } else if (DataHolder.getInstance().getPushType().equals(Constant.PUSH_NOTIFICATION_ANNOUNCEMENT_TYPE)) {
                FragmentHelper.toFragment(this, new NotificationFragment())
                        .replace();
                DataHolder.getInstance().setPushType(null);
            }
        }
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
    public void onBackPressed() {
        try {
            KeyBoardHelper.hideKeyboard();
            if (getFragmentManager().getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                if (doubleBackToExitPressedOnce) {
                    MyApplication.prefManager.setStartGettingAddress(false);
                    finish();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    MyApplication.Toast(getString(R.string.txt_please_for_exit_reenter_back), Toast.LENGTH_SHORT);
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onBackPressed method");
        }
    }

    @Override
    public void refreshNotification() {
        if (MyApplication.prefManager.getCountNotification() == 0) {
            binding.txtBadgeCount.setVisibility(View.GONE);
        } else {
            binding.txtBadgeCount.setVisibility(View.VISIBLE);
            binding.txtBadgeCount.setText(StringHelper.toPersianDigits(MyApplication.prefManager.getCountNotification() + ""));
        }
    }
}