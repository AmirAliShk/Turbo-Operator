package ir.taxi1880.operatormanagement.fragment.mistake;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.databinding.FragmentPendingMistakesBinding;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.dialog.PendingMistakesOptionsDialog;
import ir.taxi1880.operatormanagement.dialog.SaveMistakeResultDialog;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class PendingMistakesFragment extends Fragment {
    public static final String TAG = PendingMistakesFragment.class.getSimpleName();
    FragmentPendingMistakesBinding binding;
    DataBase dataBase;
    MediaPlayer mediaPlayer;
    AllMistakesModel model;
    LocalBroadcastManager broadcaster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPendingMistakesBinding.inflate(inflater, container, false);
        TypefaceUtil.overrideFonts(binding.getRoot(), MyApplication.IraSanSMedume);
        TypefaceUtil.overrideFonts(binding.txtEmpty);

        dataBase = new DataBase(MyApplication.context);

        binding.llMissedCall.setOnClickListener(view -> missCall());

        binding.imgPause.setOnClickListener(view -> pauseVoice());

        binding.imgPlay.setOnClickListener(view -> {
            if (binding.vfPlayPause != null)
                binding.vfPlayPause.setDisplayedChild(1);
            Log.i("URL", "show: " + EndPoints.CALL_VOICE + dataBase.getMistakesRow().getVoipId());
            String voiceName = dataBase.getMistakesRow().getId() + ".mp3";
            File file;
            file = new File(MyApplication.DIR_MAIN_FOLDER + MyApplication.VOICE_FOLDER_NAME + voiceName);

            String voipId = dataBase.getMistakesRow().getVoipId();
            if (file.exists()) {
                initVoice(Uri.fromFile(file));
                playVoice();
            } else if (voipId.equals("0")) {
                if (binding.vfVoiceStatus != null)
                    binding.vfVoiceStatus.setDisplayedChild(1);
                if (binding.vfPlayPause != null)
                    binding.vfPlayPause.setDisplayedChild(0);
            } else {
                startDownload(EndPoints.CALL_VOICE + dataBase.getMistakesRow().getVoipId(), voiceName);
            }
        });

        binding.btnOptions.setOnClickListener(view -> {
            pauseVoice();
            String tell = dataBase.getMistakesRow().getTell();
            String mobile = dataBase.getMistakesRow().getMobile();
            new PendingMistakesOptionsDialog()
                    .show(tell, mobile);
        });

        binding.btnSaveResult.setOnClickListener(view -> {
            if (binding.vfSaveResult != null)
                binding.vfSaveResult.setDisplayedChild(1);
            pauseVoice();
            new SaveMistakeResultDialog()
                    .show(model.getId(), new SaveMistakeResultDialog.MistakesResult() {
                        @Override
                        public void onSuccess(boolean success) {
                            MyApplication.handler.postDelayed(() -> {
                                getMistakesFromDB();
                                if (binding.vfVoiceStatus != null)
                                    binding.vfVoiceStatus.setDisplayedChild(0);
                            }, 200);
                        }

                        @Override
                        public void dismiss() {
                            if (binding.vfSaveResult != null)
                                binding.vfSaveResult.setDisplayedChild(0);
                        }
                    });
        });

        return binding.getRoot();
    }

    long lastTime = 0;

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
                                new RefreshTokenAsyncTask().execute();
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

    private void initVoice(Uri uri) {
        try {
            mediaPlayer = MediaPlayer.create(MyApplication.context, uri);
            mediaPlayer.setOnCompletionListener(mp -> {
                if (binding.vfPlayPause != null) {
                    binding.vfPlayPause.setDisplayedChild(0);
                }
            });
            TOTAL_VOICE_DURATION = mediaPlayer.getDuration();

            binding.skbTimer.setMax(TOTAL_VOICE_DURATION);

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, initVoice method");
        }
    }

    private void playVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.start();
            if (binding.vfPlayPause != null)
                binding.vfPlayPause.setDisplayedChild(2);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, playVoice method");
        }

        startTimer();
    }

    public void pauseVoice() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.pause();

            binding.skbTimer.setProgress(0);

            if (binding.vfPlayPause != null)
                binding.vfPlayPause.setDisplayedChild(0);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, pauseVoice method");
        }
        cancelTimer();
    }

    private int TOTAL_VOICE_DURATION;

    private Timer timer;

    private void startTimer() {
        Log.i("pendingMistakeFragment", "startTimer: ");
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
            getAccepted();
        }
    }

    void getMistakesFromDB() {
        if (dataBase.getMistakesCount() > 0) {
            model = dataBase.getMistakesRow();
            binding.txtOriginAddress.setText(StringHelper.toPersianDigits(model.getAddress()));
            binding.txtPassengerName.setText(StringHelper.toPersianDigits(model.getCustomerName()));
            binding.txtPassengerPhone.setText(StringHelper.toPersianDigits(model.getTell()));
            binding.txtOriginStation.setText(StringHelper.toPersianDigits(model.getStationCode() + ""));
            binding.txtCity.setText(StringHelper.toPersianDigits(dataBase.getCityName(model.getCity())));
            binding.txtDescription.setText(StringHelper.toPersianDigits(model.getDescription()));
            binding.txtTripDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianTen(DateHelper.parseDate(model.getDate())) + " " + model.getTime().substring(0, 5)));
            binding.txtDestAddress.setText(StringHelper.toPersianDigits(model.getDestination()));
            binding.txtDestStation.setText(StringHelper.toPersianDigits(model.getDestStation()));
            binding.txtDestStation.setText(StringHelper.toPersianDigits(model.getDestStation()));
            if (model.getMistakeReason() == null || model.getMistakeReason().isEmpty()) {
                binding.llMistakeReason.setVisibility(View.GONE);
            } else {
                binding.vfMissedCall.setVisibility(View.GONE);
                binding.txtMistakeReason.setText(StringHelper.toPersianDigits(model.getMistakeReason()));
            }
            binding.txtPrice.setText(StringHelper.toPersianDigits(StringHelper.setComma(model.getPrice())));
            binding.txtUserCode.setText(StringHelper.toPersianDigits(model.getUserCode() + ""));
            binding.txtUserCodeOrigin.setText(StringHelper.toPersianDigits(model.getStationRegisterUser() + ""));
            binding.txtUserCodeDestination.setText(StringHelper.toPersianDigits(model.getDestStationRegisterUser() + ""));

            binding.skbTimer.setProgress(0);

            binding.skbTimer.setOnSeekChangeListener(new OnSeekChangeListener() {
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
            if (binding.vfPending != null)
                binding.vfPending.setDisplayedChild(1);
        } else {
            if (binding.vfPending != null)
                binding.vfPending.setDisplayedChild(2);
        }

    }

    @Override
    public void onDestroyView() {
        try {
            pauseVoice();
            cancelTimer();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onDestroyView method");
        }
        super.onDestroyView();
    }

    private void cancelTimer() {
        try {
            if (timer == null) return;
            timer.cancel();
            timer = null;
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, cancelTimer method");
        }

    }

    private class UpdateSeekBar extends TimerTask {
        public void run() {
            if (mediaPlayer != null) {
                try {
                    MyApplication.handler.post(() -> {
                        Log.i("pendingMistakeFragment", "onStopTrackingTouch run: " + mediaPlayer.getCurrentPosition());
                        binding.skbTimer.setProgress(mediaPlayer.getCurrentPosition());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, UpdateSeekBar method");
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

    public void getAccepted() {
        RequestHelper.builder(EndPoints.ACCEPT_LISTEN + "ed")
                .listener(getAccepted)
                .get();
    }

    RequestHelper.Callback getAccepted = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    dataBase.clearMistakeTable();
                    JSONObject listenObj = new JSONObject(args[0].toString());
                    boolean success = listenObj.getBoolean("success");
                    String message = listenObj.getString("message");

                    if (success) {
                        JSONArray dataArr = listenObj.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject dataObj = dataArr.getJSONObject(i);

                            model = new AllMistakesModel();


                            model.setId(dataObj.getInt("id"));
                            model.setServiceCode(dataObj.getInt("serviceCode"));
                            model.setUserCode(dataObj.getInt("userCode"));
                            model.setDate(dataObj.getString("saveDate"));
                            model.setTime(dataObj.getString("saveTime"));
                            model.setDescription(dataObj.getString("Description"));
                            model.setTell(dataObj.getString("tell"));
                            model.setMobile(dataObj.getString("mobile"));
                            model.setUserCodeContact(dataObj.getInt("userCodeContact"));
                            model.setStationRegisterUser(dataObj.getInt("stationRegisterUser"));
                            model.setDestStationRegisterUser(dataObj.getInt("destStationRegisterUser"));
                            model.setAddress(dataObj.getString("address"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setConDate(dataObj.getString("conDate"));
                            model.setConTime(dataObj.getString("conTime"));
                            model.setSendTime(dataObj.getString("sendTime"));
                            model.setVoipId(dataObj.getString("VoipId"));
                            model.setStationCode(dataObj.getInt("stationCode"));
                            model.setCity(dataObj.getInt("cityId"));
                            model.setMistakeReason(dataObj.getString("reasonMistake"));
                            model.setDestStation(dataObj.getString("destinationStation"));
                            model.setDestination(dataObj.getString("destinationAddress"));
                            model.setPrice(dataObj.getString("servicePrice"));

                            dataBase.insertMistakes(model);
                        }

                        if (dataBase.getMistakesCount() == 0) {
                            if (binding.vfPending != null)
                                binding.vfPending.setDisplayedChild(2);
                        } else {
                            getMistakesFromDB();
                        }
                        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
                        Intent broadcastIntent = new Intent(KEY_PENDING_MISTAKE_COUNT);
                        broadcastIntent.putExtra(PENDING_MISTAKE_COUNT, dataBase.getMistakesCount());
                        broadcaster.sendBroadcast(broadcastIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, getAccepted method");
                }
            });
        }
    };

    public void missCall() {
        if (binding.vfMissedCall != null) {
            binding.vfMissedCall.setDisplayedChild(1);
        }
        RequestHelper.builder(EndPoints.MISSED_CALL)
                .addParam("listenId", model.getId())
                .listener(sendMissCall)
                .post();
    }

    RequestHelper.Callback sendMissCall = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {// {"success":true,"message":"عملیات با موفقیت انجام شد","data":{"status":true}}
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
                        if (binding.vfMissedCall != null) {
                            binding.vfMissedCall.setDisplayedChild(0);
                        }
                    }
                } catch (Exception e) {
                    if (binding.vfMissedCall != null) {
                        binding.vfMissedCall.setDisplayedChild(0);
                    }
                    e.printStackTrace();
                    AvaCrashReporter.send(e, TAG + " class, sendMissCall method");
                }
            });
        }
    };

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
    public void onDestroy() {
        super.onDestroy();
        try {
            pauseVoice();
            cancelTimer();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, onDestroy method");
        }
    }
}