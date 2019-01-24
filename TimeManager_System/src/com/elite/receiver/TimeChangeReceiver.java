package com.elite.receiver;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.constant.TimeLockConstant;
import com.elite.model.TimeCountModel;
import com.elite.model.TimePeriodModel;
import com.elite.model.TimeRestModel;
import com.elite.utils.TimeLockDialogUtil;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


import android.os.SystemProperties;


/**
 * @author MR.ZHANG
 * @create 2018-12-27 16:18
 */
public class TimeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "TimeChangeReceiver";

    private static final String TIME_LOCK_SWITCH = "time_lock_switch";
    private static final String INTENT_ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private static final String INTENT_ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private TimeLockDialogUtil mLockDialog;
    private LocalDateTime screenOnDate;
    private LocalDateTime screenOffDate;
    private LocalDate passDay = LocalDate.now();
    private long totalUsedTime = 0;
    /**
     * 累计使用时间达到 开始锁屏
     */
    private long tempTotalUsedTime = 0;
    private static final String AVAILABLE = "available";
    private static final String UNAVAILABLE = "unavailable";
    private static final String ALL_CLOSE = "allClose";
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case INTENT_ACTION_SCREEN_ON:
                if (!timeLockIsEnable() || isDialogShowing()) {
                    return;
                }
                screenOnDate = LocalDateTime.now();
                if (screenOffDate != null) {
                    Duration period = Duration.between(screenOffDate, screenOnDate);
                    String mInfo = getRestCache();
                   if (mInfo != null) {
                       TimeRestModel mModel = JSONObject.parseObject(mInfo, TimeRestModel.class);
                       String restTime = mModel.getRestRestLength();
                       if (period.toMinutes() > Long.valueOf(restTime)) {
                           Log.d(TAG, "rest enough.rest tempTotalUsedTime");
                           tempTotalUsedTime = 0;
                       }
                   }
                }
                break;
            case INTENT_ACTION_SCREEN_OFF:
                if (!timeLockIsEnable() || isDialogShowing()) {
                    return;
                }
                screenOffDate = LocalDateTime.now();
                if (screenOnDate != null) {
                    Duration period = Duration.between(screenOnDate, screenOffDate);
                    long used = period.getSeconds();
                    totalUsedTime += used;
                    Log.d(TAG, "in this time use  : " + used + "sec");
                    Log.d(TAG, "total time use  : " + totalUsedTime + "sec");
                }
                break;
            case Intent.ACTION_TIME_TICK:
                Log.d(TAG, "1min time pass");
                if (isOneDayPass()) {
                    totalUsedTime = 0;
                    Log.d(TAG, "rest total time as : " + totalUsedTime);
                }
                if (!timeLockIsEnable()) {
                    Log.d(TAG, "time lock is unable");
                    return;
                }
                String mTimePeriod = getTimePeriodCache();
                if (mTimePeriod != null) {
                    Log.d(TAG, "mTimePeriod : " + mTimePeriod);
                    String status = isUnAvailableOnThisTimePeriod(mTimePeriod);
                    if (Objects.equals(status, ALL_CLOSE)) {
                        return;
                    }
                    if (Objects.equals(status, UNAVAILABLE)) {
                        showLockingScreenDialog(context, R.string.time_lock_password, R.string.text_not_available);
                        return;
                    }
                    String mTime = getTimeCache();
                    if (mTime != null) {
                        LocalTime mMaxAvailableTime = getMaxAvailableTime(mTime);
                        if (mMaxAvailableTime != null && isOutOfAvailableTimes(mMaxAvailableTime)) {
                            showLockingScreenDialog(context, R.string.time_lock_password, R.string.text_time_run_out);
                            return;
                        }
                    }
                }
                restTimeConfig(context);
                break;
            case Intent.ACTION_TIME_CHANGED:
                Log.d(TAG, "system time changed");
                break;
            case Intent.ACTION_TIMEZONE_CHANGED:
                Log.d(TAG, "system time zone changed");
                break;
            default:break;
        }
    }

    /**
     * 字符串转时间
     * @param time 文本类型时间 如 12:00
     * @return
     */
    private LocalTime getTime(String time) {
        if (Objects.isNull(time)) {
            Log.e(TAG, "time is null while do getTime method");
            return null;
        }
        String[] startTimeArr = time.split(":");
        return LocalTime.of(Integer.valueOf(startTimeArr[0]), Integer.valueOf(startTimeArr[1]));
    }

    /**
     *  判断是否是可用时间段
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true为当前时间设备可用
     */
    private boolean isAvailableTime (String startTime, String endTime) {
        if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
            Log.e(TAG, "startTime is null or endTime is null while do isAvailableTime method");
            return false;
        }
        LocalTime now = LocalTime.now();
        LocalTime start = getTime(startTime);
        LocalTime end = getTime(endTime);
        return now.isBefore(end) && now.isAfter(start);
    }

    /**
     * dialog是否正在显示
     * @return true为正在显示
     */
    private boolean isDialogShowing() {
        boolean flag = mLockDialog != null && mLockDialog.isShowing();
        if (flag) {
            Log.d(TAG, "锁屏已经在显示当中...");
        }
        return flag;
    }

    /**
     * 当前时间段是否可用
     * @param mTimePeriod
     * @return true 为当前时间段不可用
     */
    private String isUnAvailableOnThisTimePeriod(String mTimePeriod) {
        List<TimePeriodModel> timePeriodList = JSONArray.parseArray(mTimePeriod, TimePeriodModel.class);
        if (timePeriodList != null && timePeriodList.size() > 0) {
            List<TimePeriodModel> mCollect = timePeriodList.stream()
                    .filter(TimePeriodModel::isEnable)
                    .collect(Collectors.toList());
            if (mCollect != null && mCollect.size() > 0) {
                for (TimePeriodModel mModel : mCollect) {
                    if (!isAvailableTime(mModel.getStartTime(), mModel.getEndTime())) {
                        // 不在可用时间
                        Log.d(TAG, "is unavailable time period while do isUnAvailableOnThisTimePeriod method");
                        return UNAVAILABLE;
                    } else {
                        if (mModel.isRepeat()) {
                            // 设置了重复 今天是否在重复列表 true为在
                            boolean isToday = repeatDaysIncludeToDay(mModel.getRepeatDays());
                            if (!isToday) {
                                Log.d(TAG, "today is unavailable while do isUnAvailableOnThisTimePeriod method");
                                return UNAVAILABLE;
                            }
                        }
                        return AVAILABLE;
                    }
                }
            } else {
                // 全部是关闭状态
                Log.d(TAG, "all of time period is close while do isUnAvailableOnThisTimePeriod method");
                return ALL_CLOSE;
            }
        }
        // 没有创建
        Log.d(TAG, "not create time period while do isUnAvailableOnThisTimePeriod method");
        return ALL_CLOSE;
    }

    /**
     * 筛选出最大可用时长
     * @param mTimeAvailable 可用时长Json
     */
    private LocalTime getMaxAvailableTime(String mTimeAvailable) {
        List<TimeCountModel> timesList = JSONArray.parseArray(mTimeAvailable, TimeCountModel.class);
        List<TimeCountModel> mCollect = timesList.stream()
                .filter(TimeCountModel::isEnable)
                .collect(Collectors.toList());
        if (mCollect != null && mCollect.size() > 0) {
            List<TimeCountModel> available = new ArrayList<>(timesList.size());
            for (TimeCountModel mModel : mCollect) {
                if (mModel.isRepeat()) {
                    if (repeatDaysIncludeToDay(mModel.getRepeatDays())) {
                        // 设置重复 今天是可用
                        available.add(mModel);
                    }
                } else {
                    // 没有设置重复
                    available.add(mModel);
                }
            }
            Log.d(TAG, "get available time length while do getMaxAvailableTime method : " + available);
            if (available.size() > 0) {
                Optional<TimeCountModel> max = available.stream()
                        .max(Comparator.comparing(TimeCountModel::getTotalTime));
                if (max.isPresent()) {
                    String textMaxTime = max.get().getTotalTime();
                    Log.d(TAG, "get availableMaxTime while do getMaxAvailableTime method : " + textMaxTime);
                    return getTime(textMaxTime);
                }
            }
            Log.d(TAG, "today  is unavailable while do getMaxAvailableTime method");
            return null;
        }
        // 可用时长全部未开启
        Log.d(TAG, "all of available time is close while do getMaxAvailableTime method");
        return null;
    }

    /**
     * 判断是否超出可用时长
     * @param maxTimes 最大可用时长
     * @return
     */
    private boolean isOutOfAvailableTimes(LocalTime maxTimes) {
        return maxTimes != null && maxTimes.getSecond() < totalUsedTime;
    }

    /**
     * 锁住屏幕
     */
    private void showLockingScreenDialog(Context context, int title, int message) {
        if (isDialogShowing()) {
            return;
        }
        View view = View.inflate(context, R.layout.dialog_entry_password, null);
        mLockDialog = null;
        mLockDialog = new TimeLockDialogUtil(context, view, false, true);
        mLockDialog.setDialogListener(new TimeLockDialogUtil.DialogListener() {
            @Override
            public void onCancel(boolean isDismiss) {
                //closeTimePeriodSwitch();
            }

            @Override
            public void onSuccess(boolean isDismiss) {
                mLockDialog.dismiss();
                SystemProperties.set(TimeLockConstant.HOME_KEY_INTERCEPT, TimeLockConstant.DEFAULT);
            }
        });
        mLockDialog.setDialogTitle(title);
        mLockDialog.setDialogMessage(message);
        mLockDialog.setBackButtonEnable(false);
        mLockDialog.show();
    }

    /**
     * 是否过了一天
     * @return
     */
    private boolean isOneDayPass() {
        LocalDate nowDay = LocalDate.now();
        Log.d(TAG, "old day is : " + passDay.toString() + "  now day is : " + nowDay.toString());
        Period period = Period.between(passDay, nowDay);
        if (period.getDays() > 0) {
            passDay = LocalDate.now();
            return true;
        }
        return false;
    }

    /**
     * 在锁定时解锁使用，把当前时间段的开关关闭，避免一会又重新锁屏
     */
    private void closeTimePeriodSwitch() {
        String timePeriod = getTimePeriodCache();
        if (timePeriod != null) {
            List<TimePeriodModel> mPeriodModelList = selectAvailableTimePeriods(timePeriod);
            if (mPeriodModelList != null && mPeriodModelList.size() > 0) {
                // 先筛选出需要关闭的时间段id
                List<String> mPeriodIdList = mPeriodModelList.stream()
                        .map(TimePeriodModel::getTimePeriodId)
                        .collect(Collectors.toList());
                // 根据id把开关关掉
                JSONArray timePeriods = JSONArray.parseArray(timePeriod);
                mPeriodIdList.forEach(v -> {
                    for (int i = 0; i < timePeriods.size(); i++) {
                        JSONObject mParse = (JSONObject) JSONObject.parse(timePeriods.get(i).toString());
                        if (Objects.equals(v, mParse.get(TimeLockConstant.TIME_PERIOD_ID))) {
                            timePeriods.remove(mParse);
                            mParse.put(TimeLockConstant.IS_ENABLE, false);
                            timePeriods.add(mParse);
                            break;
                        }
                    }
                });
                TimeLockApplication.dataCache.setTimePeriod(timePeriods.toString());
            }
        }
    }

    private List<TimePeriodModel> selectAvailableTimePeriods(String mTimePeriod) {
        List<TimePeriodModel> timePeriodList = JSONArray.parseArray(mTimePeriod, TimePeriodModel.class);
        if (timePeriodList != null && timePeriodList.size() > 0) {
            List<TimePeriodModel> available = new ArrayList<>(timePeriodList.size());
            timePeriodList.forEach(x -> {
                if (!x.isEnable()) {
                    Log.d(TAG, "selectAvailableTimePeriods : " + x);
                    available.add(x);
                } else if (isAvailableTime(x.getStartTime(), x.getEndTime())) {
                    if (!x.isRepeat()) {
                        available.add(x);
                    } else if (repeatDaysIncludeToDay(x.getRepeatDays())) {
                        available.add(x);
                    }
                }

            });
            return available;
        }
        Log.e(TAG, "timePeriodList is null while do selectAvailableTimePeriods method");
        return null;
    }

    private String getTimePeriodCache() {
        return TimeLockApplication.dataCache.getTimePeriod();
    }

    private String getTimeCache() {
        return TimeLockApplication.dataCache.getTimeAvailable();
    }

    private String getRestCache() {
        return TimeLockApplication.dataCache.getRestLockInfo();
    }

    private boolean timeLockIsEnable() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(TimeLockApplication.getContext());
        return mPreferences.getBoolean(TIME_LOCK_SWITCH, false);
    }

    /**
     * 设置重复后判断今天是否是重复
     * @return
     */
    private boolean repeatDaysIncludeToDay (List<String> days) {
        if (days == null || days.size() == 0) {
            return false;
        }
        LocalDate toDay = LocalDate.now();
        DayOfWeek week = toDay.getDayOfWeek();
        String nowDay = week.toString().toLowerCase();
        return days.stream()
                .filter(y -> Objects.equals(y, nowDay))
                .collect(Collectors.counting()) > 0;
    }

    /**
     * 设置休息
     */
    private void restTimeConfig(Context context) {
        String mInfo = getRestCache();
        if (mInfo != null) {
            TimeRestModel mModel = JSONObject.parseObject(mInfo, TimeRestModel.class);
            if (mModel != null) {
                if (mModel.isRestEnable()) {
                    // 增加1分钟
                    tempTotalUsedTime += 1;
                    String learnTime = mModel.getRestLearnLength();
                    if (tempTotalUsedTime >= Integer.valueOf(learnTime)) {
                        // 到达休息时间
                        Log.d(TAG, "now is our recess time");
                        showLockingScreenDialog(context, R.string.time_lock_password, R.string.text_rest_time);
                    }
                }
            }
        }
    }
}
