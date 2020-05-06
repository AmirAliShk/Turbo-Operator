

package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.RewardAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.model.RewardsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class RewardsFragment extends Fragment {

  private Unbinder unbinder;
  private ArrayList<RewardsModel> rewardsModels;
  private RewardAdapter rewardAdapter;

  @BindView(R.id.recycleRewards)
  RecyclerView recycleRewards;

  @BindView(R.id.vfReward)
  ViewFlipper vfReward;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_rewards, container, false);
    unbinder = ButterKnife.bind(this, view);
    getRewards();
    return view;
  }

  private void getRewards() {
    vfReward.setDisplayedChild(0);
    RequestHelper.builder(EndPoints.REWARDS)
            .listener(onRewards)
            .get();
  }


  private RequestHelper.Callback onRewards = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            rewardsModels = new ArrayList<>();
            JSONObject bestObj = new JSONObject(args[0].toString());
            boolean success = bestObj.getBoolean("success");
            String messsage = bestObj.getString("messsage");
            JSONArray rewardsArr = bestObj.getJSONArray("data");
            for (int i = 0; i < rewardsArr.length(); i++) {
              JSONObject obj = rewardsArr.getJSONObject(i);
              RewardsModel rewardsModel = new RewardsModel();
              rewardsModel.setScore(obj.getInt("score"));
              rewardsModel.setComment(obj.getString("comment"));
              rewardsModel.setExpireDate(obj.getString("expireDate"));
              rewardsModel.setexpireTime(obj.getString("expireTime"));
              rewardsModels.add(rewardsModel);
            }
            vfReward.setDisplayedChild(1);
            rewardAdapter = new RewardAdapter(rewardsModels);
            recycleRewards.setAdapter(rewardAdapter);

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
