package com.elite.model;

/**
 * @author Wesker
 * @create 2019-01-21 14:09
 */
public class WeekModel {
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;

    public String getMonday() {
        return monday;
    }

    public void setMonday(String mMonday) {
        monday = mMonday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String mTuesday) {
        tuesday = mTuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String mWednesday) {
        wednesday = mWednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String mThursday) {
        thursday = mThursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String mFriday) {
        friday = mFriday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String mSaturday) {
        saturday = mSaturday;
    }

    @Override
    public String toString() {
        return "WeekModel{" +
                "monday='" + monday + '\'' +
                ", tuesday='" + tuesday + '\'' +
                ", wednesday='" + wednesday + '\'' +
                ", thursday='" + thursday + '\'' +
                ", friday='" + friday + '\'' +
                ", saturday='" + saturday + '\'' +
                '}';
    }
}
