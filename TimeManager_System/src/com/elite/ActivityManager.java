package com.elite;

import android.app.Activity;

import java.util.HashSet;

/**
 * @author MR.ZHANG
 * @create 2019-01-16 15:08
 */
public class ActivityManager {
    private static HashSet<Activity> mActivityes = new HashSet<>(10);
    private ActivityManager(){}
    private static class ActivityHolder {
        private static ActivityManager INSTANCE = new ActivityManager();
    }
    public static ActivityManager getInstance() {
        return ActivityHolder.INSTANCE;
    }
    /**
     * 每一个Activity 在 onCreate 方法的时候，可以装入当前this
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            mActivityes.add(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用此方法用于销毁所有的Activity，然后我们在调用此方法之前，调到登录的Activity
     */
    public void exit() {
        try {
            mActivityes.forEach(Activity::finish);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
