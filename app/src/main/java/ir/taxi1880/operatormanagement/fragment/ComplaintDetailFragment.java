package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.rakshakhegde.stepperindicator.StepperIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.ComplaintPagerAdapter;
import ir.taxi1880.operatormanagement.app.DataHolder;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.NonSwipeableViewPager;
import ir.taxi1880.operatormanagement.dialog.ComplaintOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintDetailsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.fragment.ComplaintTripDetailsFragment.pauseVoice;


public class ComplaintDetailFragment extends Fragment {
    Unbinder unbinder;
    int statusModel;
    public static ComplaintDetailsModel complaintDetailsModel;
    public static NonSwipeableViewPager vpRegisterDriver;

    @BindView(R.id.indicator)
    StepperIndicator indicator;

    @BindView(R.id.vfNextStep)
    ViewFlipper vfNextStep;

    @BindView(R.id.vfTripDetails)
    ViewFlipper vfTripDetails;

    @BindView(R.id.vfOptions)
    ViewFlipper vfOptions;

    @BindView(R.id.llButtons)
    LinearLayout llButtons;

    @OnClick(R.id.btnNext)
    void onNext() {
        if (vfNextStep != null)
            vfNextStep.setDisplayedChild(1);
        pauseVoice();
        new GeneralDialog()
                .message("آیا میخواهید به مرحله بعد بروید؟")
                .cancelable(false)
                .firstButton("بله", () -> updateStatus())
                .secondButton("خیر", () -> {
                    if (vfNextStep != null) {
                        vfNextStep.setDisplayedChild(0);
                    }
                })
                .show();
    }

    @OnClick(R.id.btnSaveResult)
    void onConfirm() {
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
                .firstButton("بله", () -> complaintSaveResult())
                .secondButton("خیر", () -> {
                    if (vfNextStep != null) {
                        vfNextStep.setDisplayedChild(2);
                    }
                })
                .show();

    }

    @OnClick(R.id.btnTripDetails)
    void onMissCall() {
        if (vfTripDetails != null)
            vfTripDetails.setDisplayedChild(1);

        Bundle bundle = new Bundle();
        bundle.putString("id", complaintDetailsModel.getServiceId() + "");
        FragmentHelper.toFragment(MyApplication.currentActivity, new TripDetailsFragment()).setArguments(bundle).replace();
    }

    @OnClick(R.id.btnOptions)
    void onOptions() {
        new ComplaintOptionsDialog()
                .show(complaintDetailsModel.getCustomerPhoneNumber(), complaintDetailsModel.getCustomerMobileNumber(), complaintDetailsModel.getTaxicode());
    }

    public ComplaintDetailFragment(ComplaintDetailsModel complaintsModel) {
        this.complaintDetailsModel = complaintsModel;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        vpRegisterDriver = view.findViewById(R.id.vpRegisterDriver);

        statusModel = complaintDetailsModel.getStatus();

        setupViewPager(vpRegisterDriver);

        indicator.setCurrentStep(complaintDetailsModel.getStatus());

        refreshStep(complaintDetailsModel.getStatus());

        return view;
    }

    int statusParam;

    private void refreshStep(int statusId) {

        switch (statusId) {
            case 1: //accepted request
                indicator.setCurrentStep(statusId - 1);
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 2;
                break;
            case 2: //waiting for call
                indicator.setCurrentStep(statusId - 1);
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 3;
                break;
            case 3: //waiting for saveResult
                indicator.setCurrentStep(statusId - 1);
                if (vfNextStep != null) {
                    vfNextStep.setDisplayedChild(2);
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
                                if (vfNextStep != null)
                                    vfNextStep.setDisplayedChild(2);
                            } else {
                                if (vfNextStep != null)
                                    vfNextStep.setDisplayedChild(0);
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
                            if (vfNextStep != null)
                                vfNextStep.setDisplayedChild(2);
                        } else {
                            if (vfNextStep != null)
                                vfNextStep.setDisplayedChild(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (statusModel == 3) {
                        if (vfNextStep != null)
                            vfNextStep.setDisplayedChild(2);
                    } else {
                        if (vfNextStep != null)
                            vfNextStep.setDisplayedChild(0);
                    }
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() ->
            {
                if (statusModel == 3) {
                    if (vfNextStep != null)
                        vfNextStep.setDisplayedChild(2);
                } else {
                    if (vfNextStep != null)
                        vfNextStep.setDisplayedChild(0);
                }
            });
        }
    };

    private void complaintSaveResult() {
        if (vfNextStep != null)
            vfNextStep.setDisplayedChild(1);
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

                            if (vfNextStep != null)
                                vfNextStep.setDisplayedChild(2);
                        } else {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تایید", () -> MyApplication.currentActivity.onBackPressed())
                                    .show();
                        }
                        if (vfNextStep != null)
                            vfNextStep.setDisplayedChild(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfNextStep != null)
                    vfNextStep.setDisplayedChild(2);
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
