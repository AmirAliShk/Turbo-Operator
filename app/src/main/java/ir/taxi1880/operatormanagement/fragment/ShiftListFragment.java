package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.adapter.ShiftAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentShiftListBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ShiftModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ShiftListFragment extends Fragment {
    public static final String TAG = ShiftListFragment.class.getSimpleName();
    FragmentShiftListBinding binding;
    private ArrayList<ShiftModel> shiftModels;
    private ShiftAdapter shiftAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShiftListBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        shiftModels = new ArrayList<>();

        getShifts();

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    private void getShifts() {
//        if (binding.vfShift != null)
            binding.vfShift.setDisplayedChild(0);

        RequestHelper.builder(EndPoints.GET_SHIFTS)
                .listener(onGetShifts)
                .get();
    }

    private RequestHelper.Callback onGetShifts = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    Log.i(TAG, "onResponse: " + args[0].toString());
                    JSONArray arr = new JSONArray(args[0].toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        ShiftModel shiftModel = new ShiftModel();
                        shiftModel.setShiftId(object.getInt("id"));
                        shiftModel.setShiftDate(object.getString("date"));
                        shiftModel.setShiftName(object.getString("shiftName"));
                        shiftModel.setShiftTime(object.getString("time"));
                        shiftModels.add(shiftModel);
                    }

                    shiftAdapter = new ShiftAdapter(shiftModels);
//                    if (binding.listShift != null)
                        binding.listShift.setAdapter(shiftAdapter);

                    if (shiftModels.size() == 0) {
//                        if (binding.vfShift != null)
                            binding.vfShift.setDisplayedChild(2);
                    } else {
//                        if (binding.vfShift != null)
                            binding.vfShift.setDisplayedChild(1);
                    }
                } catch (Exception e) {
//                    if (binding.vfShift != null)
                        binding.vfShift.setDisplayedChild(3);
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetShifts onResponse method");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
//                if (binding.vfShift != null)
                    binding.vfShift.setDisplayedChild(3);
            });
        }
    };
}