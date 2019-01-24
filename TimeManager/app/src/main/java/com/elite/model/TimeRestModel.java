package com.elite.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Wesker
 * @create 2019-01-23 11:19
 */
public class TimeRestModel {

    @JSONField(name = "rest_enable")
    private boolean restEnable;

    @JSONField(name = "rest_learn_length")
    private String restLearnLength;

    @JSONField(name = "rest_rest_length")
    private String restRestLength;

    public boolean isRestEnable() {
        return restEnable;
    }

    @JSONField(name = "rest_enable")
    public void setRestEnable(boolean mRestEnable) {
        restEnable = mRestEnable;
    }

    public String getRestLearnLength() {
        return restLearnLength;
    }

    @JSONField(name = "rest_learn_length")
    public void setRestLearnLength(String mRestLearnLength) {
        restLearnLength = mRestLearnLength;
    }

    public String getRestRestLength() {
        return restRestLength;
    }

    @JSONField(name = "rest_rest_length")
    public void setRestRestLength(String mRestRestLength) {
        restRestLength = mRestRestLength;
    }

    @Override
    public String toString() {
        return "TimeRestModel{" +
                "restEnable=" + restEnable +
                ", restLearnLength='" + restLearnLength + '\'' +
                ", restRestLength='" + restRestLength + '\'' +
                '}';
    }
}
