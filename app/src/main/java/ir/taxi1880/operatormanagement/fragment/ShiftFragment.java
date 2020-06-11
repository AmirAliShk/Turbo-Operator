package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftFragment extends Fragment {

  Unbinder unbinder;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @OnClick(R.id.llShiftList)
  void llShifts() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ShiftListFragment())
            .replace();
  }

  @OnClick(R.id.llSendReplacement)
  void llSendReplacement() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new SendReplacementReqFragment())
            .replace();
  }

  @OnClick(R.id.llGetReplacement)
  void llGetReplacement() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ReplacementWaitingFragment())
            .replace();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shift, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
