package com.iseasoft.iseafootball.listeners;

import com.iseasoft.iseafootball.PlayerType;
import com.iseasoft.iseafootball.VideoPlayerView;

public interface VideoPlayerListener {

    void onChangeStatus(VideoPlayerView playerView, @PlayerType.PlayerStatusType int type);

    void onError(VideoPlayerView playerView, @PlayerType.PlayerStatusType int error);

    void playbackProgress(VideoPlayerView playerView, int progress);

    void playbackBufferProgress(VideoPlayerView playerView, int buffer);
}
