package ir.taxi1880.operatormanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.activity.SupportActivity;
import ir.taxi1880.operatormanagement.activity.TripRegisterActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.RequestDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    public final String TAG = MenuFragment.class.getSimpleName();
    private Unbinder unbinder;

    @OnClick(R.id.llTripRegister)
    void onTripRegister() {
        if (MyApplication.prefManager.getAccessInsertService() == 0) {
            new GeneralDialog()
                    .title("هشدار")
                    .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                    .firstButton("باشه", null)
                    .show();
        } else {
            startActivity(new Intent(MyApplication.context, TripRegisterActivity.class));
            MyApplication.currentActivity.finish();
        }
    }

    @OnClick(R.id.llDetermination)
    void onDetermination() {
        if (MyApplication.prefManager.getAccessStationDeterminationPage() == 0) {
            new GeneralDialog()
                    .title("هشدار")
                    .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                    .firstButton("باشه", null)
                    .show();
        } else {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new DeterminationPageFragment())
                    .replace();
        }
    }

    @OnClick(R.id.llSupport)
    void onSupport() {
        if (MyApplication.prefManager.getAccessDriverSupport() == 1) {
            startActivity(new Intent(MyApplication.currentActivity, SupportActivity.class));
            MyApplication.currentActivity.finish();
        } else {
            new GeneralDialog()
                    .title("هشدار")
                    .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                    .firstButton("باشه", null)
                    .show();
        }
    }

    @OnClick(R.id.llShift)
    void onShift() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new ShiftListFragment())
                .replace();
    }

    @OnClick(R.id.llBest)
    void onBest() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new BestsFragment())
                .replace();
    }

    @OnClick(R.id.llComplaint)
    void onComplaint() {
        if (MyApplication.prefManager.getAccessComplaint() == 0) {
            new GeneralDialog()
                    .title("هشدار")
                    .message("شما اجازه دسترسی به این بخش از برنامه را ندارید")
                    .firstButton("باشه", null)
                    .show();
        } else {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new ComplaintFragment())
                    .replace();
        }
    }

    @OnClick(R.id.llRequest)
    void onRequest() {
        new RequestDialog()
                .show();
    }

    @OnClick(R.id.llRewards)
    void onRewards() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new RewardsFragment())
                .replace();
    }

    @OnClick(R.id.llScores)
    void onScores() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new ScoreListFragment())
                .replace();
    }

    @OnClick(R.id.llMyMistakes)
    void onMistakes() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new OperatorMistakesFragment())
                .replace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
