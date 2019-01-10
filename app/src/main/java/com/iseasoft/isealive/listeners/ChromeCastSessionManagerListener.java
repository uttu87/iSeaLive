package com.iseasoft.isealive.listeners;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.iseasoft.isealive.LiveApplication;
import com.iseasoft.isealive.models.Match;

import static com.iseasoft.isealive.ISeaLiveConstants.SPORT_TV_ID;


public class ChromeCastSessionManagerListener implements SessionManagerListener<CastSession> {

    private static final String USER_AGENT = "ExoCastDemoPlayer";
    private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY =
            new DefaultHttpDataSourceFactory(USER_AGENT);

    private Match match;
    private ChromeCastSessionListener chromeCastSessionListener;
    private boolean isNeedResetMediaOptions;

    public boolean isNeedResetMediaOptions() {
        return isNeedResetMediaOptions;
    }

    public void setNeedResetMediaOptions(boolean needResetMediaOptions) {
        isNeedResetMediaOptions = needResetMediaOptions;
    }

    public ChromeCastSessionManagerListener() {
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public void setChromeCastSessionListener(ChromeCastSessionListener chromecastSessionListener) {
        this.chromeCastSessionListener = chromecastSessionListener;
    }


    @Override
    public void onSessionStarting(CastSession castSession) {
        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionStarting(castSession, match);
        }
    }

    @Override
    public void onSessionStarted(final CastSession castSession, String s) {
        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionStarted(castSession, s, match);
        }
    }

    public void sendMatch() {

        if (match == null) {
            return;
        }

        final CastSession castSession = LiveApplication.getCastSession();
        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onPublish(castSession, match);
        }

        final MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, match.getDescription());
        movieMetadata.putString(MediaMetadata.KEY_TITLE, match.getName());

        if (match.getThumbnailUrl() != null) {
            String thumbnail = match.getThumbnailUrl();
            if (!TextUtils.isEmpty(thumbnail)) {
                movieMetadata.addImage(new WebImage(Uri.parse(thumbnail)));
            }
        }
        remoteMediaLoad(movieMetadata);
    }

    private void remoteMediaLoad(MediaMetadata mediaMetadata) {
        String contentType = "application/dash+xml";
        if (match.isLive() || Integer.valueOf(match.getLeague()) == SPORT_TV_ID) {
            contentType = "application/x-mpegurl";
        }
        MediaInfo mediaInfo = new MediaInfo.Builder(match.getStreamUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(contentType)
                .setMetadata(mediaMetadata)
                .build();
        remoteMediaLoad(mediaInfo, 0);
    }

    private void remoteMediaLoad(MediaInfo mediaInfo, int position) {
        final CastSession castSession = LiveApplication.getCastSession();
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        MediaLoadOptions.Builder mediaLoadOptionsBuilder = new MediaLoadOptions.Builder().setPlayPosition(position * 1000);
        remoteMediaClient.load(mediaInfo, mediaLoadOptionsBuilder.build());
        if (position > 0) {
            isNeedResetMediaOptions = true;
        }
    }

    @Override
    public void onSessionStartFailed(CastSession castSession, int i) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionStartFailed(castSession, i, match);
        }
    }

    @Override
    public void onSessionEnding(CastSession castSession) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionEnding(castSession, match);
        }
    }

    @Override
    public void onSessionEnded(CastSession castSession, int i) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionEnded(castSession, i, match);
        }
    }

    @Override
    public void onSessionResuming(CastSession castSession, String s) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionResuming(castSession, s, match);
        }
    }

    @Override
    public void onSessionResumed(CastSession castSession, boolean b) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionResumed(castSession, b, match);
        }
    }

    @Override
    public void onSessionResumeFailed(CastSession castSession, int i) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionResumeFailed(castSession, i, match);
        }

    }

    @Override
    public void onSessionSuspended(CastSession castSession, int i) {

        if (chromeCastSessionListener != null) {
            chromeCastSessionListener.onSessionSuspended(castSession, i, match);
        }
    }
}
