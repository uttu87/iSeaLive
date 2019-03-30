package com.iseasoft.iseafootball;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile;

public class ISeaLiveVideoController extends VideoControlsMobile {

    private ImageView screenModeChangeButton;
    private ImageButton btnReload;

    private OnClickListener screenModeChangeButtonClickListener;
    private OnClickListener reloadButtonClickListener;

    public ISeaLiveVideoController(Context context) {
        super(context);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScreenModeChangeButtonClickListener(OnClickListener screenModeChangeButtonClickListener) {
        this.screenModeChangeButtonClickListener = screenModeChangeButtonClickListener;
        if (screenModeChangeButton != null) {
            screenModeChangeButton.setOnClickListener(screenModeChangeButtonClickListener);
        }
    }

    public void setReloadButtonClickListener(OnClickListener reloadButtonClickListener) {
        this.reloadButtonClickListener = reloadButtonClickListener;
        if (btnReload != null) {
            btnReload.setOnClickListener(reloadButtonClickListener);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.isealive_video_controls;
    }

    @Override
    protected void retrieveViews() {
        super.retrieveViews();
        screenModeChangeButton = findViewById(R.id.button_screen_mode_change);
        btnReload = findViewById(R.id.exomedia_controls_reload_btn);
    }

    public void updateScreenModeChangeImage(boolean isFullScreen) {
        screenModeChangeButton.setImageResource(isFullScreen ? R.drawable.ic_fullscreen_exit : R.drawable.ic_fullscreen);
    }

    public void setReloadButtonVisible(boolean visible) {
        btnReload.setVisibility(visible ? VISIBLE : GONE);
        playPauseButton.setVisibility(visible ? GONE : VISIBLE);
    }
}
