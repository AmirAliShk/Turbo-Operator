package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentOpScoreBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class OpScoreFragment extends Fragment {
    public final String TAG = OpScoreFragment.class.getSimpleName();
    FragmentOpScoreBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOpScoreBinding.inflate(inflater, container, false);
        getSingle();
        return binding.getRoot();
    }

    private void getSingle() {
        RequestHelper.builder(EndPoints.SINGLE)
                .listener(getSingle)
                .get();
    }

    private RequestHelper.Callback getSingle = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    Log.i(TAG, "run: " + args[0].toString());
                    JSONObject scoreObj = new JSONObject(args[0].toString());
                    boolean success = scoreObj.getBoolean("success");
                    String message = scoreObj.getString("message");

                    if (success) {
                        JSONObject bestObj = scoreObj.getJSONObject("data");
                        String totalScore = bestObj.getString("totalScore");
                        String monthScore = bestObj.getString("monthScore");
                        String weekScore = bestObj.getString("weekScore");
                        String todayScore = bestObj.getString("todayScore");
                        if (binding.txtTotalScore != null)
                            binding.txtTotalScore.setText(totalScore + " totalScore");
                        if (binding.txtMonthScore != null)
                            binding.txtMonthScore.setText(monthScore + " monthScore");
                        if (binding.txtWeekScore != null)
                            binding.txtWeekScore.setText(weekScore + " weekScore");
                        if (binding.txtTodayScore != null)
                            binding.txtTodayScore.setText(todayScore + " todayScore");
                    } else {
                        new GeneralDialog()
                                .title("هشدار")
                                .message(message)
                                .secondButton("باشه", null)
                                .cancelable(false)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, getSingle onResponse method");
                }
            });
        }
    };
}