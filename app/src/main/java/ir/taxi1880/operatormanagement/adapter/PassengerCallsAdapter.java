package ir.taxi1880.operatormanagement.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.PassengerCallsModel;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class PassengerCallsAdapter extends RecyclerView.Adapter<PassengerCallsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PassengerCallsModel> passengerCallsModels;
    static ViewFlipper vfPlayPause;
    static MediaPlayer mediaPlayer;
    private int TOTAL_VOICE_DURATION;
    static private Timer timer;
    static IndicatorSeekBar skbTimer;
    TextView txtTimeRemaining;
    ImageView imgPlay;
    ImageView imgPause;
    int position;


    public PassengerCallsAdapter(Context mContext, ArrayList<PassengerCallsModel> passengerCallsModels) {
        this.mContext = mContext;
        this.passengerCallsModels = passengerCallsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_passenger_calls, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        ViewFlipper vfPlayPause;
        IndicatorSeekBar skbTimer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTimeRemaining = itemView.findViewById(R.id.txtTimeRemaining);
            imgPlay = itemView.findViewById(R.id.imgPlay);
            imgPause = itemView.findViewById(R.id.imgPause);
            vfPlayPause = itemView.findViewById(R.id.vfPlayPause);
            skbTimer = itemView.findViewById(R.id.skbTimer);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PassengerCallsModel model = passengerCallsModels.get(position);

        holder.txtDate.setText(model.getTxtDate());
        holder.txtTime.setText(model.getTxtTime());
        txtTimeRemaining.setText(model.getTxtTimeRemaining() + "");

        imgPlay.setOnClickListener(view -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pauseVoice();
            }
            this.vfPlayPause = holder.vfPlayPause;
            this.position = position;
            this.skbTimer = holder.skbTimer;
            if (holder.vfPlayPause != null) {
                holder.vfPlayPause.setDisplayedChild(1);
            }
            Log.i("URL", "show: " + EndPoints.CALL_VOICE + model.getVoipId());
            String voiceName = model.getVoipId() + ".mp3";
            File file = new File(MyApplication.DIR_ROOT + MyApplication.VOICE_FOLDER_NAME + "/" + voiceName);
            String voipId = model.getVoipId();
            if (file.exists()) {
                initVoice(Uri.fromFile(file));
                playVoice();
            } else if (voipId.equals("0")) {
                MyApplication.Toast("صوتی برای این تماس وجود ندارد", Toast.LENGTH_SHORT);
                vfPlayPause.setDisplayedChild(0);
            } else {
                startDownload(EndPoints.CALL_VOICE + model.getVoipId(), voiceName);
            }

        });

        imgPause.setOnClickListener(view -> {
            pauseVoice();
        });
    }

    @Override
    public int getItemCount() {
        return passengerCallsModels.size();
    }

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

    public static void pauseVoice() {
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

    private void startTimer() {
        Log.i("PlayConversationDialog", "startTimer: ");
        if (timer != null) {
            return;
        }
        timer = new Timer();
        UpdateSeekBar task = new UpdateSeekBar();
        timer.scheduleAtFixedRate(task, 500, 1000);

    }

    private static void cancelTimer() {
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
//I/URL: show: http://turbotaxi.ir:1884/api/v1/voice/1615283716.10914667
//        I/MediaPlayer: setDataSource(107, 0, 576460752303423487)
//        I/MediaPlayer: [HSM] stayAwake true uid: 10955, pid: 6111
//        I/MediaPlayer: Pid:6111 MediaPlayer::start
//        I/PlayConversationDialog: startTimer:
//        E/MediaPlayer: error (1, -19)
//        E/MediaPlayer: invoke failed: wrong state 0, mPlayer(0x7e17aa5900)
//        E/MediaPlayer: Error (1,-19)
//        I/MediaPlayer: [HSM] stayAwake false uid: 10955, pid: 6111
//        E/MediaPlayer: Error (1,-1010)
//        I/MediaPlayer: [HSM] stayAwake false uid: 10955, pid: 6111
//        I/PlayConversationDialog: onStopTrackingTouch run: 0
//        I/PlayConversationDialog: onStopTrackingTouch run: 0
//        I/MediaPlayer: [HSM] stayAwake false uid: 10955, pid: 6111
//        E/MediaPlayer: pause called in state 0, mPlayer(0x7e17aa5900)
//        E/MediaPlayer: error (-38, 0)
//        E/MediaPlayer: Error (-38,0)
//        I/MediaPlayer: [HSM] stayAwake false uid: 10955, pid: 6111
//
