package ir.taxi1880.operatormanagement.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    public static final String TAG = MenuFragment.class.getSimpleName();
    Unbinder unbinder;


    @OnClick(R.id.llNotification)
    void onNotification() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new NotificationFragment())
                .replace();
    }

    @OnClick(R.id.llShift)
    void onShifts() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new ShiftFragment())
                .replace();
    }

    @OnClick(R.id.llReplacement)
    void onReplacement() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new SendReplacementReqFragment())
                .replace();
    }

    @OnClick(R.id.llWaitReplacement)
    void onWaotReplacement() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new ReplacementWaitingFragment())
                .replace();
    }

    @OnClick(R.id.llMessage)
    void onMessage() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new MessageFragment())
                .replace();
    }

    @BindView(R.id.txtBadgeCount)
    TextView txtBadgeCount;

    @BindView(R.id.txtRequestCount)
    TextView txtRequestCount;

    @BindView(R.id.txtOperatorCode)
    TextView txtOperatorCode;

    @BindView(R.id.txtOperatorName)
    TextView txtOperatorName;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        if (MyApplication.prefManager.getCountNotification() == 0) {
            txtBadgeCount.setVisibility(View.GONE);
        } else {
            txtBadgeCount.setVisibility(View.VISIBLE);
            txtBadgeCount.setText(MyApplication.prefManager.getCountNotification() + "");
        }

        if (MyApplication.prefManager.getCountRequest() == 0) {
            txtRequestCount.setVisibility(View.GONE);
        } else {
            txtRequestCount.setVisibility(View.VISIBLE);
            txtRequestCount.setText(MyApplication.prefManager.getCountRequest() + "");
        }

        txtOperatorName.setText(MyApplication.prefManager.getOperatorName());

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
