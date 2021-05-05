package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
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
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintDetailsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.fragment.ComplaintSaveResultFragment.rgBlameComplaint;

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
        if (vfNextStep != null)
            vfNextStep.setDisplayedChild(1);

//        complaintId	int
//        typeResult	tinyint
//        lockDriver	tinyint
//        lockDay	tinyint
//        unlockDriver	tinyint
//        fined	tinyint
//        customerLock	tinyint
//        outDriver	tinyint

        new GeneralDialog()
                .message("ثبت نتیجه‌ی شکایت؟")
                .cancelable(false)
                .firstButton("بله", () -> complaintSaveResult(blameStatus,))
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
        bundle.putString("tellNumber", complaintDetailsModel.getCustomerMobileNumber());
        FragmentHelper.toFragment(MyApplication.currentActivity, new TripSupportFragment()).setArguments(bundle).replace();
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
            case 2: //waiting for docs
                indicator.setCurrentStep(statusId - 1);
                vpRegisterDriver.setCurrentItem(statusId - 1);
                statusParam = 3;
                break;
            case 3: //waiting for saveResult
                indicator.setCurrentStep(statusId - 1);
                if (vfNextStep != null) {
                    vfNextStep.setDisplayedChild(1);
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
//               {"status":true,"message":"عملیات با موفقیت انجام شد", data}
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("result");
                        boolean status = JSONObj.getBoolean("status");
                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
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
                    if (vfNextStep != null)
                        vfNextStep.setDisplayedChild(0);
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

    private void complaintSaveResult(int blameStatus, int lockDriver, int lockDay, int unlockDriver, int fined, int customerLock, int outDriver) {
        RequestHelper.builder(EndPoints.COMPLAINT_FINISH)
                .addParam("complaintId", complaintDetailsModel.getComplaintId())
                .addParam("typeResult", blameStatus)
                .addParam("lockDriver", lockDriver)
                .addParam("lockDay", lockDay)
                .addParam("unlockDriver", unlockDriver)
                .addParam("fined", fined)
                .addParam("customerLock", customerLock)
                .addParam("outDriver", outDriver)
                .listener(saveResultCallBack)
                .post();
    }

    RequestHelper.Callback saveResultCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("result");
                        boolean status = JSONObj.getBoolean("status");
                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
//                                    .firstButton("تلاش مجدد", () -> complaintSaveResult())//todo
                                    .secondButton("برگشت", null)
                                    .show();

                            if (vfNextStep != null)
                                vfNextStep.setDisplayedChild(0);
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
