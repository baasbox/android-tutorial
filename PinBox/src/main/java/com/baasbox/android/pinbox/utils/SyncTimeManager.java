package com.baasbox.android.pinbox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eto on 18/01/14.
 */
public class SyncTimeManager {
    private final static String SYNC_PREFERENCES = "LAST_SYNC";
    private final static String LAST_SYNC_TIME = "LAST_SYNC_KEY";

    private final SharedPreferences preferences;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SyncTimeManager(Context context) {
        preferences = context.getSharedPreferences(SYNC_PREFERENCES, Context.MODE_PRIVATE);

    }

    public String getSyncTime() {
        String formattedSync;
        long lastSyncTime = preferences.getLong(LAST_SYNC_TIME, 0);
        Date d = new Date();
        d.setTime(lastSyncTime);
        formattedSync = format.format(d);
        preferences.edit().putLong(LAST_SYNC_TIME, System.currentTimeMillis()).commit();
        return formattedSync;
    }

    public void resetSyncTime() {
        preferences.edit().remove(LAST_SYNC_TIME).commit();
    }
}
