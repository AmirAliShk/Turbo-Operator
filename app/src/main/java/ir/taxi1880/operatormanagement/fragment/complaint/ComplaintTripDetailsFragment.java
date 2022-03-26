package ir.taxi1880.operatormanagement.fragment.complaint;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.downloader.Progress;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.databinding.FragmentComplaintTripDetailsBinding;
import ir.taxi1880.operatormanagement.OnVoiceListener;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.helper.VoiceHelper;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;

public class ComplaintTripDetailsFragment extends Fragment {
    public static final String TAG = ComplaintTripDetailsFragment.class.getSimpleName();
    FragmentComplaintTripDetailsBinding binding;
    static IndicatorSeekBar skbTimer;
    static ViewFlipper vfPlayPause;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComplaintTripDetailsBinding.inflate(inflater, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TypefaceUtil.overrideFonts(binding.getRoot());

        skbTimer = binding.skbTimer;
        vfPlayPause = binding.vfPlayPause;

        binding.txtComplaintType.setText(StringHelper.toPersianDigits(ComplaintDetailFragment.complaintDetailsModel.getComplaintType()));

        String date = DateHelper.strPersianTree(DateHelper.parseDate(ComplaintDetailFragment.complaintDetailsModel.getServiceDate()));
        binding.txtServiceDate.setText(StringHelper.toPersianDigits(date));
        binding.txtOrigin.setText(StringHelper.toPersianDigits(ComplaintDetailFragment.complaintDetailsModel.getAddress()));
        binding.txtPrice.setText(StringHelper.toPersianDigits(StringHelper.setComma(ComplaintDetailFragment.complaintDetailsModel.getPrice() + "") + " تومان"));

        binding.imgPause.setOnClickListener(view -> VoiceHelper.getInstance().pauseVoice());

        binding.imgPlay.setOnClickListener(view -> {
            if (vfPlayPause != null)
                vfPlayPause.setDisplayedChild(1);

            Log.i("URL", "show: " + EndPoints.CALL_VOICE + ComplaintDetailFragment.complaintDetailsModel.getComplaintVoipId());
            String voiceName = ComplaintDetailFragment.complaintDetailsModel.getComplaintId() + ".mp3";

            VoiceHelper.getInstance().autoplay(
                    EndPoints.CALL_VOICE + ComplaintDetailFragment.complaintDetailsModel.getComplaintVoipId(),
                    voiceName,
                    ComplaintDetailFragment.complaintDetailsModel.getComplaintVoipId(),
                    new OnVoiceListener() {
                        @Override
                        public void onFileExist() {

                        }

                        @Override
                        public void onStartDownload() {

                        }

                        @Override
                        public void onProgressDownload(Progress progress) {

                        }

                        @Override
                        public void onDownloadCompleted() {

                        }

                        @Override
                        public void onDownloadError() {

                        }

                        @Override
                        public void onDuringInit() {
                            vfPlayPause.setDisplayedChild(0);
                        }

                        @Override
                        public void onEndOfInit(int maxDuration) {
                            skbTimer.setMax(maxDuration);
                        }

                        @Override
                        public void onPlayVoice() {
                            vfPlayPause.setDisplayedChild(2);
                        }

                        @Override
                        public void onTimerTask(int currentDuration) {
                            skbTimer.setProgress(currentDuration);
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
                            skbTimer.setProgress(0);
                            vfPlayPause.setDisplayedChild(0);
                        }

                        @Override
                        public void onVoipIdEqual0() {
                            binding.vfVoiceStatus.setDisplayedChild(1);
                            vfPlayPause.setDisplayedChild(0);
                        }
                    });

            binding.skbTimer.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                    if (VoiceHelper.getInstance().staticMd() != null) {
                        if (seekBar != null) {
                            VoiceHelper.getInstance().staticMd().seekTo(seekBar.getProgress());
                        }
                    }
                }
            });
        });

        return binding.getRoot();
    }

    static class RefreshTokenAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            new AuthenticationInterceptor().refreshToken();
            return null;
        }
    }
}