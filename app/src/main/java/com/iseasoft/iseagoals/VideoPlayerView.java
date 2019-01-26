package com.iseasoft.iseagoals;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iseasoft.iseagoals.listeners.VideoPlayerListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VideoPlayerView extends FrameLayout {
    private static final String TAG = VideoPlayerView.class.getSimpleName();
    private static final int SEEK_TIME = 10 * 1000;
    int videoSetErrorEventToken = 0;
    private String proxyUrl;
    private VideoPlayerListener playerListener;
    private VideoView playerVideoView;
    private boolean isFullScreen;
    private long pt;
    private boolean isSendFirstPlay;
    private long playStartTime;
    private int resumeTime = -1;
    private boolean playing = false;
    private Map<String, String> additionalHeaders = new HashMap<String, String>();


    public VideoPlayerView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setProxyUrl(String url) {
        this.proxyUrl = url;
    }

    public void setPlayerListener(VideoPlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    private void resetValues() {
        resetPt();
        isSendFirstPlay = false;
        playStartTime = 0;
    }

    public long getDuration() {
        return playerVideoView.getDuration();
    }

    public long getCurrentTime() {
        return playerVideoView.getCurrentPosition();
    }

    public int getBufferedPosition() {
        return playerVideoView.getBufferPercentage();
    }

    public int getLimitBitrate() {
        return 0;//TODO
    }

    public void setLimitBitrate(int limitBitrate) {
        //TODO setLimitBitrate
    }

    public void play() {
        if (playerVideoView != null)
            playerVideoView.start();
    }

    public boolean isPlaying() {
        if (playerVideoView != null)
            return playerVideoView.isPlaying();
        return false;
    }

    public void pause() {
        playerVideoView.pause();
    }

    public void seek(long position) {
        if (position < 0) {
            position = 0;
        } else if (position > playerVideoView.getDuration()) {
            position = playerVideoView.getDuration();
        }
        playerVideoView.seekTo(position);
    }

    public void skipBackward() {
        seek(playerVideoView.getCurrentPosition() - SEEK_TIME);
    }

    public void skipForward() {
        seek(playerVideoView.getCurrentPosition() + SEEK_TIME);
    }

    public void setSubtitle(String language) {
        //TODO
    }

    public void resetPt() {
        pt = 0;
    }

    public void resetPlayer() {
        playerVideoView.reset();
        playerListener = null;
        resetValues();
    }

    public void seekStart() {
        //TODO Check start seek
    }

    public void seekEnd() {
        //TODO check end seek
    }

    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        if (isFullScreen) {
            //TODO Enter fullscreen
        } else {
            //TODO exit fullscreen
        }
    }

    private void init(Context context) {

        View.inflate(getContext(), R.layout.view_player, this);
        playerVideoView = findViewById(R.id.player_video_view);

        playerVideoView.setControls((VideoControlsCore) null);

    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public void setAdditionalHeaders(Map<String, String> headers) {
        additionalHeaders = headers;
    }

    private void setVideoFinished() {
        resetValues();
    }

    private void sendPlayerError(@PlayerType.PlayerErrorType int error) {
        if (playerListener != null) {
            playerListener.onError(this, error);
        }
    }

    private void sendPlayerStateUpdateEvent(@PlayerType.PlayerStatusType int status) {
        if (playerListener != null) {
            playerListener.onChangeStatus(this, status);
        }
    }

    private void sendPlayEvent(int currentTime) {
        if (playerListener != null) {
            playerListener.playbackProgress(this, currentTime);
        }
    }

    private void sendBufferedPosition(int position) {
        if (playerListener != null) {
            playerListener.playbackBufferProgress(this, position);
        }
    }

    private String getDisplaySizeString() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point(0, 0);
            display.getRealSize(point);
            String result = String.format("%dx%d", point.x, point.y);
            return result;
        } else {
            return "";
        }
    }

    private void upPt() {
        if (playStartTime > 0) {
            long currentTime = new Date().getTime();
            pt += (currentTime - playStartTime);
        }
        playStartTime = 0;
    }
}
