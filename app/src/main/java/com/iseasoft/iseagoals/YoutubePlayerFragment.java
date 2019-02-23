package com.iseasoft.iseagoals;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.iseasoft.iseagoals.listeners.FragmentEventListener;
import com.iseasoft.iseagoals.models.Match;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.iseasoft.iseagoals.ISeaLiveConstants.MATCH_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.YOUTUBE_API_KEY;

public class YoutubePlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.OnFullscreenListener {
    private static final String TAG = YoutubePlayerFragment.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.publisherAdView)
    PublisherAdView publisherAdView;

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
        setupInternalBannerAds();
        ((BaseActivity) getActivity()).showFootContent(false);
    }


    private void setupInternalBannerAds() {
        ((BaseActivity) getActivity()).showFootContent(false);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();

        publisherAdView.loadAd(adRequest);
        publisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (publisherAdView != null) {
                    publisherAdView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

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
