package ir.taxi1880.operatormanagement;

import com.downloader.Progress;

public interface OnVoiceListener {
        void onFileExist();

        void onStartDownload();

        void onProgressDownload(Progress progress);

        void onDownloadCompleted();
        void onDownloadError();

        void onDownload401Error();

        void onDownload404Error();

        void onDuringInit();

        void onEndOfInit(int maxDuration);

        void onPlayVoice();

        void onTimerTask(int currentDuration);

        void onPauseVoice();

        void onVoipIdEqual0();

}
