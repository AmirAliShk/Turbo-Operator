package ir.taxi1880.operatormanagement.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.PendingMistakesOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.SaveResultDialog;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;

public class PendingMistakesFragment extends Fragment {
    Unbinder unbinder;
    DataBase dataBase;
    MediaPlayer mediaPlayer;
    AllMistakesModel model;

    @OnClick(R.id.btnSaveResult)
    void onSaveResult() {
        pauseVoice();
        new SaveResultDialog()
                .show(model.getId(), success -> {
                    if (success) {
                        MyApplication.handler.postDelayed(() -> getMistakesFromDB(), 200);
                    }
                });
    }

    @OnClick(R.id.btnOptions)
    void onOptions() {
        pauseVoice();
        String tell = dataBase.getMistakesRow().getTell();
        new PendingMistakesOptionsDialog()
                .show(tell);
    }

    @BindView(R.id.txtTripDate)
    TextView txtTripDate;

    @BindView(R.id.txtPassengerName)
    TextView txtPassengerName;

    @BindView(R.id.txtPassengerPhone)
    TextView txtPassengerPhone;

    @BindView(R.id.vfPending)
    ViewFlipper vfPending;

    @BindView(R.id.txtTripTime)
    TextView txtTripTime;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.txtStationCode)
    TextView txtStationCode;

    @OnClick(R.id.imgPlay)
    void onPlay() {
        vfPlayPause.setDisplayedChild(1);
        Log.i("URL", "show: " + EndPoints.CALL_VOICE + dataBase.getMistakesRow().getVoipId());
        String voiceName = dataBase.getMistakesRow().getId() + ".mp3";
        File file = new File(MyApplication.DIR_ROOT + MyApplication.VOICE_FOLDER_NAME + "/" + voiceName);
        String voipId = dataBase.getMistakesRow().getVoipId();
        if (file.exists()) {
            initVoice(Uri.fromFile(file));
            playVoice();
        } else if (voipId.equals("0")) {
            MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT);
            vfPlayPause.setDisplayedChild(0);
        } else {
            startDownload(EndPoints.CALL_VOICE + dataBase.getMistakesRow().getVoipId(), voiceName);
        }
    }

    @OnClick(R.id.imgPause)
    void onImgPause() {
        pauseVoice();
    }

    @BindView(R.id.skbTimer)
    IndicatorSeekBar skbTimer;

    @BindView(R.id.vfPlayPause)
    ViewFlipper vfPlayPause;

    @BindView(R.id.txtEmpty)
    TextView txtEmpty;

    @BindView(R.id.txtMistakesVoipId)
    TextView txtComplaintVoipId;

    @BindView(R.id.txtMistakesId)
    TextView txtComplaintId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_mistakes, container, false);
        unbinder = ButterKnife.bind(this, view);
        TypefaceUtil.overrideFonts(view, MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(txtEmpty);

        dataBase = new DataBase(MyApplication.context);

        return view;
    }

    long lastTime = 0;

    private void startDownload(final String urlString, final String fileName) {
        try {
            URL url = new URL(urlString);

            String dirPath = MyApplication.DIR_ROOT + "voice/";
            String dirPathTemp = MyApplication.DIR_ROOT + "temp/";

            new File(dirPath).mkdirs();
            File file = new File(dirPath);
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    new File(file, children[i]).delete();
                }
            }

//      File file = new File(dirPathTemp + fileName);
//      int downloadId = FindDownloadId.execte(urlString);
//      if (file.exists() && downloadId != -1) {
//        PRDownloader.resume(downloadId);
//      } else {
//        downloadId =
            PRDownloader.download(url.toString(), dirPathTemp, fileName)
                    .setHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .setHeader("id_token", MyApplication.prefManager.getIdToken())
                    .build()
                    .setOnStartOrResumeListener(() -> {
                    })
                    .setOnPauseListener(() -> {
                    })
                    .setOnCancelListener(() -> {
                    })
                    .start(new OnDownloadListener() {

                        @Override
                        public void onDownloadComplete() {
//                    FinishedDownload.execute(urlString);
                            FileHelper.moveFile(dirPathTemp, fileName, dirPath);
                            File file = new File(dirPath + fileName);

                            MyApplication.handler.postDelayed(() -> {
                                initVoice(Uri.fromFile(file));
                                playVoice();
                            }, 500);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.e("PlayConversationDialog", "onError: " + error.getResponseCode() + "");
                            Log.e("PlayConversationDialog", "onError: " + error.getServerErrorMessage() + "");
                            FileHelper.deleteFile(dirPathTemp, fileName);
                            if (error.getResponseCode() == 401)
                                new RefreshTokenAsyncTask().execute();
                        }
                    });

