package ir.taxi1880.operatormanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ir.taxi1880.operatormanagement.activity.SupportActivity;
import ir.taxi1880.operatormanagement.activity.TripRegisterActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentMenuBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.RequestDialog;
import ir.taxi1880.operatormanagement.fragment.complaint.ComplaintFragment;
import ir.taxi1880.operatormanagement.fragment.mistake.OperatorMistakesFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class MenuFragment extends Fragment {
    public final String TAG = MenuFragment.class.getSimpleName();
    FragmentMenuBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        binding.llMyMistakes.setOnClickListener(view -> FragmentHelper
                .toFragment(MyApplication.currentActivity, new OperatorMistakesFragment())
                .replace());

        binding.llScores.setOnClickListener(view -> FragmentHelper.toFragment(MyApplication.currentActivity, new ScoreListFragment()).replace());

        binding.llRewards.setOnClickListener(view -> FragmentHelper.toFragment(MyApplication.currentActivity, new RewardsFragment()).replace());

        binding.llRequest.setOnClickListener(view -> new RequestDialog().show());

        binding.llComplaint.setOnClickListener(view -> {
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
        });

        binding.llBest.setOnClickListener(view -> FragmentHelper.toFragment(MyApplication.currentActivity, new BestsFragment()).replace());

        binding.llShift.setOnClickListener(view -> FragmentHelper.toFragment(MyApplication.currentActivity, new ShiftListFragment()).replace());

        binding.llSupport.setOnClickListener(view -> {
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
        });

        binding.llDetermination.setOnClickListener(view -> {
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
        });

        binding.llTripRegister.setOnClickListener(view -> {
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
        });

        return binding.getRoot();
    }
}