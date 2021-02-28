package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.warkiz.widget.IndicatorSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.PendingComplaintOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.SaveResultDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;

public class PendingComplaintFragment extends Fragment {
    Unbinder unbinder;
    DataBase dataBase;

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

    @BindView(R.id.txtTripDate)
    TextView txtTripDate;

    @BindView(R.id.txtTripTime)
    TextView txtTripTime;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.txtStationCode)
    TextView txtStationCode;

    @BindView(R.id.imgPlay)
    ImageView imgPlay;

    @BindView(R.id.skbTimer)
    IndicatorSeekBar skbTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_complaint, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);

        dataBase = new DataBase(MyApplication.context);

//        AllComplaintModel model = dataBase.getComplaintRow(1);
//        txtAddress.setText(StringHelper.toPersianDigits(model.getAddress()));
//        txtStationCode.setText(StringHelper.toPersianDigits("199"));
//        txtCity.setText(StringHelper.toPersianDigits("مشهد"));
//        txtDescription.setText(StringHelper.toPersianDigits(model.getDescription()));
//        txtTripTime.setText(StringHelper.toPersianDigits(model.getSendTime()));
//        txtTripDate.setText(StringHelper.toPersianDigits(model.getDate()));

        return view;
    }
}
