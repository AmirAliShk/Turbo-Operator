package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ShiftAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ShiftModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftListFragment extends Fragment {
  public static final String TAG = ShiftListFragment.class.getSimpleName();
  private Unbinder unbinder;
  private ArrayList<ShiftModel> shiftModels;
  private ShiftAdapter shiftAdapter;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.listShift)
  ListView listShift;

  @BindView(R.id.txtNull)
  TextView txtNull;

  @BindView(R.id.vfShift)
  ViewFlipper vfShift;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shift_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    shiftModels = new ArrayList<>();

    getShifts(MyApplication.prefManager.getUserCode());

    return view;
  }

  private void getShifts(int operatorId) {
    if (vfShift != null)
      vfShift.setDisplayedChild(0);

    RequestHelper.builder(EndPoints.GET_SHIFTS)
            .addParam("operatorId", operatorId)
            .listener(onGetShifts)
            .post();

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

          shiftAdapter = new ShiftAdapter(shiftModels, MyApplication.context);
          if (listShift != null)
            listShift.setAdapter(shiftAdapter);

          if (shiftModels.size() == 0) {
            if (vfShift != null)
              vfShift.setDisplayedChild(2);
          } else {
            if (vfShift != null)
              vfShift.setDisplayedChild(1);
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
