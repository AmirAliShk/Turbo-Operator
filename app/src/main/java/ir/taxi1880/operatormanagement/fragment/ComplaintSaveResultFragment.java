package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class ComplaintSaveResultFragment extends Fragment {
    Unbinder unbinder;

    @BindView(R.id.rgBlameComplaint)
    RadioGroup rgBlameComplaint;

    @BindView(R.id.rbBlameDriver)
    RadioButton rbBlameDriver;

    @BindView(R.id.rbBlameCustomer)
    RadioButton rbBlameCustomer;

    @BindView(R.id.rbUnnecessaryComplaint)
    RadioButton rbUnnecessaryComplaint;

    @BindView(R.id.rbSomethingElse)
    RadioButton rbSomethingElse;

    @BindView(R.id.chbLockDriver)
    CheckBox chbLockDriver;

    @BindView(R.id.edtLockTime)
    EditText edtLockTime;

    @BindView(R.id.chbUnlockDriver)
    CheckBox chbUnlockDriver;

    @BindView(R.id.chbFined)
    CheckBox chbFined;

    @BindView(R.id.chbOutDriver)
    CheckBox chbOutDriver;

    @BindView(R.id.chbLockCustomer)
    CheckBox chbLockCustomer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_save_result, container, false);//todo
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        edtLockTime.setEnabled(false);

        return view;
    }

    public void result() {

        rgBlameComplaint.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rbBlameDriver:
                    DataHolder.getInstance().setComplaintResult((byte) 1);
                    break;
                case R.id.rbBlameCustomer:
                    DataHolder.getInstance().setComplaintResult((byte) 2);
                    break;
                case R.id.rbUnnecessaryComplaint:
                    DataHolder.getInstance().setComplaintResult((byte) 3);
                    break;
                case R.id.rbSomethingElse:
                    DataHolder.getInstance().setComplaintResult((byte) 4);
                    break;
            }
        });

        if (chbLockDriver.isChecked()) {
            edtLockTime.setEnabled(true);
            DataHolder.getInstance().setLockDriver(true);
        } else DataHolder.getInstance().setLockDriver(false);

        if (chbLockDriver.isChecked() && edtLockTime.getText().toString() == null) {
            MyApplication.Toast("لطفا تعداد روزهای قفل راننده را انتخاب کنید", Toast.LENGTH_SHORT);
        } else {
            DataHolder.getInstance().setLockDay(edtLockTime.getText().toString());
        }

        if (chbUnlockDriver.isChecked())
            DataHolder.getInstance().setUnlockDriver(true);
        else DataHolder.getInstance().setUnlockDriver(false);

        if (chbFined.isChecked())
            DataHolder.getInstance().setFined(true);
        else DataHolder.getInstance().setFined(false);

        if (chbOutDriver.isChecked())
            DataHolder.getInstance().setOutDriver(true);
        else DataHolder.getInstance().setOutDriver(false);

        if (chbLockCustomer.isChecked())
            DataHolder.getInstance().setCustomerLock(true);
        else DataHolder.getInstance().setCustomerLock(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
