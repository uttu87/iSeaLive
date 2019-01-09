package com.iseasoft.isealive.listeners;

import com.google.android.gms.cast.framework.CastSession;
import com.iseasoft.isealive.models.Match;


public interface ChromeCastSessionListener {
    void onPublish(CastSession castSession, Match match);

    void onSessionStarting(CastSession castSession, Match match);

    void onSessionStarted(CastSession castSession, String s, Match match);

    void onSessionStartFailed(CastSession castSession, int i, Match match);

    void onSessionEnding(CastSession castSession, Match match);

    void onSessionEnded(CastSession castSession, int i, Match match);

    void onSessionResuming(CastSession castSession, String s, Match match);

    void onSessionResumed(CastSession castSession, boolean b, Match match);

    void onSessionResumeFailed(CastSession castSession, int i, Match match);

    void onSessionSuspended(CastSession castSession, int i, Match match);
}
