package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AllComplaintAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;

public class AllComplaintFragment extends Fragment {
    Unbinder unbinder;

    @BindView(R.id.complaintList)
    ListView complaintList;

    AllComplaintAdapter mAdapter;
    ArrayList<AllComplaintModel> allComplaintModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_complaint, container, false);
        unbinder = ButterKnife.bind(this, view);
        allComplaintModels = new ArrayList<>();

        allComplaintModels.add(new AllComplaintModel("28/4/99","12:20"));
        allComplaintModels.add(new AllComplaintModel("13/7/99","17:20"));
        allComplaintModels.add(new AllComplaintModel("25/3/99","08:20"));

        mAdapter = new AllComplaintAdapter(MyApplication.currentActivity, allComplaintModels);
        complaintList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
