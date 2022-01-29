package ir.taxi1880.operatormanagement.fragment;

import static ir.taxi1880.operatormanagement.fragment.ComplaintTripDetailsFragment.pauseVoice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.adapter.ComplaintPagerAdapter;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.NonSwipeableViewPager;
import ir.taxi1880.operatormanagement.databinding.FragmentComplaintDetailBinding;
import ir.taxi1880.operatormanagement.dialog.ComplaintOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintDetailsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ComplaintDetailFragment extends Fragment {
    public static final String TAG = ComplaintDetailFragment.class.getSimpleName();
    FragmentComplaintDetailBinding binding;
    int statusModel;
    public static ComplaintDetailsModel complaintDetailsModel;
    public static NonSwipeableViewPager vpRegisterDriver;

    public ComplaintDetailFragment(ComplaintDetailsModel complaintsModel) {
        this.complaintDetailsModel = complaintsModel;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComplaintDetailBinding.inflate(inflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TypefaceUtil.overrideFonts(binding.getRoot());

        vpRegisterDriver = binding.vpRegisterDriver;

        statusModel = complaintDetailsModel.getStatus();

        setupViewPager(vpRegisterDriver);

        if (binding.indicator != null) {
            binding.indicator.setCurrentStep(complaintDetailsModel.getStatus());
        }

        refreshStep(complaintDetailsModel.getStatus());

        binding.btnOptions.setOnClickListener(view -> new ComplaintOptionsDialog()
                .show(complaintDetailsModel.getCustomerPhoneNumber(), complaintDetailsModel.getCustomerMobileNumber(), complaintDetailsModel.getTaxicode()));

        binding.btnTripDetails.setOnClickListener(view -> {
            if (binding.vfTripDetails != null)
                binding.vfTripDetails.setDisplayedChild(1);

            Bundle bundle = new Bundle();
            bundle.putString("id", complaintDetailsModel.getServiceId() + "");
            FragmentHelper.toFragment(MyApplication.currentActivity, new PassengerTripSupportDetailsFragment()).setArguments(bundle).replace();
        });

        binding.btnSaveResult.setOnClickListener(view -> {
            //        complaintId	int
            //        typeResult	tinyint
            //        lockDriver	tinyint
            //        lockDay	    tinyint
            //        unlockDriver	tinyint
            //        fined	        tinyint
            //        customerLock	tinyint
            //        outDriver	    tinyint

            if (DataHolder.getInstance().getComplaintResult() == 0) {
                MyApplication.Toast("لطفا مقصر را مشخص کنید", Toast.LENGTH_SHORT);
                return;
            }

            if (DataHolder.getInstance().isLockDriver() && DataHolder.getInstance().getLockDay().isEmpty()) {
                MyApplication.Toast("لطفا تعداد روزهای قفل راننده را انتخاب کنید", Toast.LENGTH_SHORT);
                return;
            }

            if (DataHolder.getInstance().isLockDriver() && DataHolder.getInstance().getLockReason() == 0) {
                MyApplication.Toast("لطفا دلیل قفل را انتخاب نمایید.", Toast.LENGTH_SHORT);
                return;
            }

            new GeneralDialog()
                    .message("ثبت نتیجه‌ی شکایت؟")
                    .cancelable(false)
                    .firstButton("بله", this::complaintSaveResult)
                    .secondButton("خیر", () -> {
                        if (binding.vfNextStep != null) {
                            binding.vfNextStep.setDisplayedChild(2);
                        }
                    })
                    .show();
        });

        binding.btnNext.setOnClickListener(view -> {
            if (binding.vfNextStep != null)
                binding.vfNextStep.setDisplayedChild(1);
            pauseVoice();
            new GeneralDialog()
                    .message("آیا میخواهید به مرحله بعد بروید؟")
                    .cancelable(false)
                    .firstButton("بله", this::updateStatus)
                    .secondButton("خیر", () -> {
                        if (binding.vfNextStep != null) {
                            binding.vfNextStep.setDisplayedChild(0);
                        }
                    })
                    .show();
        });

        return binding.getRoot();
    }

    int statusParam;

    private void refreshStep(int statusId) {
        switch (statusId) {
            case 1: //accepted request
                if (binding.indicator != null)
                    binding.indicator.setCurrentStep(statusId - 1);
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 2;
                break;
            case 2: //waiting for call
                if (binding.indicator != null)
                    binding.indicator.setCurrentStep(statusId - 1);
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 3;
                break;
            case 3: //waiting for saveResult
                if (binding.indicator != null)
                    binding.indicator.setCurrentStep(statusId - 1);
                if (binding.vfNextStep != null) {
                    binding.vfNextStep.setDisplayedChild(2);
                }
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 4;
                break;
        }
    }

    private void setupViewPager(NonSwipeableViewPager viewPager) {
        ComplaintPagerAdapter adapter = new ComplaintPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ComplaintTripDetailsFragment());
        adapter.addFragment(new ComplaintCallFragment());
        adapter.addFragment(new ComplaintSaveResultFragment());
        viewPager.setAdapter(adapter);
    }

    private void updateStatus() {
        RequestHelper.builder(EndPoints.COMPLAINT_UPDATE_STATUS)
                .addParam("complaintId", complaintDetailsModel.getComplaintId())
                .addParam("status", statusParam)
                .listener(updateStatus)
                .put();
    }

    RequestHelper.Callback updateStatus = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
//                {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"result":"true","resultDes":"تکميل شد"}}
//                {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"result":"false","resultDes":"اين مرحله تکميل نشده است"}}
                try {
                    DataHolder.getInstance().setComplaintResult((byte) 0);
                    DataHolder.getInstance().setLockDriver(false);
                    DataHolder.getInstance().setLockDay("");
                    DataHolder.getInstance().setUnlockDriver(false);
                    DataHolder.getInstance().setFined(false);
                    DataHolder.getInstance().setCustomerLock(false);
                    DataHolder.getInstance().setOutDriver(false);

                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("data");
                        boolean status = JSONObj.getBoolean("result");
                        String resultDes = JSONObj.getString("resultDes");
                        if (!status) {
                            new GeneralDialog()
                                    .message(resultDes)
                                    .cancelable(false)
                                    .firstButton("تلاش مجدد", () -> updateStatus())
                                    .secondButton("برگشت", null)
                                    .show();

                            if (statusModel == 3) {
                                if (binding.vfNextStep != null)
                                    binding.vfNextStep.setDisplayedChild(2);
                            } else {
                                if (binding.vfNextStep != null)
                                    binding.vfNextStep.setDisplayedChild(0);
                            }
                        } else {
                            if (statusParam == 4) {
                                new GeneralDialog()
                                        .message(message)
                                        .cancelable(false)
                                        .firstButton("تایید", () -> MyApplication.currentActivity.onBackPressed())
                                        .show();
                            } else {
                                refreshStep(statusParam);
                                statusModel = statusModel + 1;
                                vpRegisterDriver.setCurrentItem(statusParam - 2);
                            }
                        }

                        if (statusModel == 3) {
                            if (binding.vfNextStep != null)
                                binding.vfNextStep.setDisplayedChild(2);
                        } else {
                            if (binding.vfNextStep != null)
                                binding.vfNextStep.setDisplayedChild(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, updateStatus callBAck");
                    if (statusModel == 3) {
                        if (binding.vfNextStep != null)
                            binding.vfNextStep.setDisplayedChild(2);
                    } else {
                        if (binding.vfNextStep != null)
                            binding.vfNextStep.setDisplayedChild(0);
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() ->
            {
                if (statusModel == 3) {
                    if (binding.vfNextStep != null)
                        binding.vfNextStep.setDisplayedChild(2);
                } else {
                    if (binding.vfNextStep != null)
                        binding.vfNextStep.setDisplayedChild(0);
                }
            });
        }
    };

    private void complaintSaveResult() {
        if (binding.vfNextStep != null)
            binding.vfNextStep.setDisplayedChild(1);
        RequestHelper.builder(EndPoints.COMPLAINT_FINISH)
                .addParam("complaintId", complaintDetailsModel.getComplaintId())
                .addParam("typeResult", DataHolder.getInstance().getComplaintResult())
                .addParam("lockDriver", DataHolder.getInstance().isLockDriver() ? 1 : 0)
                .addParam("lockDay", DataHolder.getInstance().getLockDay())
                .addParam("unlockDriver", DataHolder.getInstance().isUnlockDriver() ? 1 : 0)
                .addParam("fined", DataHolder.getInstance().isFined() ? 1 : 0)
                .addParam("customerLock", DataHolder.getInstance().isCustomerLock() ? 1 : 0)
                .addParam("outDriver", DataHolder.getInstance().isOutDriver() ? 1 : 0)
                .addParam("driverLockReason", DataHolder.getInstance().getLockReason())
                .listener(saveResultCallBack)
                .post();
    }

    RequestHelper.Callback saveResultCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    DataHolder.getInstance().setComplaintResult((byte) 0);
                    DataHolder.getInstance().setLockDriver(false);
                    DataHolder.getInstance().setLockDay("");
                    DataHolder.getInstance().setUnlockDriver(false);
                    DataHolder.getInstance().setFined(false);
                    DataHolder.getInstance().setCustomerLock(false);
                    DataHolder.getInstance().setOutDriver(false);

                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("data");
                        boolean status = JSONObj.getBoolean("status");
                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تلاش مجدد", () -> complaintSaveResult())
                                    .secondButton("برگشت", null)
                                    .show();

                            if (binding.vfNextStep != null)
                                binding.vfNextStep.setDisplayedChild(2);
                        } else {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تایید", () -> MyApplication.currentActivity.onBackPressed())
                                    .show();
                        }
                        if (binding.vfNextStep != null)
                            binding.vfNextStep.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, saveResultCallBack callBAck");
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfNextStep != null)
                    binding.vfNextStep.setDisplayedChild(2);
            });
        }
    };
}