//        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVoice(Uri uri) {
        try {
            mediaPlayer = MediaPlayer.create(MyApplication.context, uri);
            mediaPlayer.setOnCompletionListener(mp -> {
                if (vfPlayPause != null) {
                    vfPlayPause.setDisplayedChild(0);
                }
            });
            TOTAL_VOICE_DURATION = mediaPlayer.getDuration();

            skbTimer.setMax(TOTAL_VOICE_DURATION);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.start();
            if (vfPlayPause != null)
                vfPlayPause.setDisplayedChild(2);
        } catch (Exception e) {
        }

        startTimer();
    }

    private void pauseVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.pause();

            skbTimer.setProgress(0);

            if (vfPlayPause != null)
                vfPlayPause.setDisplayedChild(0);
        } catch (Exception e) {
        }
        cancelTimer();
    }

    private int TOTAL_VOICE_DURATION;

    private Timer timer;

    private void startTimer() {
        Log.i("PlayConversationDialog", "startTimer: ");
        if (timer != null) {
            return;
        }
        timer = new Timer();
        UpdateSeekBar task = new UpdateSeekBar();
        timer.scheduleAtFixedRate(task, 500, 1000);

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            getMistakesFromDB();
        }
    }

    void getMistakesFromDB() {
        if (dataBase.getMistakesCount() == 0) {
            if (vfPending != null)
                vfPending.setDisplayedChild(2);
        } else {
            model = dataBase.getMistakesRow();
            txtAddress.setText(StringHelper.toPersianDigits(model.getAddress()));
            txtPassengerName.setText(StringHelper.toPersianDigits(model.getCustomerName()));
            txtPassengerPhone.setText(StringHelper.toPersianDigits(model.getTell()));
            txtStationCode.setText(StringHelper.toPersianDigits("199")); //TODO correct station name an station code
            txtCity.setText(StringHelper.toPersianDigits("مشهد"));
            txtDescription.setText(StringHelper.toPersianDigits(model.getDescription()));
            txtTripTime.setText(StringHelper.toPersianDigits(model.getTime()));
            txtTripDate.setText(StringHelper.toPersianDigits(model.getDate()));
            txtComplaintId.setText(StringHelper.toPersianDigits(model.getId() + ""));
            txtComplaintVoipId.setText(StringHelper.toPersianDigits(model.getVoipId() + ""));

            skbTimer.setProgress(0);

            skbTimer.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    int timeRemaining = seekParams.progress / 1000;

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                    if (mediaPlayer != null) {
                        if (seekBar != null) {
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }
                }
            });
            if (vfPending != null)
                vfPending.setDisplayedChild(1);
        }

    }

    @Override
    public void onDestroyView() {
        try {
            pauseVoice();
            cancelTimer();
        } catch (Exception e) {}
        super.onDestroyView();
    }

    private void cancelTimer() {
        try {
            if (timer == null) return;
            timer.cancel();
            timer = null;
        } catch (Exception e) { }

    }

    private class UpdateSeekBar extends TimerTask {
        public void run() {
            if (mediaPlayer != null) {
                try {
                    MyApplication.handler.post(() -> {
                        Log.i("PlayConversationDialog", "onStopTrackingTouch run: " + mediaPlayer.getCurrentPosition());
                        skbTimer.setProgress(mediaPlayer.getCurrentPosition());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class RefreshTokenAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            new AuthenticationInterceptor().refreshToken();
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseVoice();
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseVoice();
    }

    @Override
    public void onResume() {
        Log.i("TAG", "onResume: ");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            pauseVoice();
            cancelTimer();
        } catch (Exception e) {

        }
    }
}
