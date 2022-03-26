package ir.taxi1880.operatormanagement.fragment.complaint;

import static ir.taxi1880.operatormanagement.fragment.complaint.ComplaintDetailFragment.complaintDetailsModel;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.FragmentComplaintCallBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.NumbersDialog;
import ir.taxi1880.operatormanagement.fragment.OnVoiceListener;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.helper.VoiceHelper;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class ComplaintCallFragment extends Fragment {

    public static final String TAG = ComplaintCallFragment.class.getSimpleName();
    FragmentComplaintCallBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComplaintCallBinding.inflate(inflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TypefaceUtil.overrideFonts(binding.getRoot());

        binding.txtComplaintType.setText(StringHelper.toPersianDigits(complaintDetailsModel.getComplaintType()));

        String date = DateHelper.strPersianTree(DateHelper.parseDate(complaintDetailsModel.getServiceDate()));
        binding.txtServiceDate.setText(StringHelper.toPersianDigits(date));
        binding.txtOrigin.setText(StringHelper.toPersianDigits(complaintDetailsModel.getAddress()));
        binding.txtPrice.setText(StringHelper.toPersianDigits(StringHelper.setComma(complaintDetailsModel.getPrice() + "") + " تومان"));
        binding.txtCustomerName.setText(StringHelper.toPersianDigits(complaintDetailsModel.getCustomerName()));
        binding.txtDriverName.setText(StringHelper.toPersianDigits(complaintDetailsModel.getDriverName() + " " + complaintDetailsModel.getDriverLastName()));
        binding.txtCountCallPassenger.setText(StringHelper.toPersianDigits(complaintDetailsModel.getCountCallCustomer() + " بار"));

        binding.vfCallDriver.setOnClickListener(view -> {
            new NumbersDialog()
                    .show(complaintDetailsModel.getDriverMobile().substring(1), complaintDetailsModel.getDriverMobile2().substring(1));
        });

        binding.vfCallCustomer.setOnClickListener(view -> {
            new NumbersDialog()
                    .show(complaintDetailsModel.getCustomerMobileNumber(), complaintDetailsModel.getCustomerPhoneNumber());
        });

        binding.imgMissedCallCustomer.setOnClickListener(view -> {
            binding.vfMissedCallCustomer.setDisplayedChild(1);
            missedCall(2);
        });

        binding.imgMissedCallDriver.setOnClickListener(view -> {

            binding.vfMissedCallDriver.setDisplayedChild(1);
            missedCall(1);
        });

        binding.imgPause.setOnClickListener(view -> VoiceHelper.getInstance().pauseVoice());

        binding.imgPlay.setOnClickListener(view -> {
            binding.vfPlayPause.setDisplayedChild(1);

            Log.i("URL", "show: " + EndPoints.CALL_VOICE + complaintDetailsModel.getComplaintVoipId());
            String voiceName = complaintDetailsModel.getComplaintId() + ".mp3";


            VoiceHelper.getInstance().autoplay(
                    EndPoints.CALL_VOICE + complaintDetailsModel.getComplaintVoipId(),
                    voiceName,
                    complaintDetailsModel.getComplaintVoipId(),
                    new OnVoiceListener() {
                        @Override
                        public void onDuringInit() {
                            binding.vfPlayPause.setDisplayedChild(0);
                        }

                        @Override
                        public void onEndOfInit(int maxDuration) {
                            binding.skbTimer.setMax(maxDuration);
                        }

                        @Override
                        public void onPlayVoice() {
                            binding.vfPlayPause.setDisplayedChild(2);
                        }

                        @Override
                        public void onTimerTask(int currentDuration) {
                            binding.skbTimer.setProgress(currentDuration);
                        }

                        @Override
                        public void onDownload401Error() {
                            new RefreshTokenAsyncTask().execute();
                        }

                        @Override
                        public void onDownload404Error() {
                            binding.vfVoiceStatus.setDisplayedChild(1);
                        }

                        @Override
                        public void onPauseVoice() {
                            binding.skbTimer.setProgress(0);
                            binding.vfPlayPause.setDisplayedChild(0);
                        }

                        @Override
                        public void onVoipIdEqual0() {
                            binding.vfVoiceStatus.setDisplayedChild(1);
                            binding.vfPlayPause.setDisplayedChild(0);
                        }
                    });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        try {
            VoiceHelper.getInstance().pauseVoice();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onDestroyView method");
        }
        super.onDestroyView();
    }

    static class RefreshTokenAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            new AuthenticationInterceptor().refreshToken();
            return null;
        }
    }

    private void missedCall(int type) {
        RequestHelper.builder(EndPoints.COMPLAINT_MISSED_CALL) // 1 then call driver if 2 then call customer
                .listener(missCall)
                .addParam("complaintId", complaintDetailsModel.getComplaintId())
                .addParam("status", 2)
                .addParam("comment", "")
                .addParam("type", type)
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
                                    .secondButton("برگشت", null)
                                    .show();
                        } else {
                            new GeneralDialog()
                                    .message("پیامک تماس از دست رفته ارسال شد.")
                                    .cancelable(true)
                                    .firstButton("تایید", null)
                                    .show();
                        }
                        if (binding.vfMissedCallDriver != null)
                            binding.vfMissedCallDriver.setDisplayedChild(0);
                        if (binding.vfMissedCallCustomer != null)
                            binding.vfMissedCallCustomer.setDisplayedChild(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, missCall callBAck");
                    if (binding.vfMissedCallDriver != null)
                        binding.vfMissedCallDriver.setDisplayedChild(0);
                    if (binding.vfMissedCallCustomer != null)
                        binding.vfMissedCallCustomer.setDisplayedChild(0);
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
                if (binding.vfMissedCallDriver != null)
                    binding.vfMissedCallDriver.setDisplayedChild(0);
                if (binding.vfMissedCallCustomer != null)
                    binding.vfMissedCallCustomer.setDisplayedChild(0);
            });
        }
    };
}