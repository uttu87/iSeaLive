package com.iseasoft.isealive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.MediaRouteButton;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.iseasoft.isealive.adapters.PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends CastPlayerActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.viewPager)
    ViewPager pager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.media_router_button)
    MediaRouteButton mediaRouteButton;

    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private IntroductoryOverlay mIntroductoryOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        setupHomeView();
        checkToPlayFromPushNotification();
        Utils.setupMediaRouteButton(this, mediaRouteButton, R.color.white);

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };
        mCastContext = CastContext.getSharedInstance(this);
    }

    private void checkToPlayFromPushNotification() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(ISeaLiveConstants.PUSH_URL_KEY);
            String message = intent.getStringExtra(ISeaLiveConstants.PUSH_MESSAGE);

            if (!TextUtils.isEmpty(url)) {
                Intent playerIntent = new Intent(this, PlayerActivity.class);
                playerIntent.putExtra(ISeaLiveConstants.PUSH_URL_KEY, url);
                playerIntent.putExtra(ISeaLiveConstants.PUSH_MESSAGE, message);
                startActivity(playerIntent);
            }
        }
    }

    private void setupHomeView() {
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

    @Override
    protected void onResume() {
        mCastContext.addCastStateListener(mCastStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCastStateListener = null;
        mCastContext = null;
        mIntroductoryOverlay = null;
        unbinder.unbind();
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteButton != null) && mediaRouteButton.getVisibility() == View.VISIBLE) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            MainActivity.this, mediaRouteButton)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }
}
