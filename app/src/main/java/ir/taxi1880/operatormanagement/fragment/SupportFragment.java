package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.SearchFilterDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class SupportFragment extends Fragment {
  Unbinder unbinder;
  int searchCase = 2;

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @BindView(R.id.vfTrip)
  ViewFlipper vfTrip;

  @OnClick(R.id.imgSearch)
  void onSearchPress() {
    String searchText=edtSearchTrip.getText().toString();

    searchService(searchText);
  }

  @OnClick(R.id.imgClear)
  void onClearPress() {
    FragmentHelper.toFragment(MyApplication.currentActivity, new TripDetailsFragment()).replace();
  }

  @OnClick(R.id.imgSearchType)
  void onSearchTypePress() {
    new SearchFilterDialog().show(type -> {
      this.searchCase = type;
    });
  }

  @OnClick(R.id.llExtendedTime)
  void onExtendedTimePress() {
    chbExtendedTime.setChecked(!chbExtendedTime.isChecked());
  }

  @BindView(R.id.chbExtendedTime)
  CheckBox chbExtendedTime;

  @BindView(R.id.edtSearchTrip)
  EditText edtSearchTrip;

  @BindView(R.id.recycleTrip)
  RecyclerView recycleTrip;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_support, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    return view;
  }

  private void searchService(String searchText) {
   if (vfTrip!=null){
     vfTrip.setDisplayedChild(0);
   }

    int extendedTime = chbExtendedTime.isChecked() ? 1 : 0;

    RequestHelper.builder(EndPoints.SEARCH_SERVICE)
            .addParam("searchCase", searchCase)
            .addParam("searchText", searchText)
            .addParam("allServices", extendedTime)
            .listener(onGetTripList)
            .post();
  }

  RequestHelper.Callback onGetTripList = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            Log.i("SupportFragment", "run: "+args[0].toString());

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(() -> {
        if (vfTrip!=null){
          vfTrip.setDisplayedChild(3);
        }
      });
    }
  };

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}