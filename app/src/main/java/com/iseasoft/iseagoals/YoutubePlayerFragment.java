package com.iseasoft.iseagoals;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.iseasoft.iseagoals.listeners.FragmentEventListener;
import com.iseasoft.iseagoals.models.Match;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.iseasoft.iseagoals.ISeaLiveConstants.MATCH_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.YOUTUBE_API_KEY;

public class YoutubePlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.OnFullscreenListener {
    private static final String TAG = YoutubePlayerFragment.class.getSimpleName();
    Unbinder unbinder;
    //@BindView(R.id.adView)
    //AdView mAdView;

    private YouTubePlayer youTubePlayer;
    private Match match;
    private String mVideoUrl;
    private FragmentEventListener fragmentEventListener;

    public static YoutubePlayerFragment newInstance(Match match) {
        YoutubePlayerFragment fragment = new YoutubePlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(MATCH_KEY, match);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragmentEventListener(FragmentEventListener fragmentEventListener) {
        this.fragmentEventListener = fragmentEventListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            match = (Match) getArguments().getSerializable(MATCH_KEY);
            mVideoUrl = match.getStreamUrl();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initializeYoutubePlayer() {
        YouTubePlayerSupportFragment youTubePlayerSupportFragment =
                (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtubesupportfragment);
        youTubePlayerSupportFragment.initialize(YOUTUBE_API_KEY, this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeYoutubePlayer();
        //setupInternalBannerAds();
        ((BaseActivity) getActivity()).showFootContent(false);
    }

    /*

    private void setupInternalBannerAds() {
        ((BaseActivity) getActivity()).showFootContent(false);
        AdRequest adRequest = new AdRequest.Builder().build();

        if (LiveApplication.isDebugBuild()) {
            mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id_test));
        } else {
            mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        }

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mAdView != null) {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    */

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        youTubePlayer.setOnFullscreenListener(this);
        youTubePlayer.loadVideo(mVideoUrl);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayer = null;
        match = null;
        unbinder.unbind();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        if (fragmentEventListener != null) {
            fragmentEventListener.changeScreenMode(isFullscreen, true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (fragmentEventListener == null) {
            return;
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentEventListener.changeScreenMode(true, false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentEventListener.changeScreenMode(false, false);
        }

    }
}
