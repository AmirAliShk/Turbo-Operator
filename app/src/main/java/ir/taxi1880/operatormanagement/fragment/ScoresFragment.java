package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoresFragment extends Fragment {
  Unbinder unbinder;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.vfScores)
  ViewFlipper vfScores;

  @BindView(R.id.txtDay)
  TextView txtDay;

  @BindView(R.id.txtDayScore)
  TextView txtDayScore;

  @BindView(R.id.txtMonth)
  TextView txtMonth;

  @BindView(R.id.txtMonthScore)
  TextView txtMonthScore;

  @OnClick(R.id.llBest)
  void llBest() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new BestsFragment())
            .replace();
  }

  @OnClick(R.id.llRewards)
  void llRewards() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new RewardsFragment())
            .replace();
  }

  @OnClick(R.id.llScoreList)
  void llScoreList() {
    FragmentHelper
            .toFragment(MyApplication.currentActivity, new ScoreListFragment())
            .replace();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_scores, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);
    getSingleScore();
    return view;
  }

  private void getSingleScore() {
    if (vfScores != null)
      vfScores.setDisplayedChild(0);
    RequestHelper.builder(EndPoints.SINGLE)
            .addPath(MyApplication.prefManager.getUserCode() + "")
            .listener(getSingleScore)
            .get();
  }

  private RequestHelper.Callback getSingleScore = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONObject bestObj = new JSONObject(args[0].toString());
            boolean success = bestObj.getBoolean("success");
            String message = bestObj.getString("message");
            JSONObject data = bestObj.getJSONObject("data");

            int totalScore = data.getInt("totalScore");
            int monthScore = data.getInt("monthScore");
            int weekScore = data.getInt("weekScore");
            int todayScore = data.getInt("todayScore");

            if (txtDayScore != null)
              txtDayScore.setText(StringHelper.toPersianDigits(todayScore + ""));
            if (txtMonthScore!=null)
            txtMonthScore.setText(StringHelper.toPersianDigits(monthScore + ""));

            if (vfScores != null)
              vfScores.setDisplayedChild(1);

          } catch (Exception e) {
            e.printStackTrace();
          }

        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
