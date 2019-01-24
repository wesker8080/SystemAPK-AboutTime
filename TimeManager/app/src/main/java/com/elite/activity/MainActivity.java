package com.elite.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.constant.TimeLockConstant;
import com.elite.utils.CommonDialog;
import com.elite.utils.CommonUtils;
import com.elite.utils.TimeLockDialogUtil;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
//import android.os.SystemProperties;

/**
 * @author MR.ZHANG
 * @create 2019-01-15 11:14
 */
public class MainActivity extends PreferenceActivity {

    private static final String TAG = "MainActivity";

    private static final String TIME_LOCK_SWITCH = "time_lock_switch";
    private static final String TIME_LOCK_SUMMARY = "time_lock_summary";
    private static final String ADD_AVAILABLE_TIME_PERIOD = "add_available_time_period";
    private static final String ADD_AVAILABLE_TIME = "add_available_time";
    private static final String REST_SWITCH = "rest_switch";
    private static final String LEARN_TIME = "learn_time";
    private static final String REST_TIME = "rest_time";
    private static final String MODIFY_TIME_LOCK_PASSWORD = "modify_time_lock_password";
    private static final String LIST_AVAILABLE_TIME_PERIOD = "list_available_time_period";
    private static final String LIST_AVAILABLE_TIME = "list_available_time";

    private SwitchPreference timeLockSwitch;
    private SwitchPreference restSwitch;
    private Preference timeLockSummary;
    private Preference addAvailableTimePeriod;
    private Preference addAvailableTime;
    private ListPreference learnTime;
    private ListPreference restTime;
    private Preference modifyTimeLockPassword;
    private PreferenceCategory listAvailableTimePeriod;
    private PreferenceCategory listAvailableTime;

