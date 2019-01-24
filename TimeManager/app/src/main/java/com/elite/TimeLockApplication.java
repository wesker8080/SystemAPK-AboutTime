package com.elite;

import android.app.Application;
import android.content.Context;

/**
 * @author MR.ZHANG
 * @create 2019-01-16 12:27
 */
public class TimeLockApplication extends Application {

    public static DataCache dataCache;
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        dataCache = DataCache.getInstance(this);
    }
}
