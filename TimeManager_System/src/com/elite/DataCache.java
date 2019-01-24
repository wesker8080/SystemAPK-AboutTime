package com.elite;

import android.content.Context;
import android.content.SharedPreferences;

import com.elite.constant.TimeLockConstant;

/**
 * Created by wesker on 2018/5/25 16:23.
 */

public class DataCache {

    private static final String SHARENAME = "elite";

    private static volatile DataCache sInstance;

    private static SharedPreferences sPreferences;

    private DataCache(Context context) {
        if (sPreferences == null) {
            sPreferences = context.getSharedPreferences(SHARENAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * 保证多线程安全，及高性能，volatile变量的操作，是不允许和它之前的读写操作打乱顺序。
     *
     * @return
     */
    public static DataCache getInstance(Context context) {
        DataCache instance = sInstance;
        if (sInstance == null) {
            synchronized (DataCache.class) {
                instance = sInstance;
                if (instance == null) {
                    instance = new DataCache(context);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }


    /**
     * 是否第一次进入
     * @param isFirstEntry
     */
    public void setFirstEntry(boolean isFirstEntry) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean("isFirstEntry", isFirstEntry);
        editor.apply();
    }

    /**
     * 获取热点
     * @return
     */
    public boolean isFirstEntry() {
        return sPreferences.getBoolean("isFirstEntry",true);
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setTimeLockPassword(String password) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.PASSWORD, password);
        editor.apply();
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getTimeLockPassword() {
        return sPreferences.getString(TimeLockConstant.PASSWORD, TimeLockConstant.DEFAULT);
    }

    /**
     * 设置设备允许使用时间段
     * @param time 时间
     */
    public void setTimePeriodStartEnd(String time) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.TIME_PERIOD_START_END, time);
        editor.apply();
    }
    /**
     * 获取设备允许开始使用时间
     *
     */
    public String getTimePeriodStartEnd() {
        return sPreferences.getString(TimeLockConstant.TIME_PERIOD_START_END, TimeLockConstant.TIME_PERIOD_START_DEFAULT);
    }
    /**
     * 设置设备允许使用时间段
     * @param time 时间
     */
    public void setTimePeriod(String time) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.TIME_PERIOD, time);
        editor.apply();
    }
    /**
     * 获取设备允许使用时间
     *
     */
    public String getTimePeriod() {
        return sPreferences.getString(TimeLockConstant.TIME_PERIOD, null);
    }

    /**
     * 设置设备使用时长
     * @param time
     */
    public void setTimeAvailable(String time) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.TIME_AVAILABLE, time);
        editor.apply();
    }

    /**
     * 获取设备使用时长
     *
     */
    public String getTimeAvailable() {
        return sPreferences.getString(TimeLockConstant.TIME_AVAILABLE, null);
    }

    /**
     * 设置时间锁是否开启
     * @param isEnable
     */
    public void setTimeLockEnable(String isEnable) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.TIME_LOCK_ENABLE, isEnable);
        editor.apply();
    }

    /**
     * 时间锁是否开启
     * @return true:开启 false:关闭
     */
    public String getTimeLockEnable() {
        return sPreferences.getString(TimeLockConstant.TIME_LOCK_ENABLE, null);
    }

    /**
     * 设置休息相关数据
     * @param info
     */
    public void setRestLockInfo(String info) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(TimeLockConstant.REST_LOCK_INFO, info);
        editor.apply();
    }

    /**
     * 获取休息数据
     * @return 休息数据
     */
    public String getRestLockInfo() {
        return sPreferences.getString(TimeLockConstant.REST_LOCK_INFO, null);
    }
}
