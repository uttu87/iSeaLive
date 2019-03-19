package com.iseasoft.iseagoal.listeners;

import com.iseasoft.iseagoal.PlayerType;
import com.iseasoft.iseagoal.VideoPlayerView;

public interface VideoPlayerListener {

    void onChangeStatus(VideoPlayerView playerView, @PlayerType.PlayerStatusType int type);

    void onError(VideoPlayerView playerView, @PlayerType.PlayerStatusType int error);

    void playbackProgress(VideoPlayerView playerView, int progress);

    void playbackBufferProgress(VideoPlayerView playerView, int buffer);
}
