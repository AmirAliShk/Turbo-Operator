package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        View view = inflater.inflate(R.layout.fragment_complaint_save_result, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        edtLockTime.setEnabled(false);
        edtLockTime.addTextChangedListener(edtLockTimeTextWatcher);
        result();
        return view;
    }

    TextWatcher edtLockTimeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DataHolder.getInstance().setLockDay(edtLockTime.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            DataHolder.getInstance().setLockDay(edtLockTime.getText().toString());
        }
    };

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

        chbLockDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (chbLockDriver.isChecked()) {
                DataHolder.getInstance().setLockDriver(true);
                edtLockTime.setEnabled(true);
                chbUnlockDriver.setEnabled(false);
            } else if (!chbLockDriver.isChecked()) {
                DataHolder.getInstance().setLockDriver(false);
                edtLockTime.setText(null);
                edtLockTime.setEnabled(false);
                chbUnlockDriver.setEnabled(true);
            }
        });

        chbUnlockDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (chbUnlockDriver.isChecked()) {
                DataHolder.getInstance().setUnlockDriver(true);
                chbLockDriver.setEnabled(false);
            } else {
                DataHolder.getInstance().setUnlockDriver(false);
                chbLockDriver.setEnabled(true);
            }
        });

        chbFined.setOnCheckedChangeListener((compoundButton, b) -> {
            if (chbFined.isChecked()) {
                DataHolder.getInstance().setFined(true);
            } else DataHolder.getInstance().setFined(false);
        });

        chbOutDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (chbOutDriver.isChecked()) {
                DataHolder.getInstance().setOutDriver(true);
            } else DataHolder.getInstance().setOutDriver(false);
        });

        chbLockCustomer.setOnCheckedChangeListener((compoundButton, b) -> {
            if (chbLockCustomer.isChecked()) {
                DataHolder.getInstance().setCustomerLock(true);
            } else DataHolder.getInstance().setCustomerLock(false);
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        DataHolder.getInstance().setComplaintResult((byte) 0);
        DataHolder.getInstance().setLockDriver(false);
        DataHolder.getInstance().setLockDay("");
        DataHolder.getInstance().setUnlockDriver(false);
        DataHolder.getInstance().setFined(false);
        DataHolder.getInstance().setCustomerLock(false);
        DataHolder.getInstance().setOutDriver(false);

        rgBlameComplaint.clearCheck();
        chbLockDriver.setSelected(false);
        chbUnlockDriver.setSelected(false);
        chbFined.setSelected(false);
        chbOutDriver.setSelected(false);
        chbLockCustomer.setSelected(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        DataHolder.getInstance().setComplaintResult((byte) 0);
        DataHolder.getInstance().setLockDriver(false);
        DataHolder.getInstance().setLockDay("");
        DataHolder.getInstance().setUnlockDriver(false);
        DataHolder.getInstance().setFined(false);
        DataHolder.getInstance().setCustomerLock(false);
        DataHolder.getInstance().setOutDriver(false);

        rgBlameComplaint.clearCheck();
        chbLockDriver.setSelected(false);
        chbUnlockDriver.setSelected(false);
        chbFined.setSelected(false);
        chbOutDriver.setSelected(false);
        chbLockCustomer.setSelected(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
