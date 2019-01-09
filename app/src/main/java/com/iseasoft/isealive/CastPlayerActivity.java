package com.iseasoft.isealive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.MediaRouteButton;

public class CastPlayerActivity extends BaseActivity {
    MediaRouteButton mediaRouteButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaRouteButton = findViewById(R.id.media_router_button);
        Utils.setupMediaRouteButton(this, mediaRouteButton, R.color.white);
    }
}
