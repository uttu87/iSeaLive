package com.iseasoft.iseagoals;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.Lifecycle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iseasoft.iseagoals.listeners.OnConfirmationDialogListener;
import com.iseasoft.iseagoals.models.League;
import com.iseasoft.iseagoals.models.Match;
import com.iseasoft.iseagoals.widgets.ConfirmationDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

import static com.iseasoft.iseagoals.ISeaLiveConstants.GOOGLE_PLAY_APP_LINK;
import static com.iseasoft.iseagoals.ISeaLiveConstants.LEAGUE_KEY;
import static com.iseasoft.iseagoals.ISeaLiveConstants.MATCH_KEY;

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = BaseActivity.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.footer_container)
    LinearLayout footerContainer;
    @BindView(R.id.btn_share)
    FloatingActionButton btnShare;
    @BindView(R.id.today_highlight)
    TextView todayHighlight;
    @BindView(R.id.publisherAdView)
    PublisherAdView publisherAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        showFootContent(true);
        setupPublisherAds();
        LiveApplication.screenCount++;
    }

    private void setupGoogleApi() {
        /*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();*/
    }

    private void setupPublisherAds() {
        setupPublisherBannerAds();
    }


    private void setupPublisherBannerAds() {
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
    protected void onResume() {
        super.onResume();

        if (publisherAdView != null) {
            publisherAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (publisherAdView != null) {
            publisherAdView.pause();
        }
    }

    protected void navigationToPlayerScreen(Match match) {
        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MATCH_KEY, match);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void navigationToLeagueScreen(League league) {
        Intent intent = new Intent(this, LeagueDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(LEAGUE_KEY, league);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void navigationToMainScreen(boolean isFinish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (publisherAdView != null) {
            publisherAdView.destroy();
        }
        unbinder.unbind();
        unbinder = null;
    }


    protected boolean isStateSafe() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void shareApp(Match match) {
        String[] blacklist = new String[]{"com.any.package", "net.other.package"};
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = getString(R.string.share_boday);
        if (match != null) {
            shareBody = "Watch " + match.getName();
        }
        shareBody = shareBody + " at: " + GOOGLE_PLAY_APP_LINK;

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        String title = getString(match != null ? R.string.share_video_title : R.string.share_app_title);

        startActivity(Intent.createChooser(sharingIntent, title));
    }

    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;

        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;

                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.share_app_title));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, getString(R.string.share_app_title));
    }

    public void showFootContent(boolean show) {
        if (footerContainer != null) {
            footerContainer.setVisibility(show ? View.VISIBLE : View.GONE);
            final String todayStatus = LiveApplication.getTodayHighlightStatus();
            if (TextUtils.isEmpty(todayStatus)) {
                todayHighlight.setVisibility(View.GONE);
            } else {
                todayHighlight.setText(todayStatus);
                todayHighlight.setVisibility(View.VISIBLE);
                todayHighlight.setSelected(true);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void showShareButton(boolean show) {
        if (btnShare != null) {
            btnShare.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Optional()
    @OnClick({R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                shareApp(null);
                break;
        }
    }

    protected void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public void onBackPressed() {
        if (isLastBackStack()) {
            ConfirmationDialog dialog = ConfirmationDialog.newInstance(
                    getString(R.string.exit_app_dialog_title),
                    "",
                    getString(R.string.exit_app_dialog_ok),
                    new OnConfirmationDialogListener() {
                        @Override
                        public void onConfirmed() {
                            BaseActivity.super.onBackPressed();
                        }

                        @Override
                        public void onCanceled() {
                        }
                    });
            dialog.setQuitPopup(true);
            dialog.show(getSupportFragmentManager(), ConfirmationDialog.TAG);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isLastBackStack() {
        return isLastActivityStack() && isLastFragmentStack();
    }

    private boolean isLastActivityStack() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> latestTask = am.getRunningTasks(1);
        if (latestTask == null && latestTask.size() != 1) {
            return false;
        }
        ActivityManager.RunningTaskInfo task = latestTask.get(0);
        return task.numActivities == 1
                && task.topActivity.getClassName().equals(this.getClass().getName());
    }

    private boolean isLastFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        return fm.getBackStackEntryCount() == 0;
    }

}
