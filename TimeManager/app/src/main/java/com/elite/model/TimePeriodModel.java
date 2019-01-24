package com.elite.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 可用时间段model
 * @author Wesker
 * @create 2019-01-17 17:40
 */
public class TimePeriodModel {
    private String startTime;
    private String endTime;
    private Boolean isRepeat;
    private Boolean isEnable;
    private List<String> repeatDays;
    @JSONField(name = "time_period_id")
    private String timePeriodId;

    public List<String> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(List<String> mRepeatDays) {
        repeatDays = mRepeatDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String mStartTime) {
        startTime = mStartTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String mEndTime) {
        endTime = mEndTime;
    }

    public Boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean mRepeat) {
        isRepeat = mRepeat;
    }

    public Boolean isEnable() {
        return isEnable;
    }

    public void setEnable(Boolean mEnable) {
        isEnable = mEnable;
    }


    public String getTimePeriodId() {
        return timePeriodId;
    }

    @JSONField(name = "time_period_id")
    public void setTimePeriodId(String mTimePeriodId) {
        timePeriodId = mTimePeriodId;
    }

    @Override
    public String toString() {
        return "TimePeriodModel{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", isRepeat=" + isRepeat +
                ", isEnable=" + isEnable +
                ", repeatDays=" + repeatDays +
                ", timePeriodId='" + timePeriodId + '\'' +
                '}';
    }
}
