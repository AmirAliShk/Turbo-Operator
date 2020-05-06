package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ScoreAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.model.ScoreModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class ScoreFragment extends Fragment {

    private Unbinder unbinder;
    private ArrayList<ScoreModel> scoreModels;
    private ScoreAdapter scoreAdapter;

    @BindView(R.id.recycleScore)
    RecyclerView recycleScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        unbinder = ButterKnife.bind(this, view);
        getScore();
        return view;
    }

    private void getScore() {
        RequestHelper.builder(EndPoints.SCORE)
                .listener(onScore)
                .get();
    }


    private RequestHelper.Callback onScore = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        scoreModels = new ArrayList<>();
                        JSONObject bestObj = new JSONObject(args[0].toString());
                        boolean success = bestObj.getBoolean("success");
                        String messsage = bestObj.getString("messsage");
                        JSONArray rewardsArr = bestObj.getJSONArray("data");
                        for (int i = 0; i < rewardsArr.length(); i++) {
                            JSONObject obj = rewardsArr.getJSONObject(i);
                            ScoreModel scoreModel = new ScoreModel();
                            scoreModel.setScore(obj.getInt("score"));
                            scoreModel.setHour(obj.getString("hour"));
                            scoreModels.add(scoreModel);
                        }

                        scoreAdapter = new ScoreAdapter(scoreModels);
                        recycleScore.setAdapter(scoreAdapter);

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

