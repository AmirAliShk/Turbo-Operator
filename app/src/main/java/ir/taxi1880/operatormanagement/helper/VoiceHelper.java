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
import java.util.Timer;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.mistake.PendingMistakesFragment;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class VoiceHelper {
    public static String TAG = VoiceHelper.class.getSimpleName();
    private static VoiceHelper instance;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private File file;

    private VoiceHelper() {
    }

    OnInitListener onInitListener;
    OnPlayListener onPlayListener;

    interface OnInitListener {
        void onInit();
    }

    interface OnPlayListener {
        void onPlay();
    }

    public synchronized static VoiceHelper getInstance() {
        if (instance == null) {
            instance = new VoiceHelper();
        }
        return instance;
    }

    public void play(String voiceName, String VoipId,) {
        instance.file = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName);
        if (instance.file.exists()) {
            initVoice(Uri.fromFile(file));
            playVoice();
        }
    }

    private void initVoice(Uri uri) {
        instance.mediaPlayer = MediaPlayer.create(MyApplication.context, uri);
        instance.mediaPlayer.setOnCompletionListener(mp -> {
//            if (binding.vfPlayPause != null) {
//                binding.vfPlayPause.setDisplayedChild(0);
//            }
            onInitListener.onInit();
        });
        TOTAL_VOICE_DURATION = mediaPlayer.getDuration();

        binding.skbTimer.setMax(TOTAL_VOICE_DURATION);

    }

    private void playVoice() {
        try {
            if (instance.mediaPlayer != null)
                instance.mediaPlayer.start();
            onPlayListener.onPlay();
//            if (binding.vfPlayPause != null)
//                binding.vfPlayPause.setDisplayedChild(2);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, playVoice method");
        }

        startTimer();
    }

    private void startTimer() {
        Log.i("pendingMistakeFragment", "startTimer: ");
        if (timer != null) {
            return;
        }
        timer = new Timer();
        PendingMistakesFragment.UpdateSeekBar task = new PendingMistakesFragment.UpdateSeekBar();
        timer.scheduleAtFixedRate(task, 500, 1000);

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
                    .start(new OnDownloadListener() {

                        @Override
                        public void onDownloadComplete() {
//                    FinishedDownload.execute(urlString);
                            File file = new File(dirPath + fileName);

                            MyApplication.handler.postDelayed(() -> {
                                initVoice(Uri.fromFile(file));
                                playVoice();
                            }, 500);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.e("pendingMistakeFragment", "onError: " + error.getResponseCode() + "");
                            Log.e("pendingMistakeFragment", "onError: " + error.getServerErrorMessage() + "");
                            FileHelper.deleteFile(dirPath, fileName);
                            if (error.getResponseCode() == 401)
                                new PendingMistakesFragment.RefreshTokenAsyncTask().execute();
                            if (error.getResponseCode() == 404)
                                binding.vfVoiceStatus.setDisplayedChild(1);
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

}

