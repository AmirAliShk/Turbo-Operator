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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.FileHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.RecentCallsModel;
import ir.taxi1880.operatormanagement.okHttp.AuthenticationInterceptor;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class RecentCallsAdapter extends RecyclerView.Adapter<RecentCallsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<RecentCallsModel> recentCallsModels;
    static ViewFlipper vfPlayPause;
    ViewFlipper vfVoiceStatus;
    static MediaPlayer mediaPlayer;
    private int TOTAL_VOICE_DURATION;
    static private Timer timer;
    static IndicatorSeekBar skbTimer;
    TextView txtTimeRemaining;
    ImageView imgPlay;
    ImageView imgPause;
    int position;
    LinearLayout llPhone;
    boolean isDownloading = false;

    public RecentCallsAdapter(Context mContext, ArrayList<RecentCallsModel> recentCallsModels) {
        this.mContext = mContext;
        this.recentCallsModels = recentCallsModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_calls, parent, false);
        TypefaceUtil.overrideFonts(view);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtTime;
        ViewFlipper vfPlayPause;
        ViewFlipper vfVoiceStatus;
        IndicatorSeekBar skbTimer;
        TextView txtTimeRemaining;
        TextView phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTimeRemaining = itemView.findViewById(R.id.txtTimeRemaining);
            imgPlay = itemView.findViewById(R.id.imgPlay);
            imgPause = itemView.findViewById(R.id.imgPause);
            vfPlayPause = itemView.findViewById(R.id.vfPlayPause);
            vfVoiceStatus = itemView.findViewById(R.id.vfVoiceStatus);
            skbTimer = itemView.findViewById(R.id.skbTimer);
            phone = itemView.findViewById(R.id.txtPassengerTell);
            llPhone = itemView.findViewById(R.id.llPhone);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentCallsModel model = recentCallsModels.get(position);

        holder.txtDate.setText(DateHelper.parseFormatToString(model.getTxtDate()));
        holder.txtTime.setText(DateHelper.parseFormat(model.getTxtDate()));
        if (model.getPhone() == null) {
            llPhone.setVisibility(View.GONE);
        } else {
            holder.phone.setText(model.getPhone());
        }

        imgPlay.setOnClickListener(view -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pauseVoice();
            }
            this.vfPlayPause = holder.vfPlayPause;
            this.position = position;
            this.skbTimer = holder.skbTimer;
            this.vfVoiceStatus = holder.vfVoiceStatus;
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

            skbTimer.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    int timeRemaining = seekParams.progress / 1000;
                    String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
                    txtTimeRemaining = holder.txtTimeRemaining;
                    txtTimeRemaining.setText(strTimeRemaining);
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
        });

        imgPause.setOnClickListener(view -> {
            pauseVoice();
        });

    }

    @Override
    public int getItemCount() {
        return recentCallsModels.size();
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
                        isDownloading = false;
                    })
                    .setOnCancelListener(() -> {
                        isDownloading = false;
                    })
                    .setOnProgressListener(progress -> {
                        isDownloading = true;
                    })
                    .start(new OnDownloadListener() {

                        @Override
                        public void onDownloadComplete() {
                            isDownloading = false;
                            FileHelper.moveFile(dirPathTemp, fileName, dirPath);
                            File file = new File(dirPath + fileName);
                            MyApplication.handler.postDelayed(() -> {
                                initVoice(Uri.fromFile(file));
                                playVoice();
                            }, 500);
                        }

                        @Override
                        public void onError(Error error) {
                            isDownloading = false;
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
                        int timeRemaining = mediaPlayer.getCurrentPosition() / 1000;
                        String strTimeRemaining = String.format(new Locale("en_US"), "%02d:%02d", timeRemaining / 60, timeRemaining % 60);
                        txtTimeRemaining.setText(strTimeRemaining);
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