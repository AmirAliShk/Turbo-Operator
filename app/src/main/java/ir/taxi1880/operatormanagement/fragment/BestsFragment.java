package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.BestAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentBestsBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.BestModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class BestsFragment extends Fragment {
    public final String TAG = BestsFragment.class.getSimpleName();
    FragmentBestsBinding binding;
    private ArrayList<BestModel> bestModels;
    private BestAdapter bestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBestsBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());
        getBest();
        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());
        return binding.getRoot();
    }

    private void getBest() {
        if (binding.vfBest != null)
            binding.vfBest.setDisplayedChild(0);
        RequestHelper.builder(EndPoints.BESTS)
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
                        if (bestArr.length() != 0) {
                            JSONObject obj0 = bestArr.getJSONObject(0);
                            JSONObject obj1 = bestArr.getJSONObject(1);
                            JSONObject obj2 = bestArr.getJSONObject(2);
                            if (binding.txtGolden != null) {
                                binding.txtGolden.setText((obj0.getInt("rowNumber") + "." + obj0.getString("name") + " " + obj0.getString("lastName")));
                            }
                            if (binding.txtSilver != null) {
                                binding.txtSilver.setText((obj1.getInt("rowNumber") + "." + obj1.getString("name") + " " + obj1.getString("lastName")));
                            }
                            if (binding.txtBronze != null) {
                                binding.txtBronze.setText((obj2.getInt("rowNumber") + "." + obj2.getString("name") + " " + obj2.getString("lastName")));
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
                            if (binding.recycleBest != null)
                                binding.recycleBest.setAdapter(bestAdapter);
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
                        if (binding.vfBest != null)
                            binding.vfBest.setDisplayedChild(2);
                    } else {
                        if (binding.vfBest != null)
                            binding.vfBest.setDisplayedChild(1);
                    }

                } catch (Exception e) {
                    if (binding.vfBest != null)
                        binding.vfBest.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, getBest onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfBest != null)
                    binding.vfBest.setDisplayedChild(3);
            });
        }
    };
}