    private CommonUtils mCommonUtils;
    private ConcurrentHashMap<String, Object> restInfoMap = new ConcurrentHashMap<>(4);
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Optional.ofNullable(getActionBar()).ifPresent(x -> x.setDisplayHomeAsUpEnabled(true));
        addPreferencesFromResource(R.xml.time_lock_one_page);
        initController();
        findAllPreference();
        preferenceController();
        switchTimeLockEnable();
        refreshTimePeriodView();
        refreshTimeAvailableView();
        longPressEvent();
        lockTheScreen(true);
    }
    private void initController() {
        mCommonUtils = new CommonUtils();
        mCommonUtils.setTimeLockCallBackListener(() -> {
            Log.d(TAG, "setTimeLockCallBackListener");
            //mCommonUtils.showMessageDialog(TimeLockApplication.getContext());
            Intent intent = new Intent(TimeLockApplication.getContext(), SetPasswordActivity.class);
            startActivityForResult(intent, TimeLockConstant.SET_PASSWORD_REQUEST_CODE);
        });
    }

    private void preferenceController() {
        timeLockSwitch.setOnPreferenceClickListener(v -> {
            switchTimeLockEnable();
            return false;
        });
        timeLockSwitch.setOnPreferenceChangeListener((v,w) -> {
            // 首次进入
            if (TimeLockConstant.DEFAULT.equals(TimeLockApplication.dataCache.getTimeLockPassword())) {
                mCommonUtils.showMessageDialog(TimeLockApplication.getContext());
                return false;
            }
            // 准备开启时间锁功能 需要输入密码
            if (!timeLockSwitch.isChecked()) {
                lockTheScreen(false);
                return false;
            }
            Log.d(TAG, "timeLockSwitch : " + timeLockSwitch.isChecked());

            return true;
        });
        addAvailableTimePeriod.setOnPreferenceClickListener(p -> {
            Intent intent = new Intent(TimeLockApplication.getContext(), SetTimePeriodActivity.class);
            intent.putExtra(TimeLockConstant.ACTIVITY, TimeLockConstant.ADD_AVAILABLE_TIME_PERIOD);
            startActivityForResult(intent, TimeLockConstant.SET_TIME_PERIOD_REQUEST_CODE);
            return true;
        });
        addAvailableTime.setOnPreferenceClickListener(p -> {
            Intent intent = new Intent(TimeLockApplication.getContext(), SetTimePeriodActivity.class);
            intent.putExtra(TimeLockConstant.ACTIVITY, TimeLockConstant.ADD_AVAILABLE_TIME);
            startActivityForResult(intent, TimeLockConstant.SET_TIME_AVAILABLE_REQUEST_CODE);
            return true;
        });
        learnTime.setValue(getResources().getString(R.string.time_45min));
        learnTime.setOnPreferenceChangeListener((preference, newValue) -> {
            learnTime.setSummary(newValue.toString());
            setRestInfo();
            return true;
        });
        restTime.setValue(getResources().getString(R.string.time_10min));
        restTime.setOnPreferenceChangeListener((preference, newValue) -> {
            restTime.setSummary(newValue.toString());
            setRestInfo();
            return true;
        });
        modifyTimeLockPassword.setOnPreferenceClickListener(p -> {
            Intent intent = new Intent(TimeLockApplication.getContext(), SetPasswordActivity.class);
            intent.putExtra(TimeLockConstant.ACTIVITY, TimeLockConstant.MODIFY_PASSWORD);
            startActivityForResult(intent, TimeLockConstant.MODIFY_PASSWORD_REQUEST_CODE);
            return true;
        });
        restSwitch.setOnPreferenceClickListener(preference -> {
            setRestInfo();
            return true;
        });
    }
    private void switchTimeLockEnable() {
        if (timeLockSwitch.isChecked()) {
            timeLockSwitch.setTitle(R.string.system_time_lock_open);
            getPreferenceScreen().removePreference(timeLockSummary);
            switchPreferenceEnable(true);
        } else {
            timeLockSwitch.setTitle(R.string.system_time_lock_close);
            getPreferenceScreen().addPreference(timeLockSummary);
            switchPreferenceEnable(false);
        }
    }
    private void switchPreferenceEnable(boolean isEnabled) {
        addAvailableTimePeriod.setEnabled(isEnabled);
        addAvailableTime.setEnabled(isEnabled);
        learnTime.setEnabled(isEnabled);
        restSwitch.setEnabled(isEnabled);
        restTime.setEnabled(isEnabled);
        modifyTimeLockPassword.setEnabled(isEnabled);
        listAvailableTimePeriod.setEnabled(isEnabled);
        listAvailableTime.setEnabled(isEnabled);
    }
    private void findAllPreference() {
        timeLockSwitch = (SwitchPreference) findPreference(TIME_LOCK_SWITCH);
        restSwitch = (SwitchPreference) findPreference(REST_SWITCH);
        timeLockSummary = findPreference(TIME_LOCK_SUMMARY);
        addAvailableTimePeriod = findPreference(ADD_AVAILABLE_TIME_PERIOD);
        addAvailableTime = findPreference(ADD_AVAILABLE_TIME);
        learnTime = (ListPreference) findPreference(LEARN_TIME);
        restTime = (ListPreference) findPreference(REST_TIME);
        modifyTimeLockPassword = findPreference(MODIFY_TIME_LOCK_PASSWORD);
        listAvailableTimePeriod = (PreferenceCategory) findPreference(LIST_AVAILABLE_TIME_PERIOD);
        listAvailableTime = (PreferenceCategory) findPreference(LIST_AVAILABLE_TIME);
    }

    /**
     * 缓存休息相关数据
     */
    private void setRestInfo() {
        restInfoMap.put(TimeLockConstant.RestConstant.REST_ENABLE, restSwitch.isChecked());
        restInfoMap.put(TimeLockConstant.RestConstant.REST_LEARN_LENGTH, learnTime.getValue());
        restInfoMap.put(TimeLockConstant.RestConstant.REST_REST_LENGTH, restTime.getValue());
        String restInfoJson = JSON.toJSON(restInfoMap).toString();
        TimeLockApplication.dataCache.setRestLockInfo(restInfoJson);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TimeLockConstant.SET_PASSWORD_REQUEST_CODE) {
                Log.d(TAG, "config password success");
                timeLockSwitch.setChecked(true);
                switchTimeLockEnable();
            } else if (requestCode == TimeLockConstant.SET_TIME_PERIOD_REQUEST_CODE) {
                Log.d(TAG, "config time period success");
                refreshTimePeriodView();
            } else if (requestCode == TimeLockConstant.SET_TIME_AVAILABLE_REQUEST_CODE) {
                Log.d(TAG, "config time available success");
                refreshTimeAvailableView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 刷新可用时长UI
     */
    private void refreshTimeAvailableView() {
        listAvailableTime.removeAll();
        String timeJson = TimeLockApplication.dataCache.getTimeAvailable();
        if (timeJson != null) {
            JSONArray times = JSONArray.parseArray(timeJson);
            JSONArray timesTemp = JSONArray.parseArray(timeJson);
            times.forEach(x -> {
                JSONObject mParse = (JSONObject) JSONObject.parse(x.toString());
                SwitchPreference mPreference = createTimesPreference(mParse);
                mPreference.setOnPreferenceClickListener(p -> {
                    String key = mPreference.getKey();
                    times.forEach(v -> {
                        // 修改本地缓存数据
                        JSONObject json = (JSONObject) JSONObject.parse(x.toString());
                        if (key.equals(json.getString(TimeLockConstant.TIME_AVAILABLE_ID))) {
                            timesTemp.remove(json);
                            json.put(TimeLockConstant.IS_ENABLE,mPreference.isChecked());
                            timesTemp.add(json);
                            TimeLockApplication.dataCache.setTimePeriod(timesTemp.toString());
                        }
                    });
                    return false;
                });
                listAvailableTime.addPreference(mPreference);
            });
        }
    }

    /**
     * 创建可用时长SwitchPreference
     * @param mParse
     * @return
     */
    private SwitchPreference createTimesPreference(JSONObject mParse) {
        StringBuilder mBuilder = new StringBuilder();
        SwitchPreference mPreference = new SwitchPreference(TimeLockApplication.getContext());
        mPreference.setKey(mParse.getString(TimeLockConstant.TIME_AVAILABLE_ID));
        String totalTime = mParse.getString(TimeLockConstant.TOTAL_TIME);
        mBuilder.append(totalTime.split(":")[0]);
        mBuilder.append(getResources().getString(R.string.available_time_per_hour));
        mBuilder.append(totalTime.split(":")[1]);
        mBuilder.append(getResources().getString(R.string.available_time_per_min));
        mPreference.setTitle(mBuilder.toString());
        mPreference.setChecked(mParse.getBoolean(TimeLockConstant.IS_ENABLE));
        if (isRepeat(mParse)) {
            JSONArray days = mParse.getJSONArray(TimeLockConstant.REAPEAT_DAYS);
            String repeatDays = days.stream().map(x -> {
                String id = TimeLockConstant.DAY_RESOURCE_PREFIX + x.toString();
                int resId = getResources().getIdentifier(id, TimeLockConstant.RESOURCE_TYPE, TimeLockApplication.getContext().getPackageName());
                return getResources().getString(resId);
            }).collect(Collectors.joining("、"));
            mPreference.setSummary(repeatDays);
        }
        return mPreference;
    }

    /**
     * 刷新可用时间段UI
     */
    private void refreshTimePeriodView() {
        listAvailableTimePeriod.removeAll();
        String timePeriodJson = TimeLockApplication.dataCache.getTimePeriod();
        if (timePeriodJson != null) {
            JSONArray timePeriods = JSONArray.parseArray(timePeriodJson);
            JSONArray timePeriodTemp = JSONArray.parseArray(timePeriodJson);
            timePeriods.forEach(x -> {
                JSONObject mParse = (JSONObject) JSONObject.parse(x.toString());
                SwitchPreference mPreference = createTimePeriodPreference(mParse);
                mPreference.setOnPreferenceClickListener(p -> {
                    String key = mPreference.getKey();
                    timePeriods.forEach(v -> {
                        // 修改本地缓存数据
                        JSONObject json = (JSONObject) JSONObject.parse(x.toString());
                        if (key.equals(json.getString(TimeLockConstant.TIME_PERIOD_ID))) {
                            timePeriodTemp.remove(json);
                            json.put(TimeLockConstant.IS_ENABLE,mPreference.isChecked());
                            timePeriodTemp.add(json);
                            TimeLockApplication.dataCache.setTimePeriod(timePeriodTemp.toString());
                        }
                    });
                    return false;
                });
                listAvailableTimePeriod.addPreference(mPreference);
            });
        }
    }

    /**
     * 创建可用时间段SwitchPreference
     * @param mParse JSONObject 可用时间段的json数据
     * @return
     */
    private SwitchPreference createTimePeriodPreference (JSONObject mParse) {
        StringBuilder mBuilder = new StringBuilder();
        SwitchPreference mPreference = new SwitchPreference(TimeLockApplication.getContext());
        mPreference.setKey(mParse.getString(TimeLockConstant.TIME_PERIOD_ID));
        mBuilder.append(mParse.getString(TimeLockConstant.START_TIME));
        mBuilder.append(" - ");
        mBuilder.append(mParse.getString(TimeLockConstant.END_TIME));
        mPreference.setTitle(mBuilder.toString());
        mPreference.setChecked(mParse.getBoolean(TimeLockConstant.IS_ENABLE));
        if (isRepeat(mParse)) {
            JSONArray days = mParse.getJSONArray(TimeLockConstant.REAPEAT_DAYS);
            String repeatDays = days.stream().map(x -> {
                String id = TimeLockConstant.DAY_RESOURCE_PREFIX + x.toString();
                int resId = getResources().getIdentifier(id, TimeLockConstant.RESOURCE_TYPE, TimeLockApplication.getContext().getPackageName());
                return getResources().getString(resId);
            }).collect(Collectors.joining("、"));
            mPreference.setSummary(repeatDays);
        }
        return mPreference;
    }

    /**
     *  是否重复
     * @return true 重复 | false 不重复
     */
    private boolean isRepeat(JSONObject json) {
        return json.getBoolean(TimeLockConstant.IS_REPEAT);
    }

    /**
     * preference长按事件
     */
    private void longPressEvent() {
        ListView mListView = getListView();
        mListView.setOnItemLongClickListener((parent, view, position, id) -> {
            ListView listView = (ListView) parent;
            ListAdapter mAdapter = listView.getAdapter();
            Preference preference = (Preference) mAdapter.getItem(position);
            if (canDeletePreference(preference.getKey())) {
                AlertDialog.Builder builder = CommonDialog.createDialogBuilder(this, null, preference.getTitle().toString());
                builder.setNeutralButton(R.string.dialog_delete, (dialog, which) -> {
                    deletePeriodTimeDataFromKey(preference.getKey());
                });
                CommonDialog.createDialog(builder, true);
            }
            return true;
        });
    }

    /**
     * 当前Item是否可以删除
     * @param key
     * @return
     */
    private boolean canDeletePreference(String key) {
        return TimeLockApplication.dataCache.getTimePeriod().contains(key) || TimeLockApplication.dataCache.getTimeAvailable().contains(key);
    }

    /**
     * 根据key删除可用时间段
     * @param key
     */
    private void deletePeriodTimeDataFromKey(String key) {
        String mTimePeriod = TimeLockApplication.dataCache.getTimePeriod();
        JSONArray timePeriods = JSONArray.parseArray(mTimePeriod);
        boolean canBreak = false;
        for (int i = 0; i < timePeriods.size(); i++) {
            JSONObject mParse = (JSONObject) JSONObject.parse(timePeriods.get(i).toString());
            if (key.equals(mParse.get(TimeLockConstant.TIME_PERIOD_ID))) {
                timePeriods.remove(mParse);
                TimeLockApplication.dataCache.setTimePeriod(timePeriods.toString());
                refreshTimePeriodView();
                canBreak = true;
                break;
            }
        }
        if (!canBreak) {
            String mTimeCache = TimeLockApplication.dataCache.getTimeAvailable();
            JSONArray times = JSONArray.parseArray(mTimeCache);
            for (int i = 0; i < times.size(); i++) {
                JSONObject mParse = (JSONObject) JSONObject.parse(times.get(i).toString());
                if (key.equals(mParse.get(TimeLockConstant.TIME_AVAILABLE_ID))) {
                    times.remove(mParse);
                    TimeLockApplication.dataCache.setTimeAvailable(times.toString());
                    refreshTimeAvailableView();
                    break;
                }
            }
        }
    }

    @Override
    public void finish() {
        Intent mIntent = new Intent();
        setResult(RESULT_OK, mIntent);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 开启时间锁，进入时需要输入密码
     */
    private void lockTheScreen(boolean isOnCreate) {
        // 首次进入
        if (isOnCreate && TimeLockConstant.DEFAULT.equals(TimeLockApplication.dataCache.getTimeLockPassword())) {
             return;
        }
        // 已设置密码 开关未打开 打开应用
        if (isOnCreate && !timeLockSwitch.isChecked() && !TimeLockConstant.DEFAULT.equals(TimeLockApplication.dataCache.getTimeLockPassword())) {
            return;
        }
        View view = View.inflate(this, R.layout.dialog_entry_password, null);
        TimeLockDialogUtil mLockDialogUtil = new TimeLockDialogUtil(this, view, isOnCreate, false);
        mLockDialogUtil.setDialogListener(new TimeLockDialogUtil.DialogListener() {
            @Override
            public void onCancel(boolean isFinish) {
                if (isFinish) {
                    mLockDialogUtil.dismiss();
                    finish();
                } else {
                    mLockDialogUtil.dismiss();
                }
            }

            @Override
            public void onSuccess(boolean isDissmiss) {
                if (isDissmiss) {
                    mLockDialogUtil.dismiss();
                } else {
                    mLockDialogUtil.dismiss();
                    timeLockSwitch.setChecked(true);
                    switchTimeLockEnable();
                }
            }
        });
        mLockDialogUtil.setDialogTitle(R.string.time_lock_password);
        mLockDialogUtil.setDialogMessage(R.string.text_entry_password);
        mLockDialogUtil.setBackButtonEnable(true);
        mLockDialogUtil.show();
    }
}
