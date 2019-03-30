package com.iseasoft.iseafootball;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.iseasoft.iseafootball.api.APIListener;
import com.iseasoft.iseafootball.api.ISeaLiveAPI;
import com.iseasoft.iseafootball.references.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import static com.iseasoft.iseafootball.ISeaLiveConstants.ACTIVE_ADS_KEY;
import static com.iseasoft.iseafootball.ISeaLiveConstants.ACTIVE_FULLMATCH;
import static com.iseasoft.iseafootball.ISeaLiveConstants.ADS_TYPE;
import static com.iseasoft.iseafootball.ISeaLiveConstants.INTERSTITIAL_ADS_LIMIT;
import static com.iseasoft.iseafootball.ISeaLiveConstants.LIVE_SCORE_URL;
import static com.iseasoft.iseafootball.ISeaLiveConstants.TODAY_HIGHLIGHT_STATUS;
import static com.iseasoft.iseafootball.ISeaLiveConstants.USE_ADMOB;
import static com.iseasoft.iseafootball.ISeaLiveConstants.USE_ONLINE_DATA_FLAG_KEY;
import static com.iseasoft.iseafootball.ISeaLiveConstants.USE_RICHADX;
import static com.iseasoft.iseafootball.ISeaLiveConstants.USE_STARTAPP;

public class SplashActivity extends AppCompatActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefs.getInstance().increaseAppOpenCount();
        setupFireStoreSettings();
        setupFirebaseRemoteConfig();
        ISeaLiveAPI.getInstance().getConfig(new APIListener<Task<QuerySnapshot>>() {
            @Override
            public void onRequestCompleted(Task<QuerySnapshot> tasks, String json) {
                boolean isActiveAds = false;
                boolean useOnlineData = false;
                if (tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        try {
                            JSONObject jsonObject = new JSONObject(document.getData());
                            if (jsonObject.has(ACTIVE_ADS_KEY)) {
                                isActiveAds = jsonObject.getBoolean(ACTIVE_ADS_KEY);
                            }

                            if (jsonObject.has(USE_ONLINE_DATA_FLAG_KEY)) {
                                useOnlineData = jsonObject.getBoolean(USE_ONLINE_DATA_FLAG_KEY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                LiveApplication.setActiveAds(isActiveAds);
                LiveApplication.setUseOnlineData(useOnlineData);

                navigationToMainScreen();
            }

            @Override
            public void onError(Error e) {
                navigationToMainScreen();
            }
        });


    }

    private void setupFireStoreSettings() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    private void navigationToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        Intent launchIntent = getIntent();
        if (launchIntent != null) {
            String url = launchIntent.getStringExtra(ISeaLiveConstants.PUSH_URL_KEY);
            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(ISeaLiveConstants.PUSH_URL_KEY, url);
            }

            String message = launchIntent.getStringExtra(ISeaLiveConstants.PUSH_MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(ISeaLiveConstants.PUSH_MESSAGE, message);
            }
        }

        startActivity(intent);
        finish();
    }


    private void navigationToAdminScreen() {
        Intent launchIntent = getIntent();
        if (launchIntent != null) {
            boolean launchFromPush = false;
            Intent intent = new Intent(this, MainActivity.class);
            String url = launchIntent.getStringExtra(ISeaLiveConstants.PUSH_URL_KEY);
            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(ISeaLiveConstants.PUSH_URL_KEY, url);
                launchFromPush = true;
            }

            String message = launchIntent.getStringExtra(ISeaLiveConstants.PUSH_MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(ISeaLiveConstants.PUSH_MESSAGE, message);
            }
            if (launchFromPush) {
                startActivity(intent);
                finish();
                return;
            }
        }

        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfig();
    }

    private void fetchRemoteConfig() {
        long cacheExpiration = 3600; // seconds.

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            if (mFirebaseRemoteConfig != null) {
                                mFirebaseRemoteConfig.activateFetched();
                            }
                        }
                        applyRemoteConfig();
                    }
                });
        // [END fetch_config_with_callback]
    }

    private void applyRemoteConfig() {
        LiveApplication.setActiveFullMatch(mFirebaseRemoteConfig.getBoolean(ACTIVE_FULLMATCH));
        LiveApplication.setTodayHighlightStatus(mFirebaseRemoteConfig.getString(TODAY_HIGHLIGHT_STATUS));
        if (!TextUtils.isEmpty(mFirebaseRemoteConfig.getString(LIVE_SCORE_URL))) {
            LiveApplication.setLiveScoreUrl(mFirebaseRemoteConfig.getString(LIVE_SCORE_URL));
        }
        LiveApplication.setUseAdMob(mFirebaseRemoteConfig.getBoolean(USE_ADMOB));
        LiveApplication.setUseStartApp(mFirebaseRemoteConfig.getBoolean(USE_STARTAPP));
        LiveApplication.setUseRichAdx(mFirebaseRemoteConfig.getBoolean(USE_RICHADX));
        LiveApplication.setInterstitialAdsLimit(mFirebaseRemoteConfig.getLong(INTERSTITIAL_ADS_LIMIT));
        LiveApplication.setAdsType(mFirebaseRemoteConfig.getLong(ADS_TYPE));
    }
}
