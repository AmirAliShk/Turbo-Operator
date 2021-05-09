package ir.taxi1880.operatormanagement.fragment;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AllMistakesAdapter;
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
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

import static ir.taxi1880.operatormanagement.app.Keys.KEY_NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.KEY_PENDING_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.NEW_MISTAKE_COUNT;
import static ir.taxi1880.operatormanagement.app.Keys.PENDING_MISTAKE_COUNT;

public class PendingMistakesFragment extends Fragment {
    Unbinder unbinder;
    DataBase dataBase;
    MediaPlayer mediaPlayer;
    AllMistakesModel model;
    LocalBroadcastManager broadcaster;

    @OnClick(R.id.btnSaveResult)
    void onSaveResult() {
        if (vfSaveResult != null)
            vfSaveResult.setDisplayedChild(1);
        pauseVoice();
        new SaveResultDialog()
                .show(model.getId(), new SaveResultDialog.MistakesResult() {
                    @Override
                    public void onSuccess(boolean success) {
                        MyApplication.handler.postDelayed(() -> {
                            getMistakesFromDB();
                            if (vfVoiceStatus != null)
                                vfVoiceStatus.setDisplayedChild(0);
                        }, 200);
                    }

                    @Override
                    public void dismiss() {
                        if (vfSaveResult != null)
                            vfSaveResult.setDisplayedChild(0);
                    }
                });
    }

    @OnClick(R.id.btnOptions)
    void onOptions() {
        pauseVoice();
        String tell = dataBase.getMistakesRow().getTell();
        String mobile = dataBase.getMistakesRow().getMobile();
        new PendingMistakesOptionsDialog()
                .show(tell, mobile);
    }

    @BindView(R.id.txtTripDate)
    TextView txtTripDate;

    @BindView(R.id.vfSaveResult)
    ViewFlipper vfSaveResult;

    @BindView(R.id.txtPassengerName)
    TextView txtPassengerName;

    @BindView(R.id.txtPassengerPhone)
    TextView txtPassengerPhone;

    @BindView(R.id.vfPending)
    ViewFlipper vfPending;

    @BindView(R.id.vfVoiceStatus)
    ViewFlipper vfVoiceStatus;

    @BindView(R.id.txtTripTime)
    TextView txtTripTime;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtCity)
    TextView txtCity;

    @BindView(R.id.txtOriginAddress)
    TextView txtOriginAddress;

    @BindView(R.id.txtOriginStation)
    TextView txtOriginStation;

    @BindView(R.id.txtPrice)
    TextView txtPrice;

    @BindView(R.id.txtDestAddress)
    TextView txtDestAddress;

    @BindView(R.id.txtDestStation)
    TextView txtDestStation;

    @OnClick(R.id.imgPlay)
    void onPlay() {
        if (vfPlayPause != null)
            vfPlayPause.setDisplayedChild(1);
        Log.i("URL", "show: " + EndPoints.CALL_VOICE + dataBase.getMistakesRow().getVoipId());
        String voiceName = dataBase.getMistakesRow().getId() + ".mp3";
        File file = new File(MyApplication.DIR_ROOT + MyApplication.VOICE_FOLDER_NAME + "/" + voiceName);
        String voipId = dataBase.getMistakesRow().getVoipId();
        if (file.exists()) {
            initVoice(Uri.fromFile(file));
            playVoice();
        } else if (voipId.equals("0")) {
            if (vfVoiceStatus != null)
                vfVoiceStatus.setDisplayedChild(1);
            if (vfPlayPause != null)
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
                            if (error.getResponseCode() == 404)
                                vfVoiceStatus.setDisplayedChild(1);
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

    public void pauseVoice() {
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
            getAccepted();
        }
    }

    void getMistakesFromDB() {
        if (dataBase.getMistakesCount() > 0) {
            model = dataBase.getMistakesRow();
            txtOriginAddress.setText(StringHelper.toPersianDigits(model.getAddress()));
            txtPassengerName.setText(StringHelper.toPersianDigits(model.getCustomerName()));
            txtPassengerPhone.setText(StringHelper.toPersianDigits(model.getTell()));
            txtOriginStation.setText(StringHelper.toPersianDigits(model.getStationCode() + ""));
            txtCity.setText(StringHelper.toPersianDigits(dataBase.getCityName(model.getCity())));
            txtDescription.setText(StringHelper.toPersianDigits(model.getDescription()));
            txtTripTime.setText(StringHelper.toPersianDigits(model.getTime()));
            txtTripDate.setText(StringHelper.toPersianDigits(model.getDate()));
            txtDestAddress.setText(StringHelper.toPersianDigits(model.getDestination()));
            txtDestStation.setText(StringHelper.toPersianDigits(model.getDestStation()));
            txtPrice.setText(StringHelper.toPersianDigits(model.getPrice()));

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
        } else {
            if (vfPending != null)
                vfPending.setDisplayedChild(2);
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
                            model.setAddress(dataObj.getString("address"));
                            model.setCustomerName(dataObj.getString("customerName"));
                            model.setConDate(dataObj.getString("conDate"));
                            model.setConTime(dataObj.getString("conTime"));
                            model.setSendTime(dataObj.getString("sendTime"));
                            model.setVoipId(dataObj.getString("VoipId"));
                            model.setStationCode(dataObj.getInt("stationCode"));
                            model.setCity(dataObj.getInt("cityId"));
                            model.setDestStation(dataObj.getString("destinationStation"));
                            model.setDestination(dataObj.getString("destinationAddress"));
                            model.setPrice(dataObj.getString("servicePrice"));

                            dataBase.insertMistakes(model);
                        }

                        if (dataBase.getMistakesCount() == 0) {
                            if (vfPending != null)
                                vfPending.setDisplayedChild(2);
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
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
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
