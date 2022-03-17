package ir.taxi1880.operatormanagement.fragment;

public interface OnVoiceListener {

        void onDuringInit();

        void onEndOfInit(int maxDuration);

        void onPlayVoice();

        void onTimerTask(int currentDuration);

        void onDownload401Error();

        void onDownload404Error();

        void onPauseVoice();

        void onVoipIdEqual0();

}
