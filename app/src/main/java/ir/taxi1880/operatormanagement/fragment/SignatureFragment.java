package ir.taxi1880.operatormanagement.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.PaintView;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class SignatureFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.paintView)
    PaintView paintView;

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
        Bitmap returnedBitmap = Bitmap.createBitmap(paintView.getWidth(), paintView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =paintView.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        paintView.draw(canvas);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        /*TODO(najafi): use api to send this*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signature, container, false);
        TypefaceUtil.overrideFonts(view);
        unbinder = ButterKnife.bind(this, view);
        DisplayMetrics metrics = new DisplayMetrics();
        MyApplication.currentActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        paintView.normal();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
