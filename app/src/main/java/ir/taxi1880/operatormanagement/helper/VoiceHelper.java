package ir.taxi1880.operatormanagement.helper;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.OnVoiceListener;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class VoiceHelper {
    public static String TAG = VoiceHelper.class.getSimpleName();
    private static VoiceHelper instance;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private File file;

    private boolean isPlaying = false;

    private int totalVoiceDuration = 0;

    OnVoiceListener onVoiceListener;

    private VoiceHelper() {
    }

    public static VoiceHelper getInstance() {
        if (instance == null) {
            instance = new VoiceHelper();
        }
        return instance;
    }

    public MediaPlayer staticMd() {
        return instance.mediaPlayer;
    }

    public void autoplay(String webUrl, String voiceName, String voipId, OnVoiceListener onVoiceListener) {
        instance.onVoiceListener = onVoiceListener;
        instance.file = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName);
        if (instance.mediaPlayer != null && instance.mediaPlayer.isPlaying()) {
            pauseVoice();
        }
        if (instance.file.exists()) {
            initVoice(Uri.fromFile(instance.file));
            playVoice();
            instance.onVoiceListener.onFileExist();
        } else if (voipId.equals("0")) {
            instance.onVoiceListener.onVoipIdEqual0();
        } else {
            startDownload(webUrl, voiceName);
            instance.onVoiceListener.onStartDownload();
        }
    }

    private void initVoice(Uri uri) {
        instance.mediaPlayer = MediaPlayer.create(MyApplication.context, uri);
        instance.mediaPlayer.setOnCompletionListener(mp -> {
            instance.onVoiceListener.onDuringInit();
        });
        totalVoiceDuration = instance.mediaPlayer.getDuration();
        instance.onVoiceListener.onEndOfInit(totalVoiceDuration);
    }

    private void playVoice() {
        try {
            if (!instance.isPlaying && instance.mediaPlayer != null && !instance.mediaPlayer.isPlaying()) {
                instance.mediaPlayer.start();
                instance.isPlaying = true;
            }
            instance.onVoiceListener.onPlayVoice();

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, playVoice method");
        }

        startTimer();
    }

    private void startTimer() {
        Log.i(TAG, "startTimer: ");
        if (instance.timer != null) {
            return;
        }
        instance.timer = new Timer();
        UpdateSeekBar task = new UpdateSeekBar();
        instance.timer.scheduleAtFixedRate(task, 500, 1000);

    }

    private void startDownload(final String urlString, final String fileName) {
        try {
            URL url = new URL(urlString);

            String dirPath = MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME;

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
            PRDownloader.download(url.toString(), dirPath, fileName)
                    .setHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .setHeader("id_token", MyApplication.prefManager.getIdToken())
                    .build()
                    .setOnStartOrResumeListener(() -> {
                    })
                    .setOnPauseListener(() -> {
                    })
                    .setOnCancelListener(() -> {
                    })
                    .setOnProgressListener(progress -> {
                        instance.onVoiceListener.onProgressDownload(progress);
                    })

                    .start(new OnDownloadListener() {

                        @Override
                        public void onDownloadComplete() {
//                    FinishedDownload.execute(urlString);
                            File file = new File(dirPath + fileName);
                            instance.onVoiceListener.onDownloadCompleted();
                            MyApplication.handler.postDelayed(() -> {
                                initVoice(Uri.fromFile(file));
                                playVoice();
                            }, 500);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.e(TAG, "onError: " + error.getResponseCode() + "");
                            Log.e(TAG, "onError: " + error.getServerErrorMessage() + "");
                            FileHelper.deleteFile(dirPath, fileName);
                            instance.onVoiceListener.onDownloadError();
                            if (error.getResponseCode() == 401)
                                instance.onVoiceListener.onDownload401Error();
//                                RefreshTokenAsyncTask.execute();
                            if (error.getResponseCode() == 404)
                                instance.onVoiceListener.onDownload404Error();
//                                binding.vfVoiceStatus.setDisplayedChild(1);
                        }
                    });

//        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, startDownload method");
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, startDownload method1");
        }
    }

    private class UpdateSeekBar extends TimerTask {
        public void run() {
            if (instance.mediaPlayer != null) {
                try {
                    MyApplication.handler.post(() -> {
                        Log.i(TAG, "onStopTrackingTouch run: " + instance.mediaPlayer.getCurrentPosition());
                        instance.onVoiceListener.onTimerTask(instance.mediaPlayer.getCurrentPosition());
//                        binding.skbTimer.setProgress(mediaPlayer.getCurrentPosition());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, UpdateSeekBar method");
                }
            }
        }
    }

    public void pauseVoice() {
        try {
            if (instance.isPlaying && instance.mediaPlayer != null) {
                instance.isPlaying = false;
                instance.mediaPlayer.pause();
                instance.onVoiceListener.onPauseVoice();
            }
//            binding.skbTimer.setProgress(0);
//            binding.vfPlayPause.setDisplayedChild(0);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, pauseVoice method");
        }
        cancelTimer();
    }

    private void cancelTimer() {
        try {
            if (instance.timer == null) return;
            instance.timer.cancel();
            instance.timer = null;
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, cancelTimer method");
        }

    }


}


// class RefreshTokenAsyncTask extends AsyncTask<Void, Void, Boolean> {
//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        new AuthenticationInterceptor().refreshToken();
//        return null;
//    }
//}

