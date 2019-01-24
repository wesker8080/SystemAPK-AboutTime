package com.elite.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.os.SystemProperties;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.constant.TimeLockConstant;
import com.elite.utils.CommonDialog;
import com.elite.utils.TimeSetDialogUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MR.ZHANG
 * @create 2019-01-17 10:28
 */
public class SetTimePeriodActivity extends Activity {

    private final String TAG = "SetTimePeriodActivity";

    private RelativeLayout rlPeriodTimeStart;
    private RelativeLayout rlPeriodTimeEnd;
    private TextView tvPeriodTimeStartDes;
    private TextView tvPeriodTimeEndDes;
    private TextView tvPeriodTimeStart;
    private TextView tvPeriodTimeEnd;
    private CheckBox cbPeriodTimeRepeat;
    private ImageView backButton;
    private Button btnSave;
    private TimeSetDialogUtil startTimeSetDialog;
    private TimeSetDialogUtil endTimeSetDialog;
    private LinearLayout repeatDays;
    private CheckBox mon, tue, wed, thu, fri, sat, sun;
    private boolean isRepeat = true;
    private ConcurrentHashMap<String, Boolean> daysMap = new ConcurrentHashMap<>(16);
    private List<String> mTimePeriodModelList = new ArrayList<>(10);
    private String preferenceFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* Optional.ofNullable(getActionBar()).ifPresent(x -> {
            x.setDisplayHomeAsUpEnabled(true);
            x.setTitle(R.string.entry_password_to_unlock_title);
            x.setHomeButtonEnabled(true);
        });*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_time_period);
        Intent mIntent = getIntent();
        preferenceFlag = mIntent.getStringExtra(TimeLockConstant.ACTIVITY);
        initView();
        initViewController();
    }

    private void initViewController() {
        backButton.setOnClickListener(v -> finish());
        saveButtonController();
        rlPeriodTimeStart.setOnClickListener(v -> {
            if (startTimeSetDialog == null) {
                startTimeSetDialog = new TimeSetDialogUtil(this);
                startTimeSetDialog.setSelectedTime(getTextStartTime());
                startTimeSetDialog.setOnClickCallBack(t -> {
                    tvPeriodTimeStart.setText(t);
                    if (isTimePeriodPreferenceEntry() && isTimeSettingError()) {
                        setTimeSettingErrorTheme(getResources().getColor(R.color.colorPasswordRed, null));
                    } else {
                        setTimeSettingErrorTheme(getResources().getColor(R.color.colorPeriodText, null));
                    }
                });
                startTimeSetDialog.show();
            } else {
                startTimeSetDialog.show();
            }
        });
        rlPeriodTimeEnd.setOnClickListener(v -> {
            if (endTimeSetDialog == null) {
                endTimeSetDialog = new TimeSetDialogUtil(this);
                endTimeSetDialog.setSelectedTime(getTextEndTime());
                endTimeSetDialog.setOnClickCallBack(t -> {
                    tvPeriodTimeEnd.setText(t);
                    if (isTimePeriodPreferenceEntry() && isTimeSettingError()) {
                        setTimeSettingErrorTheme(getResources().getColor(R.color.colorPasswordRed, null));
                    } else {
                        setTimeSettingErrorTheme(getResources().getColor(R.color.colorPeriodText, null));
                    }
                });
                endTimeSetDialog.show();
            } else {
                endTimeSetDialog.show();
            }
        });
        cbPeriodTimeRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                repeatDays.setVisibility(View.VISIBLE);
            } else {
                repeatDays.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        btnSave = findViewById(R.id.btn_save);
        RelativeLayout topView = findViewById(R.id.rl_top_view);
        topView.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        backButton = findViewById(R.id.iv_title_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        if (isTimePeriodPreferenceEntry()) {
            tvTitle.setText(R.string.set_period_title);
        } else {
            tvTitle.setText(R.string.set_period_title);
        }
        rlPeriodTimeStart = findViewById(R.id.rl_period_time_start);
        rlPeriodTimeEnd = findViewById(R.id.rl_period_time_end);
        tvPeriodTimeStartDes = findViewById(R.id.tv_period_time_start_des);
        tvPeriodTimeEndDes = findViewById(R.id.tv_period_time_end_des);
        tvPeriodTimeStart = findViewById(R.id.tv_period_time_start);
        tvPeriodTimeEnd = findViewById(R.id.tv_period_time_end);
        cbPeriodTimeRepeat = findViewById(R.id.cb_period_time_repeat);
        repeatDays = findViewById(R.id.repeat_days);
        sun = findViewById(R.id.day_button_box_sun);
        mon = findViewById(R.id.day_button_box_mon);
        tue = findViewById(R.id.day_button_box_tue);
        wed = findViewById(R.id.day_button_box_wed);
        thu = findViewById(R.id.day_button_box_thu);
        fri = findViewById(R.id.day_button_box_fri);
        sat = findViewById(R.id.day_button_box_sat);
        if (isTimePeriodPreferenceEntry()) {
            rlPeriodTimeEnd.setVisibility(View.VISIBLE);
            tvPeriodTimeStartDes.setText(R.string.available_time_per_day);
        } else {
            tvPeriodTimeStartDes.setText(R.string.set_period_time_start);
            rlPeriodTimeEnd.setVisibility(View.GONE);
        }
    }

    /**
     * 判断两个时间前后
     * @return true为开始时间在结束时间之后
     */
    private boolean isTimeSettingError() {
        String[] start = tvPeriodTimeStart.getText().toString().split(":");
        String[] end = tvPeriodTimeEnd.getText().toString().split(":");

        LocalTime startTime = LocalTime.of(Integer.valueOf(start[0]), Integer.valueOf(start[1]));
        LocalTime endTime = LocalTime.of(Integer.valueOf(end[0]), Integer.valueOf(end[1]));
        return startTime.isAfter(endTime) || startTime.equals(endTime) ;
    }

    /**
     *  可用时长是否错误
     * @return
     */
    private boolean timeAvailableError() {
        return "00:00".equals(tvPeriodTimeStart.getText().toString());
    }



    /**
     * 当开始时间在结束时间之后时 字体颜色变红提醒
     * @param color 警告色
     */
    private void setTimeSettingErrorTheme(int color) {
        tvPeriodTimeStart.setTextColor(color);
        tvPeriodTimeStartDes.setTextColor(color);
    }


    private List<String> saveRepeatDaysInfo() {
        if (mTimePeriodModelList != null && mTimePeriodModelList.size() != 0) {
            mTimePeriodModelList.clear();
        }
        if (sun.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.SUNDAY);
        }
        if (mon.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.MONDAY);
        }
        if (tue.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.TUESDAY);
        }
        if (wed.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.WEDNESDAY);
        }
        if (thu.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.THURSDAY);
        }
        if (fri.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.FRIDAY);
        }
        if (sat.isChecked()) {
            mTimePeriodModelList.add(TimeLockConstant.SATURDAY);
        }

        return mTimePeriodModelList;
    }
    private String getTextStartTime() {
        return tvPeriodTimeStart.getText().toString();
    }
    private String getTextEndTime() {
        return tvPeriodTimeEnd.getText().toString();
    }
    private boolean periodTimeIsRepeat() {
        return cbPeriodTimeRepeat.isChecked();
    }
    private void saveButtonController() {
        btnSave.setOnClickListener(v -> {

            if (isTimePeriodPreferenceEntry() && isTimeSettingError()) {
                CommonDialog.showDialog(this, null, R.string.set_period_time_warning);
                return;
            }
            if (!isTimePeriodPreferenceEntry() && timeAvailableError()) {
                CommonDialog.showDialog(this, null, R.string.available_time_per_warning);
                return;
            }
            saveJsonInfo(isTimePeriodPreferenceEntry());
            Log.d(TAG, "periodTimeJson : " + TimeLockApplication.dataCache.getTimePeriod());
            Log.d(TAG, "availableTimeJson : " + TimeLockApplication.dataCache.getTimeAvailable());
        });
    }

    /**
     * 是否是从设置时间段进入的
     * @return true为从设置时间段进入 false为从可用时长进入
     */
    private boolean isTimePeriodPreferenceEntry() {
        if (preferenceFlag != null) {
            return preferenceFlag.equals(TimeLockConstant.ADD_AVAILABLE_TIME_PERIOD);
        }
        return false;
    }

    /**
     * 构建JSON数据
     */
    private void saveJsonInfo(boolean isTimePeriodPreferenceEntry) {
        JSONObject periodTimeJson = new JSONObject();
        JSONArray periodTimeArray = new JSONArray();
        periodTimeJson.put(TimeLockConstant.IS_ENABLE,true);
        periodTimeJson.put(TimeLockConstant.IS_REPEAT, periodTimeIsRepeat());
        periodTimeJson.put(TimeLockConstant.REAPEAT_DAYS,saveRepeatDaysInfo());
        if (isTimePeriodPreferenceEntry) {
            setTimePeriodDataCache(periodTimeJson, periodTimeArray);
        } else {
            setTimeAvailableDataCache(periodTimeJson, periodTimeArray);
        }
        setResultAndFinish();
    }

    /**
     * 存储可用时间段数据
     */
    private void setTimePeriodDataCache(JSONObject periodTimeJson, JSONArray periodTimeArray) {
        periodTimeJson.put(TimeLockConstant.TIME_PERIOD_ID, UUID.randomUUID());
        periodTimeJson.put(TimeLockConstant.START_TIME, getTextStartTime());
        periodTimeJson.put(TimeLockConstant.END_TIME, getTextEndTime());
        String mTimePeriod = TimeLockApplication.dataCache.getTimePeriod();
        if (mTimePeriod != null) {
            JSONArray mParse = JSONArray.parseArray(mTimePeriod);
            for (int i=0;i<mParse.size();i++) {
                JSONObject jsonObject = (JSONObject) JSONObject.parse(mParse.get(i).toString());
                String startTime = jsonObject.getString(TimeLockConstant.START_TIME);
                String endTime = jsonObject.getString(TimeLockConstant.END_TIME);
                boolean isRepeat = jsonObject.getBoolean(TimeLockConstant.IS_REPEAT);
                JSONArray repeatDays = jsonObject.getJSONArray(TimeLockConstant.REAPEAT_DAYS);
                // 逐层判断是否已经存在完全相同的使用时间段
                int count = 0;
                if (startTime != null && startTime.equals(getTextStartTime())) {
                    if (endTime != null && endTime.equals(getTextEndTime())) {
                        if (isRepeat == periodTimeIsRepeat()) {
                            for (int b = 0; b < repeatDays.size(); b++) {
                                if (mTimePeriodModelList.contains(repeatDays.getString(b))) {
                                    Log.d(TAG, "repeatDays : " + repeatDays.getString(b));
                                    count ++;
                                }
                            }
                            Log.d(TAG, "count = " + count + "days : " + repeatDays.size());
                            if (count == repeatDays.size()) {
                                CommonDialog.showDialog(SetTimePeriodActivity.this, null, R.string.already_has_this_time);
                                return;
                            }
                        }
                    }
                }
            }
            mParse.add(periodTimeJson);
            TimeLockApplication.dataCache.setTimePeriod(mParse.toString());
        } else {
            periodTimeArray.add(periodTimeJson);
            TimeLockApplication.dataCache.setTimePeriod(periodTimeArray.toString());
        }
    }

    /**
     * 存储可用时长数据
     */
    private void setTimeAvailableDataCache(JSONObject periodTimeJson, JSONArray periodTimeArray) {
        periodTimeJson.put(TimeLockConstant.TIME_AVAILABLE_ID, UUID.randomUUID());
        periodTimeJson.put(TimeLockConstant.TOTAL_TIME, getTextStartTime());
        String mTimePeriod = TimeLockApplication.dataCache.getTimeAvailable();
        if (mTimePeriod != null) {
            JSONArray mParse = JSONArray.parseArray(mTimePeriod);
            for (int i=0;i<mParse.size();i++) {
                JSONObject jsonObject = (JSONObject) JSONObject.parse(mParse.get(i).toString());
                String startTime = jsonObject.getString(TimeLockConstant.TOTAL_TIME);
                boolean isRepeat = jsonObject.getBoolean(TimeLockConstant.IS_REPEAT);
                JSONArray repeatDays = jsonObject.getJSONArray(TimeLockConstant.REAPEAT_DAYS);
                // 逐层判断是否已经存在完全相同的使用时间段
                int count = 0;
                if (startTime != null && startTime.equals(getTextStartTime())) {
                    if (isRepeat == periodTimeIsRepeat()) {
                        for (int b = 0; b < repeatDays.size(); b++) {
                            if (mTimePeriodModelList.contains(repeatDays.getString(b))) {
                                count ++;
                            }
                        }
                        if (count == repeatDays.size()) {
                            CommonDialog.showDialog(SetTimePeriodActivity.this, null, R.string.already_has_this_time);
                            return;
                        }
                    }
                }
            }
            mParse.add(periodTimeJson);
            TimeLockApplication.dataCache.setTimeAvailable(mParse.toString());
        } else {
            periodTimeArray.add(periodTimeJson);
            TimeLockApplication.dataCache.setTimeAvailable(periodTimeArray.toString());
        }
    }

    private void setResultAndFinish() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
