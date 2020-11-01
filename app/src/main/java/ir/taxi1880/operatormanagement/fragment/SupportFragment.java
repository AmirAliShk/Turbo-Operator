package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SupportFragment extends Fragment {
  Unbinder unbinder;

  @OnClick(R.id.imgBack)
  void onBackPress() {
    MyApplication.currentActivity.onBackPressed();
  }

  @OnClick(R.id.imgSearch)
  void onSearchPress() {
  }

  @OnClick(R.id.imgClear)
  void onClearPress() {
    FragmentHelper.toFragment(MyApplication.currentActivity, new TripDetailsFragment()).replace();
  }

  @OnClick(R.id.imgSearchType)
  void onSearchTypePress() {
  }

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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}