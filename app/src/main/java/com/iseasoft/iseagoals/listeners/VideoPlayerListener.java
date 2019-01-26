package com.iseasoft.iseagoals.listeners;

import com.iseasoft.iseagoals.PlayerType;
import com.iseasoft.iseagoals.VideoPlayerView;

public interface VideoPlayerListener {

    void onChangeStatus(VideoPlayerView playerView, @PlayerType.PlayerStatusType int type);

    void onError(VideoPlayerView playerView, @PlayerType.PlayerStatusType int error);

    void playbackProgress(VideoPlayerView playerView, int progress);

    void playbackBufferProgress(VideoPlayerView playerView, int buffer);
}
