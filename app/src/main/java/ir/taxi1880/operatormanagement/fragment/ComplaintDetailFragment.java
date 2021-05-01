package ir.taxi1880.operatormanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.ComplaintsModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class ComplaintDetailFragment extends Fragment {
    Unbinder unbinder;
    int statusModel;
    ComplaintsModel complaintsModel;

    @BindView(R.id.indicator)
    StepperIndicator indicator;

    @BindView(R.id.vfNextStep)
    ViewFlipper vfNextStep;

    @BindView(R.id.vfMissedCall)
    ViewFlipper vfMissedCall;

    @BindView(R.id.vfDelete)
    ViewFlipper vfDelete;

    @BindView(R.id.vfCall)
    ViewFlipper vfCall;

    @BindView(R.id.vfButtons)
    ViewFlipper vfButtons;

    @BindView(R.id.vfConfirm)
    ViewFlipper vfConfirm;

    @BindView(R.id.imgStatus)
    ImageView imgStatus;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtTell)
    TextView txtTell;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtJobPosition)
    TextView txtJobPosition;

    @BindView(R.id.txtDate)
    TextView txtDate;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @OnClick(R.id.imgCall)
    void onCall() {
        if (vfCall != null)
            vfCall.setDisplayedChild(1);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0" + complaintsModel.getTell()));
        startActivity(intent);
        if (vfCall != null)
            vfCall.setDisplayedChild(0);
    }

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

    @OnClick(R.id.btnDelete)
    void onDeleteRequest() {
        if (vfDelete != null)
            vfDelete.setDisplayedChild(1);
        new GeneralDialog()
                .message("در صورت عدم تمایل شخص برای پذیرش یا پاسخگو نبودن شما مجاز به حذف وی از فرآیند پذیرش میباشید.\n آیا از قطع فرآیند اطمینان دارید؟")
                .cancelable(false)
                .firstButton("بله", () -> deleteRequest())
                .secondButton("خیر", () -> {
                    if (vfDelete != null) {
                        vfDelete.setDisplayedChild(0);
                    }
                })
                .show();
    }

    public ComplaintDetailFragment(ComplaintsModel complaintsModel) {
        this.complaintsModel = complaintsModel;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view);

        if (complaintsModel.getStatus() == 3) {
            if (vfButtons != null) {
                vfButtons.setDisplayedChild(1);
            }
        } else {
            if (vfButtons != null) {
                vfButtons.setDisplayedChild(0);
            }
        }

        txtName.setText(complaintsModel.getName());
        txtTell.setText(complaintsModel.getTell());
        try {
            JSONArray cityArr = new JSONArray(MyApplication.prefManager.getCity());
            for (int i = 0; i < cityArr.length(); i++) {
                JSONObject cityObj = cityArr.getJSONObject(i);
                if (complaintsModel.getCity() == cityObj.getInt("CityId")) {
                    txtCity.setText(cityObj.getString("CityName"));
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtJobPosition.setText(complaintsModel.getJobPosition());
        txtDate.setText(complaintsModel.getDate());
        txtTime.setText(complaintsModel.getTime());
        statusModel = complaintsModel.getStatus();

        indicator.setCurrentStep(complaintsModel.getStatus());

        refreshStep(complaintsModel.getStatus());

        return view;
    }

    //        0 'جدید'
//        1 'پذیرش شده'
//        2 'منتظر مدارک'
//        3 'در انتظار تایید'
//        4 'تایید شده'
//        5 'حذف یا رد شده'
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
            case 3: //waiting for confirm
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

    private void updateStatus() {
//        RequestHelper.builder(EndPoints. ) //todo
//                .addParam("id", complaintsModel.getId())
//                .addParam("status", statusParam)
//                .listener(updateStatus)
//                .put();
    }

    RequestHelper.Callback updateStatus = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
//                {"success":true,"message":"","data":{"status":true}}
//                {"success":true,"message":"راننده ای با این شماره موبایل ثبت نشده است","data":{"status":false}}
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("data");
                        boolean status = JSONObj.getBoolean("status");

//                         JSONObject obj = new JSONObject(args[0].toString());
//                    boolean success = obj.getBoolean("success");
//                    String message = obj.getString("message");
//                    if (success) {
//                        JSONObject data = obj.getJSONObject("data");
//                        boolean status = data.getBoolean("status");

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
//        RequestHelper.builder(EndPoints. )
//                .listener(missCall)
//                .addParam("id", complaintsModel.getId())
//                .addParam("status", statusParam - 1)
//                .addParam("comment", "")
//                .post();
    }

    RequestHelper.Callback missCall = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
//                {"success":true,"message":"","data":{"status":true}}
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");

//                         JSONObject obj = new JSONObject(args[0].toString());
//                    boolean success = obj.getBoolean("success");
//                    String message = obj.getString("message");
//                    if (success) {
//                        JSONObject data = obj.getJSONObject("data");
//                        boolean status = data.getBoolean("status");

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
//                                    .onDismissListener(() -> {
//                                        if (vfMissedCall != null)
//                                            vfMissedCall.setDisplayedChild(0);
//                                    })
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

    private void deleteRequest() {
        RequestHelper.builder(EndPoints.HIRE + "status")
                .listener(deleteRequest)
                .addParam("id", complaintsModel.getId())
                .addParam("status", 5)
                .put();
    }

    RequestHelper.Callback deleteRequest = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
//                {"success":true,"message":"","data":{"status":true}}
//                 {"success":true,"message":"فعلا امکان حذف وجود ندارد","data":{"status":false}}
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    boolean success = object.getBoolean("success");
                    String message = object.getString("message");
                    if (success) {
                        JSONObject JSONObj = object.getJSONObject("data");
                        boolean status = JSONObj.getBoolean("status");

//                         JSONObject obj = new JSONObject(args[0].toString());
//                    boolean success = obj.getBoolean("success");
//                    String message = obj.getString("message");
//                    if (success) {
//                        JSONObject data = obj.getJSONObject("data");
//                        boolean status = data.getBoolean("status");

                        if (!status) {
                            new GeneralDialog()
                                    .message(message)
                                    .cancelable(false)
                                    .firstButton("تلاش مجدد", () -> deleteRequest())
                                    .secondButton("برگشت", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .message("فرد از صف پذیرش توربوتاکسی خارج شد.")
                                    .cancelable(false)
                                    .firstButton("تایید", () -> MyApplication.currentActivity.onBackPressed())
                                    .show();
                        }

                        if (vfDelete != null)
                            vfDelete.setDisplayedChild(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (vfDelete != null)
                        vfDelete.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (vfDelete != null)
                    vfDelete.setDisplayedChild(0);
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
