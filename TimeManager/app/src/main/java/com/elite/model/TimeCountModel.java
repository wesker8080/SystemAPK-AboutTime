package com.elite.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 可用时长model
 * @author Wesker
 * @create 2019-01-17 17:40
 */
public class TimeCountModel {
    private String totalTime;
    private Boolean isRepeat;
    private Boolean isEnable;
    private List<String> repeatDays;
    @JSONField(name = "time_available_id")
    private String timeAvailableId;

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String mTotalTime) {
        totalTime = mTotalTime;
    }

    public String getTimeAvailableId() {
        return timeAvailableId;
    }

    @JSONField(name = "time_available_id")
    public void setTimeAvailableId(String mTimeAvailableId) {
        timeAvailableId = mTimeAvailableId;
    }

    public List<String> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(List<String> mRepeatDays) {
        repeatDays = mRepeatDays;
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




    @Override
    public String toString() {
        return "TimePeriodModel{" +
                "totalTime='" + totalTime + '\'' +
                ", isRepeat=" + isRepeat +
                ", isEnable=" + isEnable +
                ", repeatDays=" + repeatDays +
                ", timePeriodId='" + timeAvailableId + '\'' +
                '}';
    }
}
