package com.iseasoft.iseagoals;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.iseasoft.iseagoals.listeners.FragmentEventListener;
import com.iseasoft.iseagoals.listeners.OnConfirmationDialogListener;
import com.iseasoft.iseagoals.models.Match;
import com.iseasoft.iseagoals.references.SharedPrefs;
import com.iseasoft.iseagoals.widgets.ConfirmationDialog;
import com.startapp.android.publish.adsCommon.StartAppAd;

import butterknife.OnClick;
import butterknife.Optional;

import static com.iseasoft.iseagoals.ISeaLiveConstants.ADMOB_TYPE;
import static com.iseasoft.iseagoals.ISeaLiveConstants.CAROUSEL_ID;
import static com.iseasoft.iseagoals.ISeaLiveConstants.MATCH_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.RICHADX_TYPE;
import static com.iseasoft.iseagoals.ISeaLiveConstants.SPORT_TV_ID;
import static com.iseasoft.iseagoals.ISeaLiveConstants.STARTAPP_TYPE;

public class PlayerActivity extends BaseActivity implements FragmentEventListener {

    public static final String VIDEO_URL = "VIDEO_URL";
    private final int APP_OPEN_COUNT_LIMIT = 10;
    Match match;

    private PublisherInterstitialAd publisherInterstitialAd;
    private InterstitialAd mInterstitialAd;
    private StartAppAd startAppAd;

    private boolean isImmersiveAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private void setupPublisherInterstitialAds() {
        if (publisherInterstitialAd == null) {
            publisherInterstitialAd = new PublisherInterstitialAd(this);
            publisherInterstitialAd.setAdUnitId(getString(R.string.richadx_interstitial_ad_unit_id));
        }
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();

        publisherInterstitialAd.loadAd(adRequest);

        publisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (publisherInterstitialAd != null) {
                    publisherInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (startAppAd != null && startAppAd.isReady()) {
                    startAppAd.showAd();
                }
            }
        });
    }


    private void setupInterstitialAds() {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_ad_unit_id));
        }
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (startAppAd != null && startAppAd.isReady()) {
                    startAppAd.showAd();
                }
            }
        });
    }

    private void setupStartAppAd() {
        if (startAppAd == null) {
            startAppAd = new StartAppAd(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player);
        super.onCreate(savedInstanceState);
        setupStartAppAd();

        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra(ISeaLiveConstants.PUSH_URL_KEY))) {
            String matchUrl = getIntent().getStringExtra(ISeaLiveConstants.PUSH_URL_KEY);
            String message = getIntent().getStringExtra(ISeaLiveConstants.PUSH_MESSAGE);
            match = new Match();
            match.setStreamUrl(matchUrl);
            match.setName(message);
            match.setLeague(String.valueOf(CAROUSEL_ID));
            if (matchUrl.contains("http")) {
                match.setYoutube(false);
            } else {
                match.setYoutube(true);
            }
        } else {
            match = (Match) getIntent().getExtras().getSerializable(MATCH_KEY);
        }

        if (match != null) {
            if (match.isYoutube()) {
                setupYoutubePlayer(match);
            } else {
                setupPlayer(match);
            }

            setupMatchList(match);
        }
        checkAndShowReviewDialog();
    }

    public void setupFullScreenAds() {
        if (LiveApplication.screenCount >= LiveApplication.getInterstitialAdsLimit()) {
            LiveApplication.screenCount = 0;
            int adsType = (int) LiveApplication.getAdsType();
            switch (adsType) {
                case ADMOB_TYPE:
                    setupInterstitialAds();
                    break;
                case RICHADX_TYPE:
                    setupPublisherInterstitialAds();
                    break;
                case STARTAPP_TYPE:
                    showStartApp();
                    break;
                default:
                    setupInterstitialAds();
                    break;
            }
        }
    }

    private void showStartApp() {
        if (startAppAd != null && startAppAd.isReady()) {
            startAppAd.showAd();
        }
    }

    private void checkAndShowReviewDialog() {
        if (SharedPrefs.getInstance().getAppOpenCount() > APP_OPEN_COUNT_LIMIT &&
                !SharedPrefs.getInstance().isAppRated()) {

            ConfirmationDialog.newInstance(
                    getString(R.string.question),
                    "",
                    getString(R.string.common_dialog_yes),
                    new OnConfirmationDialogListener() {
                        @Override
                        public void onConfirmed() {
                            SharedPrefs.getInstance().setAppRated();
                            launchMarket();
                        }

                        @Override
                        public void onCanceled() {
                            SharedPrefs.getInstance().resetAppOpenCount();
                        }
                    }).show(getSupportFragmentManager(), ConfirmationDialog.TAG);
        }
    }

    private void setupPlayer(Match match) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        PlayerFragment playerFragment = PlayerFragment.newInstance(match);
        playerFragment.setFragmentEventListener(this);
        ft.replace(R.id.player_view, playerFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    private void setupYoutubePlayer(Match match) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        YoutubePlayerFragment youtubePlayerFragment = YoutubePlayerFragment.newInstance(match);
        youtubePlayerFragment.setFragmentEventListener(this);
        ft.replace(R.id.player_view, youtubePlayerFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setupMatchList(Match match) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (match.getLeague().equals(String.valueOf(SPORT_TV_ID))) {
            ChannelListFragment channelListFragment = ChannelListFragment.newInstance();
            ft.replace(R.id.match_list, channelListFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        } else {
            MatchListFragment matchListFragment = MatchListFragment.newInstance(match);
            ft.replace(R.id.match_list, matchListFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    public void setFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (isImmersiveAvailable()) {
                flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void exitFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    public void changeScreenMode(boolean isFullScreen, boolean isUserSelect) {
        if (isFullScreen) {
            if (isUserSelect) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            setFullscreen(this);
        } else {
            if (isUserSelect) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            exitFullscreen(this);
        }
    }

    @Override
    @Optional()
    @OnClick({R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                shareApp(match);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startAppAd != null) {
            startAppAd.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (startAppAd != null) {
            startAppAd.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInterstitialAd = null;
        publisherInterstitialAd = null;
        startAppAd = null;
    }
}
