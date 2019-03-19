package com.iseasoft.iseagoal;

import android.support.annotation.IntDef;

public class PlayerType {

    @IntDef({PLAYER_ERROR_VIDEO_NOT_FOUND, PLAYER_ERROR_LOAD_FAILED, PLAYER_ERROR_BEACON_CONFIG_ERROR, PLAYER_ERROR_OTHER_ERROR})
    public @interface PlayerErrorType {
    }

    public static final int PLAYER_ERROR_VIDEO_NOT_FOUND = -100;

    public static final int PLAYER_ERROR_LOAD_FAILED = -101;

    public static final int PLAYER_ERROR_BEACON_CONFIG_ERROR = -102;

    public static final int PLAYER_ERROR_OTHER_ERROR = -103;

    @IntDef({PLAYER_STATE_LOAD_FINISHED, PLAYER_STATE_PAUSE, PLAYER_STATE_PLAY_FINISHED, PLAYER_STATE_PLAY})
    public @interface PlayerStatusType {
    }

    public static final int PLAYER_STATE_LOAD_FINISHED = 200;

    public static final int PLAYER_STATE_PLAY_FINISHED = 201;

    public static final int PLAYER_STATE_PLAY = 202;

    public static final int PLAYER_STATE_PAUSE = 203;


}
