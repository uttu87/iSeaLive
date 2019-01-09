package com.iseasoft.isealive.listeners;

import com.iseasoft.isealive.PlayerType;
import com.iseasoft.isealive.VideoPlayerView;

public interface VideoPlayerListener {

    void onChangeStatus(VideoPlayerView playerView, @PlayerType.PlayerStatusType int type);

    void onError(VideoPlayerView playerView, @PlayerType.PlayerStatusType int error);

    void playbackProgress(VideoPlayerView playerView, int progress);

    void playbackBufferProgress(VideoPlayerView playerView, int buffer);
}
