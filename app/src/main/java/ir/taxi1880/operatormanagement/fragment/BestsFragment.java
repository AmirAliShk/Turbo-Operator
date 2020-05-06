package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
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
import ir.taxi1880.operatormanagement.adapter.BestAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.model.BestModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class BestsFragment extends Fragment {
  public final String TAG = BestsFragment.class.getSimpleName();
  private Unbinder unbinder;
  private ArrayList<BestModel> bestModels;
  private BestAdapter bestAdapter;


  @BindView(R.id.recycleBest)
  RecyclerView recycleBest;

  @BindView(R.id.vfBest)
  ViewFlipper vfBest;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_bests, container, false);
    unbinder = ButterKnife.bind(this, view);
    getBest();
    return view;
  }

  private void getBest() {
    vfBest.setDisplayedChild(0);
    RequestHelper.builder(EndPoints.BESTS)
            .listener(getBest)
            .get();
  }

  private RequestHelper.Callback getBest = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i(TAG, "run: " + args[0].toString());
            bestModels = new ArrayList<>();
            JSONObject bestObj = new JSONObject(args[0].toString());
            boolean success = bestObj.getBoolean("success");
            String messsage = bestObj.getString("messsage");
            JSONArray bestArr = bestObj.getJSONArray("data");
            for (int i = 0; i < bestArr.length(); i++) {
              JSONObject obj = bestArr.getJSONObject(i);
              BestModel bestModel = new BestModel();
              bestModel.setLastName(obj.getString("lastName"));
              bestModel.setName(obj.getString("name"));
              bestModel.setUserId(obj.getInt("userId"));
              bestModel.setRowNumber(obj.getInt("rowNumber"));
              bestModel.setScore(obj.getInt("score"));
              bestModels.add(bestModel);
            }

            vfBest.setDisplayedChild(1);
            bestAdapter = new BestAdapter(bestModels);
            recycleBest.setAdapter(bestAdapter);

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
