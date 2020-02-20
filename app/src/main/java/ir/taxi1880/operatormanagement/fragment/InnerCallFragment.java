package ir.taxi1880.operatormanagement.fragment;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class InnerCallFragment extends Fragment {

  public static final String TAG = InnerCallFragment.class.getSimpleName();
  Unbinder unbinder;

  @BindView(R.id.rlRoot)
  RelativeLayout rlRoot;

  @BindView(R.id.txtCallerName)
  TextView txtCallerName;

  @BindView(R.id.txtCallerNum)
  TextView txtCallerNum;

  @BindView(R.id.blurView)
  BlurView blurView;

  @OnClick(R.id.imgReject)
  void onPressReject(){
    FragmentHelper.toFragment(MyApplication.currentActivity,new TripRegisterFragment()).remove();
    remove();
  }

 @OnClick(R.id.imgAnswer)
  void onPressAnswer(){
   FragmentHelper.toFragment(MyApplication.currentActivity,new TripRegisterFragment()).remove();
   remove();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inner_call, container, false);
    unbinder = ButterKnife.bind(this, view);
    TypefaceUtil.overrideFonts(view,MyApplication.IraSanSBold);

    blurry();

    return view;
  }

  private void blurry() {
    float radius = 20f;

    View decorView = MyApplication.currentActivity.getWindow().getDecorView();
    //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
    ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
    //Set drawable to draw in the beginning of each blurred frame (Optional).
    //Can be used in case your layout has a lot of transparent space and your content
    //gets kinda lost after after blur is applied.
    Drawable windowBackground = decorView.getBackground();

    blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(new RenderScriptBlur(MyApplication.currentActivity))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true);
  }

  private void remove() {
    try {
      if (getFragmentManager() != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          Window window = MyApplication.currentActivity.getWindow();
          window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
          window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryLighter));
        }

//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down);
//
//        android.app.Fragment currentFrag = getFragmentManager().findFragmentById(R.id.toast_container);
//        transaction.remove(currentFrag).commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    remove();
  }
}
