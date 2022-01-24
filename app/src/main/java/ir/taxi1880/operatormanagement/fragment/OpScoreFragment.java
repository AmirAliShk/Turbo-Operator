package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class OpScoreFragment extends Fragment {
    public final String TAG = OpScoreFragment.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.txtTotalScore)
    TextView txtTotalScore;

    @BindView(R.id.txtMonthScore)
    TextView txtMonthScore;

    @BindView(R.id.txtWeekScore)
    TextView txtWeekScore;

    @BindView(R.id.txtTodayScore)
    TextView txtTodayScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_op_score, container, false);
        unbinder = ButterKnife.bind(this, view);
        getSingle();
        return view;
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
                        if (txtTotalScore != null)
                            txtTotalScore.setText(totalScore + " totalScore");
                        if (txtMonthScore != null)
                            txtMonthScore.setText(monthScore + " monthScore");
                        if (txtWeekScore != null)
                            txtWeekScore.setText(weekScore + " weekScore");
                        if (txtTodayScore != null)
                            txtTodayScore.setText(todayScore + " todayScore");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}