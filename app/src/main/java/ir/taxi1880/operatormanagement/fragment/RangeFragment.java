package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.NumberPadAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class RangeFragment extends Fragment {
  String TAG = RangeFragment.class.getSimpleName();
  Unbinder unbinder;
  boolean status = false;

  @BindView(R.id.gridNumber)
  GridView gridNumber;

  @BindView(R.id.txtStation)
  TextView txtStation;

  @OnClick(R.id.btnDelete)
  void onDelete() {
    txtStation.setText("");
  }

  @OnClick(R.id.btnSubmit)
  void onSubmit() {
    MyApplication.Toast("submit", Toast.LENGTH_SHORT);
  }

  @OnClick(R.id.btnHelp)
  void onHelp() {
    MyApplication.Toast("Help", Toast.LENGTH_SHORT);
  }

  @BindView(R.id.btnActivate)
  Button btnActivate;

  @BindView(R.id.btnDeActivate)
  Button btnDeActivate;

  @OnClick(R.id.btnActivate)
  void onActivePress() {
    changeStatus();
  }

  @OnClick(R.id.btnDeActivate)
  void onDeActivePress() {
    changeStatus();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_range, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    gridNumber.setAdapter(new NumberPadAdapter(MyApplication.context, new NumberPadAdapter.NumberListener() {
      @Override
      public void onResult(String character) {
        switch (character) {
          case "0":
            setNumber("0");
            break;
          default:
            setNumber(character);
        }

      }
    }));

    return view;
  }

  @SuppressLint("SetTextI18n")
  private void setNumber(String c) {
    String temp = txtStation.getText().toString();
    if (temp.length() == 3) {
      txtStation.setText(StringHelper.toPersianDigits(temp.substring(0, 2) + c));
    } else {
      txtStation.setText(StringHelper.toPersianDigits(temp + c));
    }
  }

  private void changeStatus() {
    if (status) {
      status = false;
      MyApplication.Toast("شما خارج شدید", Toast.LENGTH_SHORT);
      MyApplication.prefManager.setActivateStatus(false);
      if (btnActivate != null)
        btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge);
        btnDeActivate.setTextColor(Color.parseColor("#ffffff"));
      }
    } else {
      status = true;
      MyApplication.Toast("شما وارد شدید", Toast.LENGTH_SHORT);
      if (btnActivate != null)
        btnActivate.setBackgroundResource(R.drawable.bg_green_edge);
      MyApplication.prefManager.setActivateStatus(true);
      if (btnDeActivate != null) {
        btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"));
        btnDeActivate.setTextColor(Color.parseColor("#000000"));
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}