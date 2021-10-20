package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.BestAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.BestModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

/**
 * A simple {@link Fragment} subclass.
 */
public class BestsFragment extends Fragment {
    public final String TAG = BestsFragment.class.getSimpleName();
    private Unbinder unbinder;
    private ArrayList<BestModel> bestModels;
    private BestAdapter bestAdapter;

    @OnClick(R.id.imgBack)
    void imgBack() {
        MyApplication.currentActivity.onBackPressed();
    }

    @BindView(R.id.txtGolden)
    TextView txtGolden;

    @BindView(R.id.txtSilver)
    TextView txtSilver;

    @BindView(R.id.txtBronze)
    TextView txtBronze;

    @BindView(R.id.recycleBest)
    RecyclerView recycleBest;

    @BindView(R.id.vfBest)
    ViewFlipper vfBest;

    @BindView(R.id.imgGolden)
    ImageView imgGolden;

    @BindView(R.id.imgBronze)
    ImageView imgBronze;

    @BindView(R.id.imgSilver)
    ImageView imgSilver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bests, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);
        getBest();
        return view;
    }

    private void getBest() {
        if (vfBest != null)
            vfBest.setDisplayedChild(0);
//        RequestHelper.builder(EndPoints.BESTS)
        RequestHelper.builder("http://192.168.2.34:1881/api/operator/v3/score/bests")
                .listener(getBest)
                .get();
    }

    private RequestHelper.Callback getBest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    bestModels = new ArrayList<>();
                    JSONObject bestObj = new JSONObject(args[0].toString());

                    boolean success = bestObj.getBoolean("success");
                    String messsage = bestObj.getString("messsage");

                    if (success) {
                        JSONArray bestArr = bestObj.getJSONArray("data");
                        if (bestArr.length()!= 0)
                        {
                            JSONObject obj0 = bestArr.getJSONObject(0);
                            JSONObject obj1 = bestArr.getJSONObject(1);
                            JSONObject obj2 = bestArr.getJSONObject(2);
                            if (txtGolden != null) {
                                txtGolden.setText((obj0.getInt("rowNumber") + "." + obj0.getString("name") + " " + obj0.getString("lastName")));
                            }
                            if (txtSilver != null) {
                                txtSilver.setText((obj1.getInt("rowNumber") + "." + obj1.getString("name") + " " + obj1.getString("lastName")));
                            }
                            if (txtBronze != null) {
                                txtBronze.setText((obj2.getInt("rowNumber") + "." + obj2.getString("name") + " " + obj2.getString("lastName")));
                            }
                            for (int i = 3; i < bestArr.length(); i++) {
                                JSONObject obj = bestArr.getJSONObject(i);
                                BestModel bestModel = new BestModel();
                                bestModel.setLastName(obj.getString("lastName"));
                                bestModel.setName(obj.getString("name"));
                                bestModel.setUserId(obj.getInt("userId"));
                                bestModel.setRowNumber(obj.getInt("rowNumber"));
                                bestModel.setScore(obj.getInt("score"));
                                bestModels.add(bestModel);
                            }
                            bestAdapter = new BestAdapter(bestModels);
                            if (recycleBest != null)
                                recycleBest.setAdapter(bestAdapter);
                        }

                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(messsage)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }

                    if (bestModels.size() == 0) {
                        if (vfBest != null)
                            vfBest.setDisplayedChild(2);
                    } else {
                        if (vfBest != null)
                            vfBest.setDisplayedChild(1);
                    }

                } catch (Exception e) {
                    if (vfBest != null)
                        vfBest.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "BestsFragment class, getBest onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfBest != null)
                    vfBest.setDisplayedChild(3);
            });
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
