package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.RewardAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentRewardsBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.RewardsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class RewardsFragment extends Fragment {

    public static final String TAG = RewardsFragment.class.getSimpleName();
    FragmentRewardsBinding binding;
    private ArrayList<RewardsModel> rewardsModels;
    private RewardAdapter rewardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        getRewards();
        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());
        return binding.getRoot();
    }

    private void getRewards() {
        if (binding.vfReward != null)
            binding.vfReward.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.REWARDS)
                .listener(onRewards)
                .get();
    }

    private RequestHelper.Callback onRewards = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    rewardsModels = new ArrayList<>();
                    JSONObject bestObj = new JSONObject(args[0].toString());
                    boolean success = bestObj.getBoolean("success");
                    String messsage = bestObj.getString("messsage");

                    if (success) {
                        JSONArray rewardsArr = bestObj.getJSONArray("data");
                        for (int i = 0; i < rewardsArr.length(); i++) {
                            JSONObject obj = rewardsArr.getJSONObject(i);
                            RewardsModel rewardsModel = new RewardsModel();
                            rewardsModel.setScore(obj.getInt("score"));
                            rewardsModel.setComment(obj.getString("comment"));
                            rewardsModel.setExpireDate(obj.getString("expireDate"));
                            rewardsModel.setexpireTime(obj.getString("expireTime"));
                            rewardsModel.setSubject(obj.getString("subject"));
                            rewardsModels.add(rewardsModel);
                        }
                        rewardAdapter = new RewardAdapter(rewardsModels);
                        if (binding.recycleRewards != null)
                            binding.recycleRewards.setAdapter(rewardAdapter);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(messsage)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }

                    if (rewardsModels.size() == 0) {
                        if (binding.vfReward != null)
                            binding.vfReward.setDisplayedChild(2);
                    } else {
                        if (binding.vfReward != null)
                            binding.vfReward.setDisplayedChild(1);
                    }
                } catch (Exception e) {
                    if (binding.vfReward != null)
                        binding.vfReward.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onRewards onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfReward != null)
                    binding.vfReward.setDisplayedChild(3);
            });
        }
    };
}