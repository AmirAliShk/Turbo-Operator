package ir.taxi1880.operatormanagement.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

  public static final String TAG=AccountFragment.class.getSimpleName();
  Unbinder unbinder;
  String a;
  private int keyDel;

//  @OnClick(R.id.imgBack)
//  void onBack() {
//    MyApplication.currentActivity.onBackPressed();
//  }

  @BindView(R.id.txtOperatorName)
  TextView txtOperatorName;

  @BindView(R.id.edtCardNumber)
  EditText edtCardNumber;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view= inflater.inflate(R.layout.fragment_account, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view);

    txtOperatorName.setText(MyApplication.prefManager.getOperatorName());

    edtCardNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean flag = true;
        String eachBlock[] = edtCardNumber.getText().toString().split(" ");
        for (int i = 0; i < eachBlock.length; i++) {
          if (eachBlock[i].length() > 4) {
            flag = false;
          }
        }

        if (flag) {
          edtCardNumber.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL)
              keyDel = 1;
            return false;
          });

          if (keyDel == 0) {
            if (((edtCardNumber.getText().length() + 1) % 5) == 0) {

              if (edtCardNumber.getText().toString().split(" ").length <= 3) {
                edtCardNumber.setText(edtCardNumber.getText() + " ");
                edtCardNumber.setSelection(edtCardNumber.getText().length());
              }
            }
            a = edtCardNumber.getText().toString();
          } else {
            a = edtCardNumber.getText().toString();
            keyDel = 0;
          }

        } else {
          edtCardNumber.setText(a);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });


    return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
