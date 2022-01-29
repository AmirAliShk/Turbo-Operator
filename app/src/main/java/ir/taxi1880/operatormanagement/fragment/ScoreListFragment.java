package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.ScoreAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentScoreListBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ScoreModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ScoreListFragment extends Fragment {

    public static final String TAG = ScoreListFragment.class.getSimpleName();
    FragmentScoreListBinding binding;
    private ArrayList<ScoreModel> scoreModels;
    private ScoreAdapter scoreAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScoreListBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        getScore();
        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());
        return binding.getRoot();
    }

    private void getScore() {
        if (binding.vfScore != null)
            binding.vfScore.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.SCORE)
                .listener(onScore)
                .get();
    }

    private RequestHelper.Callback onScore = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    scoreModels = new ArrayList<>();
                    JSONObject bestObj = new JSONObject(args[0].toString());
                    boolean success = bestObj.getBoolean("success");
                    String message = bestObj.getString("message");

                    if (success) {
                        JSONArray rewardsArr = bestObj.getJSONArray("data");
                        for (int i = 0; i < rewardsArr.length(); i++) {
                            JSONObject obj = rewardsArr.getJSONObject(i);
                            ScoreModel scoreModel = new ScoreModel();
                            scoreModel.setScore(obj.getInt("score"));
                            scoreModel.setHour(obj.getString("hour"));
                            scoreModels.add(scoreModel);
                        }
                        scoreAdapter = new ScoreAdapter(scoreModels);
                        if (binding.recycleScore != null)
                            binding.recycleScore.setAdapter(scoreAdapter);
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }
                    if (scoreModels.size() == 0) {
                        if (binding.vfScore != null)
                            binding.vfScore.setDisplayedChild(2);
                    } else {
                        if (binding.vfScore != null)
                            binding.vfScore.setDisplayedChild(1);
                    }
                } catch (Exception e) {
                    if (binding.vfScore != null)
                        binding.vfScore.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onScore onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfScore != null)
                    binding.vfScore.setDisplayedChild(3);
            });
        }
    };
}