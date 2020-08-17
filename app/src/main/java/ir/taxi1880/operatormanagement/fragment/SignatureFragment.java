package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gcacace.signaturepad.views.SignaturePad;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SignatureFragment extends Fragment {

  private Unbinder unbinder;

  @BindView(R.id.paintView)
  SignaturePad paintView;

  @OnClick(R.id.imgBack)
  void onBack() {
    MyApplication.currentActivity.onBackPressed();
  }

  @OnClick(R.id.btnClearSignature)
  void btnClearAssignment() {
    if (paintView != null)
      paintView.clear();
  }

  @OnClick(R.id.btnSubmitSignature)
  void btnSubmitSignature() {
    /*TODO(najafi): complete this*/
    if (paintView != null) {
      String svgPath = paintView.getSignatureSvg();
      Log.i("TAG", "btnSubmitSignature: "+svgPath);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_signature, container, false);
    TypefaceUtil.overrideFonts(view);
    unbinder = ButterKnife.bind(this, view);

    paintView.setOnSignedListener(new SignaturePad.OnSignedListener() {
      @Override
      public void onStartSigning() {
      }

      @Override
      public void onSigned() {
      }

      @Override
      public void onClear() {
      }
    });

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
