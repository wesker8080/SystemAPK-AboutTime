package com.elite.constant;

/**
 * @author MR.ZHANG
 * @create 2019-01-16 17:37
 */
public interface TimeLockConstant {
    /**
     *  时间锁是否开启
     */
    String TIME_LOCK_ENABLE = "time_lock";

    /**
     *  休息开关是否开启
     */
    String REST_LOCK_INFO = "rest_lock_info";

    String DEFAULT = "default";

    /**
     * 资源前缀 获取语言国际化时使用
     */
    String DAY_RESOURCE_PREFIX = "period_time_";

    /**
     * string资源类型 获取语言国际化时使用
     */
    String RESOURCE_TYPE = "string";

    /**
     * SetPasswordActivity 请求码
     */
    int SET_PASSWORD_REQUEST_CODE = 8;
    /**
     * SetTimePeriodActivity 请求码
     */
    int MODIFY_PASSWORD_REQUEST_CODE = 11;
    /**
     *  跳转Activity标志
     */
    String ACTIVITY = "activity";
    String ADD_AVAILABLE_TIME_PERIOD = "addAvailableTimePeriod";
    String ADD_AVAILABLE_TIME = "addAvailableTime";
    String MODIFY_PASSWORD = "modify_password";
    int SET_TIME_PERIOD_REQUEST_CODE = 9;
    int SET_TIME_AVAILABLE_REQUEST_CODE = 10;
    String PASSWORD = "password";
    String TIME_PERIOD_START_END = "time_period_start_end";
    String TIME_PERIOD_START_DEFAULT = "12:00,13:00";
    String TIME_PERIOD = "time_period";
    String TIME_PERIOD_ID = "time_period_id";
    String TIME_AVAILABLE_ID = "time_available_id";
    String IS_REPEAT = "isRepeat";
    String IS_ENABLE = "isEnable";
    String START_TIME = "startTime";
    String END_TIME = "endTime";
    /**
     * 可用时长
     */
    String TOTAL_TIME = "totalTime";
    String REAPEAT_DAYS = "repeatDays";
    String SUNDAY = "sunday";
    String MONDAY = "monday";
    String TUESDAY = "tuesday";
    String WEDNESDAY = "wednesday";
    String THURSDAY = "thursday";
    String FRIDAY = "friday";
    String SATURDAY = "saturday";
    String TIME_AVAILABLE = "time_available";

    String HOME_KEY_INTERCEPT = "home_key_intercept";
    String HOME_VALUE_INTERCEPT = "home_value_intercept";

    /**************************休息相关缓存********************/

    interface RestConstant {
        String REST_ENABLE = "rest_enable";
        String REST_LEARN_LENGTH = "rest_learn_length";
        String REST_REST_LENGTH = "rest_rest_length";
        String REST_TRUE = "true";
        int MINUTE_60 = 60;
        int MINUTE_45 = 45;
        int MINUTE_30 = 30;
        int MINUTE_15 = 15;
        int MINUTE_10 = 10;
    }
}
