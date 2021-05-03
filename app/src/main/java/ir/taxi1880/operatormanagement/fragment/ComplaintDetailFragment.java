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
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.customView.NonSwipeableViewPager;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintDetailsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class ComplaintDetailFragment extends Fragment {
    Unbinder unbinder;
    int statusModel;
    ComplaintDetailsModel complaintDetailsModel;
    public static NonSwipeableViewPager vpRegisterDriver;

    @BindView(R.id.indicator)
    StepperIndicator indicator;

    @BindView(R.id.vfNextStep)
    ViewFlipper vfNextStep;

    @BindView(R.id.vfMissedCall)
    ViewFlipper vfMissedCall;

    @BindView(R.id.vfDelete)
    ViewFlipper vfDelete;

    @BindView(R.id.vfButtons)
    ViewFlipper vfButtons;

    @BindView(R.id.vfConfirm)
    ViewFlipper vfConfirm;

    @BindView(R.id.imgStatus)
    ImageView imgStatus;

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

    @OnClick(R.id.btnConfirm)
    void onConfirm() {
        if (vfConfirm != null)
            vfConfirm.setDisplayedChild(1);
        new GeneralDialog()
                .message("اتمام مراحل پذیرش؟")
                .cancelable(false)
                .firstButton("بله", () -> updateStatus())
                .secondButton("خیر", () -> {
                    if (vfConfirm != null) {
                        vfConfirm.setDisplayedChild(0);
                    }
                })
                .show();
    }

    @OnClick(R.id.btnMissedCall)
    void onMissCall() {
        if (vfMissedCall != null)
            vfMissedCall.setDisplayedChild(1);
        new GeneralDialog()
                .message("آیا فرد پاسخگو نبود؟")
                .cancelable(false)
                .firstButton("بله", () -> missCall())
                .secondButton("خیر", () -> {
                    if (vfMissedCall != null) {
                        vfMissedCall.setDisplayedChild(0);
                    }
                })
                .show();

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

        if (complaintDetailsModel.getStatus() == 3) {
            if (vfButtons != null) {
                vfButtons.setDisplayedChild(1);
            }
        } else {
            if (vfButtons != null) {
                vfButtons.setDisplayedChild(0);
            }
        }

        statusModel = complaintDetailsModel.getStatus();

        setupViewPager(vpRegisterDriver);

        indicator.setCurrentStep(complaintDetailsModel.getStatus());

        refreshStep(complaintDetailsModel.getStatus());

        return view;
    }

    int statusParam;

    private void refreshStep(int statusId) {

        String status = "#f09a37";
        switch (statusId) {
            case 1: //accepted request
//                imgStatus.setImageResource(R.drawable.ic_call_hire);
                indicator.setCurrentStep(statusId - 1);
                status = "#f09a37";
                statusParam = 2;
                break;
            case 2: //waiting for docs
//                imgStatus.setImageResource(R.drawable.ic_documents);
                indicator.setCurrentStep(statusId - 1);
                status = "#3478f6";
                statusParam = 3;
                break;
            case 3: //waiting for saveResult
//                imgStatus.setImageResource(R.drawable.ic_registration);
                indicator.setCurrentStep(statusId - 1);
                status = "#10ad79";
                if (vfButtons != null) {
                    vfButtons.setDisplayedChild(1);
                }
                statusParam = 4;
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable bg_btn_disable = AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_btn_disable);
            DrawableCompat.setTint(bg_btn_disable, Color.parseColor(status));
            imgStatus.setBackground(bg_btn_disable);
        } else {
            imgStatus.setBackgroundColor(Color.parseColor(status));
        }

    }

    public static void swipeRight() {
        if (vpRegisterDriver.getCurrentItem() - 1 > vpRegisterDriver.getChildCount()) return;
        vpRegisterDriver.setCurrentItem(vpRegisterDriver.getCurrentItem() + 1, true);
    }

    public static void swipeLeft() {
        if (vpRegisterDriver.getCurrentItem() - 1 < 0) return;
        vpRegisterDriver.setCurrentItem(vpRegisterDriver.getCurrentItem() - 1, true);
    }

    private void setupViewPager(NonSwipeableViewPager viewPager) {
        ComplaintPagerAdapter adapter = new ComplaintPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ComplaintTripDetailsFragment());
        adapter.addFragment(new ComplaintCallFragment());
        adapter.addFragment(new ComplaintSaveResultFragment());
        viewPager.setAdapter(adapter);
    }

    private void updateStatus() {
        RequestHelper.builder(EndPoints.COMPLAINT_UPDATE_STATUS) //todo
                .addParam("id", complaintDetailsModel.getComplaintId())
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
                        JSONObject JSONObj = object.getJSONObject("data");
                        boolean status = JSONObj.getBoolean("status");
                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تلاش مجدد", () -> updateStatus())
                                    .secondButton("برگشت", null)
                                    .show();

                            if (statusModel == 3) {
                                if (vfButtons != null)
                                    vfButtons.setDisplayedChild(1);
                            } else {
                                if (vfNextStep != null)
                                    vfNextStep.setDisplayedChild(0);
                            }
                        } else {
                            if (statusParam == 4) {
                                new GeneralDialog()
                                        .message("پذیرش با موفقیت انجام شد.")
                                        .cancelable(false)
                                        .firstButton("تایید", () -> MyApplication.currentActivity.onBackPressed())
                                        .show();
                            } else {
                                refreshStep(statusParam);

                                statusModel = statusModel + 1;
                            }
                        }

                        if (statusModel == 3) {
                            if (vfButtons != null)
                                vfButtons.setDisplayedChild(1);
                            if (vfConfirm != null)
                                vfConfirm.setDisplayedChild(0);
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
                    if (vfButtons != null)
                        vfButtons.setDisplayedChild(1);
                    if (vfConfirm != null)
                        vfConfirm.setDisplayedChild(0);
                } else {
                    if (vfNextStep != null)
                        vfNextStep.setDisplayedChild(0);
                }
            });
        }
    };

    private void missCall() {
        RequestHelper.builder(EndPoints.COMPLAINT_MISSED_CALL) //todo 1 then call driver if 2 then call customer
                .listener(missCall)
                .addParam("id", complaintDetailsModel.getComplaintId())
                .addParam("status", statusParam - 1)
                .addParam("comment", "")
                .addParam("type", "")//todo
                .post();
    }

    RequestHelper.Callback missCall = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
//               {"status":true,"message":"عملیات با موفقیت انجام شد", data}
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

                    if (success) {
                        JSONObject data = object.getJSONObject("data");
                        boolean status = data.getBoolean("status");
                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تلاش مجدد", () -> missCall())
                                    .secondButton("برگشت", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .message("پیامک تماس از دست رفته ارسال شد.")
                                    .cancelable(true)
                                    .firstButton("تایید", null)
                                    .show();
                        }
                        if (vfMissedCall != null)
                            vfMissedCall.setDisplayedChild(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (vfMissedCall != null)
                        vfMissedCall.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfMissedCall != null)
                    vfMissedCall.setDisplayedChild(0);
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
