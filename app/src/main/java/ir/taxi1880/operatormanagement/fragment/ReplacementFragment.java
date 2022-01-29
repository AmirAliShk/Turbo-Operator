package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentReplacementBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.OperatorDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ReplacementFragment extends Fragment {
    public static final String TAG = ReplacementFragment.class.getSimpleName();
    FragmentReplacementBinding binding;
    String[] shift = {"صبح", "عصر", "شب", "استراحت"};
    GeneralDialog generalDialog = new GeneralDialog();
    int shiftId;
    String shiftDate;
    String shiftName;
    int opId = 0;

    @SuppressLint("Clickab leViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReplacementBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot());

        Bundle bundle = getArguments();
        if (bundle != null) {
            shiftDate = bundle.getString("shiftDate");
            shiftName = bundle.getString("shiftName");
        }

        binding.btnSubmit.setOnClickListener(view -> {
            if (binding.txtOperator.getText().equals("")) {
                MyApplication.Toast("اپراتور مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT);
                return;
            }

            new GeneralDialog()
                    .title("ثبت درخواست")
                    .message(" شما میخواهید خانم " + binding.txtOperator.getText() + " در تاریخ " + shiftDate + " در شیفت " + shiftName + " به جای شما حضور یابد.")
                    .firstButton("بله", () -> {
                        shiftReplacementRequest();
                        binding.txtOperator.setText("");
                    })
                    .secondButton("خیر", null)
                    .show();
        });

        binding.llOperator.setOnClickListener(view -> {
            switch (shiftName) {
                case "صبح":
                    shiftId = 1;
                    break;
                case "عصر":
                    shiftId = 2;
                    break;
                case "شب":
                    shiftId = 3;
                    break;
                case "استراحت":
                    shiftId = 4;
                    break;
                default:
                    shiftId = 0;
                    break;
            }
            getOnlineOperator();
        });

        binding.imgBack.setOnClickListener(view -> MyApplication.currentActivity.onBackPressed());

        return binding.getRoot();
    }

    private void shiftReplacementRequest() {
        RequestHelper.builder(EndPoints.SHIFT_REPLACEMENT_REQUEST)
                .addParam("intendedOperatorId", opId)
                .addParam("shift", shiftId)
                .addParam("date", shiftDate)
                .listener(onShiftReplacementRequest)
                .post();
    }

    RequestHelper.Callback onShiftReplacementRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int status = object.getInt("status");
                    String msgStatus = object.getString("messageStatus");
                    generalDialog.firstButton("باشه", () -> generalDialog.dismiss());
                    generalDialog.message(msgStatus);
                    generalDialog.title("تایید");
                    generalDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onShiftReplacementRequest onResponse method");
                }
            });
        }
    };

    private void getOnlineOperator() {
        if (binding.loader != null)
            binding.loader.setVisibility(View.VISIBLE);

        RequestHelper.builder(EndPoints.GET_SHIFT_OPERATOR)
                .addParam("shiftDate", shiftDate)
                .addParam("shiftId", shiftId + "")
                .listener(onGetOnlineOperator)
                .post();
    }

    RequestHelper.Callback onGetOnlineOperator = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONArray operatorArr = new JSONArray(args[0].toString());
                    MyApplication.prefManager.setOperatorList(operatorArr.toString());

                    if (binding.loader != null)
                        binding.loader.setVisibility(View.GONE);

                    new OperatorDialog().show((op) -> {
                        if (binding.txtOperator != null)
                            binding.txtOperator.setText(op.getOperatorName());
                        opId = op.getOperatorId();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, onGetOnlineOperator onResponse method");
                }
            });
        }
    };
}