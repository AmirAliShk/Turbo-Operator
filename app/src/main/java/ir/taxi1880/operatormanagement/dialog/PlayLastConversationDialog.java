package ir.taxi1880.operatormanagement.dialog;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.OnVoiceListener;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.databinding.DialogPalyLastConversationBinding;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.helper.VoiceHelper;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PlayLastConversationDialog {
    public static final String TAG = PlayLastConversationDialog.class.getSimpleName();
    static Dialog dialog;
    DialogPalyLastConversationBinding binding;
    String url;
    int idTrip;

    public void show(int tripId, String urlString,String voipId) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        binding = DialogPalyLastConversationBinding.inflate(LayoutInflater.from(dialog.getContext()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(wlp);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(true);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());

        url = urlString;
        idTrip = tripId;

        binding.skbTimer.setProgress(0);
        Log.i("URL", "show: " + urlString);
        String voiceName = tripId + ".mp3";
        VoiceHelper.getInstance().autoplay(
                url,
                voiceName,
                voipId,
                new OnVoiceListener() {
                    @Override
                    public void onFileExist() {
                        binding.vfManageVoice.setDisplayedChild(1);
                    }

                    @Override
                    public void onStartDownload() {
                        binding.vfManageVoice.setDisplayedChild(0);
                    }

                    @Override
                    public void onProgressDownload(Progress progress) {
                        binding.vfDownload.setDisplayedChild(0);
                        int percent = (int) ((progress.currentBytes / (double) progress.totalBytes) * 100);
                        Log.i("PlayConversationDialog", "onProgress: " + percent);

                        binding.progressDownload.setProgress(percent);
                        if (Calendar.getInstance().getTimeInMillis() - lastTime > 500) {
                            binding.textProgress.setText(percent + " %");
                            lastTime = Calendar.getInstance().getTimeInMillis();
                        }
                    }

                    @Override
                    public void onDownloadCompleted() {
                        binding.vfManageVoice.setDisplayedChild(1);
                    }

                    @Override
                    public void onDownloadError() {
                        binding.vfManageVoice.setDisplayedChild(2);
                    }

                    @Override
                    public void onDuringInit() { dismiss(); }

                    @Override
                    public void onEndOfInit(int maxDuration) {
                        binding.skbTimer.setMax(maxDuration);
                        String strTime = String.format(new Locale("en_US"), "%02d:%02d", (maxDuration / 1000) / 60, (maxDuration / 1000) % 60);
                        binding.txtTime.setText(strTime);
                    }

                    @Override
                    public void onPlayVoice() {
                        binding.vfPlayPause.setDisplayedChild(1);
                    }

                    @Override
                    public void onTimerTask(int currentDuration) {
                        binding.skbTimer.setProgress(currentDuration);
                        int timeRemaining = currentDuration / 1000;
                        String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
                        binding.txtTimeRemaining.setText(strTimeRemaining);
                    }

                    @Override
                    public void onDownload401Error() {
                        new RefreshTokenAsyncTask().execute();
                    }

                    @Override
                    public void onDownload404Error() {

                    }

                    @Override
                    public void onPauseVoice() {
                        binding.skbTimer.setProgress(0);
                        binding.vfPlayPause.setDisplayedChild(0);
                    }

                    @Override
                    public void onVoipIdEqual0() {

                    }
                });

//        File file = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName);
//        if (file.exists()) {
//            initVoice(Uri.fromFile(file));
//            playVoice();
//        } else {
//            startDownload(binding.vfManageVoice, binding.progressDownload, binding.textProgress, urlString, voiceName);
//        }

        dialog.setOnDismissListener(dialogInterface -> onDestroy());

        binding.skbTimer.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                int timeRemaining = seekParams.progress / 1000;
                String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
                binding.txtTimeRemaining.setText(strTimeRemaining);
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

        binding.llLastConversationDialog.setOnClickListener(view -> {
            return;
        });

        binding.imgStop.setOnClickListener(view -> VoiceHelper.getInstance().pauseVoice());

        binding.imgPlay.setOnClickListener(view -> {
            binding.skbTimer.setProgress(0);
            Log.i("URL", "show: " + url);
            String voiceName1 = idTrip + ".mp3";
            VoiceHelper.getInstance().autoplay(
                    url,
                    voiceName1,
                    voipId,
                    new OnVoiceListener() {
                        @Override
                        public void onFileExist() {
                            binding.vfManageVoice.setDisplayedChild(1);
                        }

                        @Override
                        public void onStartDownload() {
                            binding.vfManageVoice.setDisplayedChild(0);
                        }

                        @Override
                        public void onProgressDownload(Progress progress) {
                            int percent = (int) ((progress.currentBytes / (double) progress.totalBytes) * 100);
                            Log.i("PlayConversationDialog", "onProgress: " + percent);

                            binding.progressDownload.setProgress(percent);
                            if (Calendar.getInstance().getTimeInMillis() - lastTime > 500) {
                                binding.textProgress.setText(percent + " %");
                                lastTime = Calendar.getInstance().getTimeInMillis();
                            }
                        }

                        @Override
                        public void onDownloadCompleted() {
                            binding.vfManageVoice.setDisplayedChild(1);
                        }

                        @Override
                        public void onDownloadError() {
                            binding.vfManageVoice.setDisplayedChild(2);
                        }

                        @Override
                        public void onDuringInit() { dismiss(); }

                        @Override
                        public void onEndOfInit(int maxDuration) {
                            binding.skbTimer.setMax(maxDuration);
                            String strTime = String.format(new Locale("en_US"), "%02d:%02d", (maxDuration / 1000) / 60, (maxDuration / 1000) % 60);
                            binding.txtTime.setText(strTime);
                        }

                        @Override
                        public void onPlayVoice() {
                            binding.vfPlayPause.setDisplayedChild(1);
                        }

                        @Override
                        public void onTimerTask(int currentDuration) {
                            binding.skbTimer.setProgress(currentDuration);
                            int timeRemaining = currentDuration / 1000;
                            String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
                            binding.txtTimeRemaining.setText(strTimeRemaining);
                        }

                        @Override
                        public void onDownload401Error() {
                            new RefreshTokenAsyncTask().execute();
                        }

                        @Override
                        public void onDownload404Error() {

                        }

                        @Override
                        public void onPauseVoice() {
                            binding.skbTimer.setProgress(0);
                            binding.vfPlayPause.setDisplayedChild(0);
                        }

                        @Override
                        public void onVoipIdEqual0() {

                        }
                    });
//            File file1;
//            file1 = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName1);
//
//            if (file1.exists()) {
//                initVoice(Uri.fromFile(file1));
//                playVoice();
////                if (binding.vfManageVoice != null)
//                    binding.vfManageVoice.setDisplayedChild(1);
//            } else {
//                startDownload(binding.vfManageVoice, binding.progressDownload, binding.textProgress, url, voiceName1);
////                if (binding.vfManageVoice != null)
//                    binding.vfManageVoice.setDisplayedChild(0);
//            }
        });

        binding.blrView.setOnClickListener(view -> dismiss());

        binding.llDismissDialog.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    long lastTime = 0;

//    private void startDownload(ViewFlipper vfManageVoice, ProgressBar progressBar, TextView textProgress, final String urlString, final String fileName) {
//        if (vfManageVoice != null)
//            vfManageVoice.setDisplayedChild(0);
//
//        try {
//            URL url = new URL(urlString);
//
//            String dirPath = MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME;
//
//            new File(dirPath).mkdirs();
//            File file = new File(dirPath);
//            if (file.isDirectory()) {
//                String[] children = file.list();
//                for (int i = 0; i < children.length; i++) {
//                    new File(file, children[i]).delete();
//                }
//            }
//
////      File file = new File(dirPathTemp + fileName);
////      int downloadId = FindDownloadId.execte(urlString);
////      if (file.exists() && downloadId != -1) {
////        PRDownloader.resume(downloadId);
////      } else {
////        downloadId =
//            PRDownloader.download(url.toString(), dirPath, fileName)
//                    .setHeader("Authorization", MyApplication.prefManager.getAuthorization())
//                    .setHeader("id_token", MyApplication.prefManager.getIdToken())
//                    .build()
//                    .setOnStartOrResumeListener(() -> {
//                    })
//                    .setOnPauseListener(() -> {
//                    })
//                    .setOnCancelListener(() -> {
//                    })
//                    .setOnProgressListener(progress -> {
//                        int percent = (int) ((progress.currentBytes / (double) progress.totalBytes) * 100);
//                        Log.i("PlayConversationDialog", "onProgress: " + percent);
//
//                        progressBar.setProgress(percent);
//                        if (Calendar.getInstance().getTimeInMillis() - lastTime > 500) {
//                            textProgress.setText(percent + " %");
//                            lastTime = Calendar.getInstance().getTimeInMillis();
//                        }
//
//                    })
//                    .start(new OnDownloadListener() {
//
//                        @Override
//                        public void onDownloadComplete() {
////                    FinishedDownload.execute(urlString);
//                            vfManageVoice.setDisplayedChild(1);
//                            File file = new File(dirPath + fileName);
//                            MyApplication.handler.postDelayed(() -> {
//                                initVoice(Uri.fromFile(file));
//                                playVoice();
//                            }, 500);
//                        }
//
//                        @Override
//                        public void onError(Error error) {
//                            Log.e("PlayConversationDialog", "onError: " + error.getResponseCode() + "");
//                            Log.e("PlayConversationDialog", "onError: " + error.getServerErrorMessage() + "");
//                            vfManageVoice.setDisplayedChild(2);
//                            FileHelper.deleteFile(dirPath, fileName);
//                            if (error.getResponseCode() == 401)
//                                new RefreshTokenAsyncTask().execute();
//                        }
//                    });
//
////        StartDownload.execute(downloadId, url.toString(), dirPathTemp + fileName);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, startDownload method");
//        } catch (Exception e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, startDownload method2");
//        }
//    }
//
//    private void initVoice(Uri uri) {
//        try {
//            mediaPlayer = MediaPlayer.create(context, uri);
//            mediaPlayer.setOnCompletionListener(mp -> {
//                dismiss();
//            });
//            TOTAL_VOICE_DURATION = mediaPlayer.getDuration();
//
//            binding.skbTimer.setMax(TOTAL_VOICE_DURATION);
//            String strTime = String.format(new Locale("en_US"), "%02d:%02d", (TOTAL_VOICE_DURATION / 1000) / 60, (TOTAL_VOICE_DURATION / 1000) % 60);
//            binding.txtTime.setText(strTime);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, initVoice method");
//        }
//    }

//    private void playVoice() {
//        try {
//            if (mediaPlayer != null)
//                mediaPlayer.start();
////            if (binding.vfPlayPause != null)
//                binding.vfPlayPause.setDisplayedChild(1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, playVoice method");
//        }
//
//        startTimer();
//    }
//
//    private void pauseVoice() {
//        try {
//            if (mediaPlayer != null)
//                mediaPlayer.pause();
//
//            binding.skbTimer.setProgress(0);
//
////            if (binding.vfPlayPause != null)
//                binding.vfPlayPause.setDisplayedChild(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, pauseVoice method");
//        }
//        cancelTimer();
//    }

    private int TOTAL_VOICE_DURATION;

    private Timer timer;

//    private void startTimer() {
//        Log.i("PlayConversationDialog", "startTimer: ");
//        if (timer != null) {
//            return;
//        }
//        timer = new Timer();
//        UpdateSeekBar task = new UpdateSeekBar();
//        timer.scheduleAtFixedRate(task, 500, 1000);
//
//    }

    private void onDestroy() {
        try {
            VoiceHelper.getInstance().pauseVoice();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onDestroy method");
        }
    }

//    private void cancelTimer() {
//        try {
//            if (timer == null) return;
//            timer.cancel();
//            timer = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            AvaCrashReporter.send(e, TAG + " class, cancelTimer method");
//        }
//    }

    private void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }

//    private class UpdateSeekBar extends TimerTask {
//        public void run() {
//            if (mediaPlayer != null) {
//                try {
//                    MyApplication.handler.post(() -> {
//                        Log.i("PlayConversationDialog", "onStopTrackingTouch run: " + mediaPlayer.getCurrentPosition());
//                        binding.skbTimer.setProgress(mediaPlayer.getCurrentPosition());
//                        int timeRemaining = mediaPlayer.getCurrentPosition() / 1000;
//                        String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
//                        binding.txtTimeRemaining.setText(strTimeRemaining);
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    AvaCrashReporter.send(e, TAG + " class, UpdateSeekBar method");
//                }
//            }
//        }
//    }

    static class RefreshTokenAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            new AuthenticationInterceptor().refreshToken();
            return null;
        }
    }
}