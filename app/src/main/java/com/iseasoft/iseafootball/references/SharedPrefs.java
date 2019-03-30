package com.iseasoft.iseafootball.references;

import android.content.Context;
import android.content.SharedPreferences;

import com.iseasoft.iseafootball.LiveApplication;

public class SharedPrefs {
    private static final String PREFS_NAME = "share_prefs";
    private static final String APP_OPEN_COUNT = "app_open_count";
    private static final String APP_RATED = "app_rated";
    private static SharedPrefs mInstance;
    private SharedPreferences mSharedPreferences;

    private SharedPrefs() {
        mSharedPreferences = LiveApplication.self().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance() {
        if (mInstance == null) {
            mInstance = new SharedPrefs();
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        }
        return null;
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        }
        editor.apply();
    }

    public boolean isAppRated() {
        return get(APP_RATED, Boolean.class);
    }

    public void setAppRated() {
        put(APP_RATED, true);
    }

    public int getAppOpenCount() {
        int count = get(APP_OPEN_COUNT, Integer.class);
        return count;
    }

    public void increaseAppOpenCount() {
        if (isAppRated()) {
            return;
        }
        int count = get(APP_OPEN_COUNT, Integer.class);
        count += 1;
        put(APP_OPEN_COUNT, count);
    }

    public void resetAppOpenCount() {
        if (isAppRated()) {
            return;
        }
        put(APP_OPEN_COUNT, 0);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
