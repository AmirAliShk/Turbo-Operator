package ir.taxi1880.operatormanagement.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.w3c.dom.Text;

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
import ir.taxi1880.operatormanagement.dialog.PendingComplaintOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.SaveResultDialog;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllComplaintModel;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PendingComplaintFragment extends Fragment {
    Unbinder unbinder;
    DataBase dataBase;
    MediaPlayer mediaPlayer;

    @OnClick(R.id.btnSaveResult)
    void onSaveResult() {
        new SaveResultDialog()
                .show();
    }

    @OnClick(R.id.btnOptions)
    void onOptions() {
        new PendingComplaintOptionsDialog()
                .show();
    }

    @BindView(R.id.txtTripDate)
    TextView txtTripDate;

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
        Log.i("URL", "show: " + EndPoints.CALL_VOICE + dataBase.getComplaintRow().getVoipId());
        String voiceName = dataBase.getComplaintRow().getId() + ".mp3";
        File file = new File(MyApplication.DIR_ROOT + MyApplication.VOICE_FOLDER_NAME + "/" + voiceName);
        if (file.exists()) {
            initVoice(Uri.fromFile(file));
            playVoice();
        } else {
            startDownload(EndPoints.CALL_VOICE + dataBase.getComplaintRow().getVoipId(), voiceName);
        }

    }

    @BindView(R.id.skbTimer)
    IndicatorSeekBar skbTimer;

    @BindView(R.id.vfPlayPause)
    ViewFlipper vfPlayPause;

    @BindView(R.id.txtEmpty)
    TextView txtEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_complaint, container, false);
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
//TODO thumb of seekbar goes to start
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
                vfPlayPause.setDisplayedChild(1);
        } catch (Exception e) {
        }

        startTimer();
    }

    private void pauseVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.pause();
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
            if (dataBase.getComplaintRow() == null) {
                if (vfPending != null)
                    vfPending.setDisplayedChild(2);
            } else {
                AllComplaintModel model = dataBase.getComplaintRow();
                txtAddress.setText(StringHelper.toPersianDigits(model.getAddress()));
                txtStationCode.setText(StringHelper.toPersianDigits("199"));
                txtCity.setText(StringHelper.toPersianDigits("مشهد"));
                txtDescription.setText(StringHelper.toPersianDigits(model.getDescription()));
                txtTripTime.setText(StringHelper.toPersianDigits(model.getSendTime()));
                txtTripDate.setText(StringHelper.toPersianDigits(model.getDate()));

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
    }

    @Override
    public void onDestroyView() {
        try {
            pauseVoice();
            cancelTimer();
        } catch (Exception e) {

        }
        super.onDestroyView();
    }

    private void cancelTimer() {
        try {
            if (timer == null) return;
            timer.cancel();
            timer = null;
        } catch (Exception e) {

        }

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
}
