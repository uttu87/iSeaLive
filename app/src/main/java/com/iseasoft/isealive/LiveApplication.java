package com.iseasoft.isealive;

import android.app.Application;
import android.content.Context;

public class LiveApplication extends Application {

    public static int screenCount = 0;
    private static LiveApplication mSelf;
    private static boolean useOnlineData;
    private static boolean activeAds;
    private static boolean useAdMob;
    private static boolean useStartApp;
    private static boolean useRichAdx;
    private static String todayHighlightStatus;
    private static long interstitialAdsLimit;

    public static boolean isUseOnlineData() {
        return useOnlineData;
    }

    public static void setUseOnlineData(boolean useOnlineData) {
        LiveApplication.useOnlineData = useOnlineData;
    }

    public static boolean isDebugBuild() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }

    public static boolean isActiveAds() {
        return activeAds;
    }

    public static void setActiveAds(boolean activeAds) {
        LiveApplication.activeAds = activeAds;
    }

    public static boolean isUseAdMob() {
        return useAdMob;
    }

    public static void setUseAdMob(boolean useAdMob) {
        LiveApplication.useAdMob = useAdMob;
    }

    public static boolean isUseStartApp() {
        return useStartApp;
    }

    public static void setUseStartApp(boolean useStartApp) {
        LiveApplication.useStartApp = useStartApp;
    }

    public static boolean isUseRichAdx() {
        return useRichAdx;
    }

    public static void setUseRichAdx(boolean useRichAdx) {
        LiveApplication.useRichAdx = useRichAdx;
    }

    public static long getInterstitialAdsLimit() {
        return interstitialAdsLimit;
    }

    public static void setInterstitialAdsLimit(long interstitialAdsLimit) {
        LiveApplication.interstitialAdsLimit = interstitialAdsLimit;
    }

    public static String getTodayHighlightStatus() {
        return todayHighlightStatus;
    }

    public static void setTodayHighlightStatus(String todayHighlightStatus) {
        LiveApplication.todayHighlightStatus = todayHighlightStatus;
    }

    public static LiveApplication self() {
        return mSelf;
    }

    public static LiveApplication getApplication() {
        return self();
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        /*
        if (LiveApplication.isDebugBuild()) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
        */
    }
}
