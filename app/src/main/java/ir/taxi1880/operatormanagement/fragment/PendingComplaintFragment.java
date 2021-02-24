package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.dialog.PendingComplaintOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.SaveResultDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class PendingComplaintFragment extends Fragment {
    Unbinder unbinder;

    @OnClick(R.id.btnSaveResult)
    void onSaveResult() {
        new SaveResultDialog()
                .show();
    }

    @OnClick(R.id.btnOptions)
    void onOptions() {
        new PendingComplaintOptionsDialog()
                .show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_complaint, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        return view;
    }
}
