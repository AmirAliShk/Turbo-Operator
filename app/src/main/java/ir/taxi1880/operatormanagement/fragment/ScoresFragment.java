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
public class ScoresFragment extends Fragment {
  Unbinder unbinder;

  @OnClick(R.id.llBest)
  void llBest(){
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new BestsFragment())
            .replace();
  }

  @OnClick(R.id.llRewards)
  void llRewards(){
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new RewardsFragment())
            .replace();
  }

  @OnClick(R.id.llScoreList)
  void llScoreList(){
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ScoreFragment())
            .replace();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_scores, container, false);
    unbinder= ButterKnife.bind(this,view);
    TypefaceUtil.overrideFonts(view);
    return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
