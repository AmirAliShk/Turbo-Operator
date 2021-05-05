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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class ComplaintSaveResultFragment extends Fragment {
    Unbinder unbinder;

    public static RadioGroup rgBlameComplaint;
    int blameStatus;
    int lockDriver;
    String lockDay;
    int unlockDriver;
    int fined;
    int customerLock;
    int outDriver;

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

        rgBlameComplaint = view.findViewById(R.id.rgBlameComplaint);

        return view;
    }

//        complaintId	int
//        typeResult	tinyint
//        lockDriver	tinyint
//        lockDay	    tinyint
//        unlockDriver	tinyint
//        fined	        tinyint
//        customerLock	tinyint
//        outDriver	    tinyint

    public void result() {
        switch (rgBlameComplaint.getCheckedRadioButtonId()) {
            case R.id.rbBlameDriver:
                blameStatus = 1;
                break;
            case R.id.rbBlameCustomer:
                blameStatus = 2;
                break;
            case R.id.rbUnnecessaryComplaint:
                blameStatus = 3;
                break;
            case R.id.rbSomethingElse:
                blameStatus = 4;
                break;
        }
        if (chbLockDriver.isChecked())
            lockDriver = 1;
        else lockDriver = 0;

        lockDay = edtLockTime.getText().toString();

        if (chbUnlockDriver.isChecked())
            unlockDriver = 1;
        else unlockDriver = 0;

        if (chbFined.isChecked())
            fined = 1;
        else fined = 0;

        if (chbOutDriver.isChecked())
            outDriver = 1;
        else outDriver = 0;

        if (chbLockCustomer.isChecked())
            customerLock = 1;
        else customerLock = 0;

        return blameStatus,lockDriver, lockDay, unlockDriver, fined, customerLock, outDriver;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
