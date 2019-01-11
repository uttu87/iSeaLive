package com.iseasoft.isealive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.widget.TextView;

public class Utils {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isTablet(final Context context) {
        if (context == null) return false;
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static float convertDp2Px(final Context context, final float dp) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    public static String changeToDisplayTime(long msec) {
        String result = null;
        long time = msec / 1000;

        long hour = time / 3600;
        long tmp = time % 3600;

        long min = tmp / 60;
        long sec = tmp % 60;

        if (hour > 0) {
            result = String.format("%02d:%02d:%02d", hour, min, sec);
        } else {
            result = String.format("%01d:%02d", min, sec);
        }
        return result;
    }

    public static void setupToolbar(AppCompatActivity activity, String title) {
        TextView toolbarTitle = activity.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.setTitle("");
    }

    public static void modifyListViewForHorizontal(Context context, RecyclerView recyclerView) {
        modifyListView(context, recyclerView, LinearLayoutManager.HORIZONTAL);
    }

    public static void modifyListViewForVertical(Context context, RecyclerView recyclerView) {
        modifyListView(context, recyclerView, LinearLayoutManager.VERTICAL);
    }

    private static void modifyListView(Context context, RecyclerView recyclerView, int orientation) {
        final LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(orientation);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static void modifyRecylerViewForGridView(RecyclerView recyclerView,
                                                    int spanCount,
                                                    int columnWidthInDp) {
        if (spanCount == 0) {
            spanCount = getOptimalSpanCount(recyclerView, columnWidthInDp);
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setNestedScrollingEnabled(false);
    }

    public static int getOptimalSpanCount(RecyclerView recyclerView, int columnWidthInDp) {
        return getOptimalSpanCount(recyclerView.getWidth(),
                (int) Utils.convertDp2Px(recyclerView.getContext(), columnWidthInDp));
    }

    public static int getOptimalSpanCount(int recyclerViewWidth, int columnWidthInPx) {
        return (int) Math.floor(recyclerViewWidth / columnWidthInPx);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private static final String getVersionName(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            final PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSpecialUserAgent(Context context) {
        return String.format("%s %s/%s",
                new WebView(context).getSettings().getUserAgentString(),
                context.getPackageName(),
                getVersionName(context));
    }

    @SuppressLint("DefaultLocale")
    public static String getVersionString(Context context) {
        String versionName = "";
        int versionCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return String.format("%s(%d)", versionName, versionCode);

    }
}
