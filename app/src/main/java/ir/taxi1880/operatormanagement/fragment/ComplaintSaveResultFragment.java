package ir.taxi1880.operatormanagement.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.SpinnerAdapter;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentComplaintSaveResultBinding;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.TypeServiceModel;

public class ComplaintSaveResultFragment extends Fragment {
    Unbinder unbinder;
    FragmentComplaintSaveResultBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComplaintSaveResultBinding.inflate(getLayoutInflater());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        TypefaceUtil.overrideFonts(binding.getRoot());

        initSpinner();
        binding.spComplaintType.setEnabled(false);
        binding.llComplaintType.setEnabled(false);
        binding.edtLockTime.setEnabled(false);
        binding.edtLockTime.addTextChangedListener(edtLockTimeTextWatcher);
        result();
        return binding.getRoot();
    }

    TextWatcher edtLockTimeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DataHolder.getInstance().setLockDay(binding.edtLockTime.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            DataHolder.getInstance().setLockDay(binding.edtLockTime.getText().toString());
        }
    };

    private void initSpinner() {
        ArrayList<TypeServiceModel> typeServiceModels = new ArrayList<>();
        ArrayList<String> serviceList = new ArrayList<String>();
        try {
            JSONArray serviceArr = new JSONArray(MyApplication.prefManager.getComplaint());
            serviceList.add(0, "انتخاب نشده");
            for (int i = 0; i < serviceArr.length(); i++) {
                JSONObject serviceObj = serviceArr.getJSONObject(i);
                TypeServiceModel typeServiceModel = new TypeServiceModel();
                typeServiceModel.setName(serviceObj.getString("ShektypeSharh"));
                typeServiceModel.setId(serviceObj.getInt("sheKtypeId"));
                typeServiceModels.add(typeServiceModel);
                serviceList.add(serviceObj.getString("ShektypeSharh"));
            }
            if (binding.spComplaintType == null)
                return;

            binding.spComplaintType.setEnabled(true);

            binding.spComplaintType.setAdapter(new SpinnerAdapter(MyApplication.currentActivity, R.layout.item_spinner, serviceList));

            binding.spComplaintType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        DataHolder.getInstance().setLockReason((byte) 0);
                        return;
                    }
                    DataHolder.getInstance().setLockReason((byte) typeServiceModels.get(position - 1).getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void result() {

        binding.rgBlameComplaint.setOnCheckedChangeListener((radioGroup, i) -> {
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
                case R.id.rbCustomerNotAnswer:
                    DataHolder.getInstance().setComplaintResult((byte) 5);
                    break;
            }
        });

        binding.chbLockDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.chbLockDriver.isChecked()) {
                DataHolder.getInstance().setLockDriver(true);
                binding.edtLockTime.setEnabled(true);
                binding.chbUnlockDriver.setEnabled(false);
                binding.llComplaintType.setEnabled(true);
                binding.spComplaintType.setEnabled(true);
            } else if (!binding.chbLockDriver.isChecked()) {
                DataHolder.getInstance().setLockDriver(false);
                binding.edtLockTime.setText(null);
                binding.edtLockTime.setEnabled(false);
                binding.chbUnlockDriver.setEnabled(true);
                binding.spComplaintType.setEnabled(false);
                binding.llComplaintType.setEnabled(false);
            }
        });

        binding.chbUnlockDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.chbUnlockDriver.isChecked()) {
                DataHolder.getInstance().setUnlockDriver(true);
                binding.chbLockDriver.setEnabled(false);
            } else {
                DataHolder.getInstance().setUnlockDriver(false);
                binding.chbLockDriver.setEnabled(true);
            }
        });

        binding.chbFined.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.chbFined.isChecked()) {
                DataHolder.getInstance().setFined(true);
            } else DataHolder.getInstance().setFined(false);
        });

        binding.chbOutDriver.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.chbOutDriver.isChecked()) {
                DataHolder.getInstance().setOutDriver(true);
            } else DataHolder.getInstance().setOutDriver(false);
        });

        binding.chbLockCustomer.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.chbLockCustomer.isChecked()) {
                DataHolder.getInstance().setCustomerLock(true);
            } else DataHolder.getInstance().setCustomerLock(false);
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        DataHolder.getInstance().setComplaintResult((byte) 0);
        DataHolder.getInstance().setLockReason((byte) 0);
        DataHolder.getInstance().setLockDriver(false);
        DataHolder.getInstance().setLockDay("");
        DataHolder.getInstance().setUnlockDriver(false);
        DataHolder.getInstance().setFined(false);
        DataHolder.getInstance().setCustomerLock(false);
        DataHolder.getInstance().setOutDriver(false);

        binding.rgBlameComplaint.clearCheck();
        binding.chbLockDriver.setSelected(false);
        binding.chbUnlockDriver.setSelected(false);
        binding.chbFined.setSelected(false);
        binding.chbOutDriver.setSelected(false);
        binding.chbLockCustomer.setSelected(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        DataHolder.getInstance().setComplaintResult((byte) 0);
        DataHolder.getInstance().setLockReason((byte) 0);
        DataHolder.getInstance().setLockDriver(false);
        DataHolder.getInstance().setLockDay("");
        DataHolder.getInstance().setUnlockDriver(false);
        DataHolder.getInstance().setFined(false);
        DataHolder.getInstance().setCustomerLock(false);
        DataHolder.getInstance().setOutDriver(false);

        binding.rgBlameComplaint.clearCheck();
        binding.chbLockDriver.setSelected(false);
        binding.chbUnlockDriver.setSelected(false);
        binding.chbFined.setSelected(false);
        binding.chbOutDriver.setSelected(false);
        binding.chbLockCustomer.setSelected(false);
    }

}
