package elite.com.timemanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elite.constant.TimeLockConstant;
import com.elite.model.TimePeriodModel;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testGenUUID() {
        Stream.iterate(0, x -> x+1).limit(100).forEach(x -> {
            UUID mUUID = UUID.randomUUID();
            System.out.println( mUUID.toString());
        });

    }
    @Test
    public void jsonTest() {
        List<String> mStringList = new ArrayList<>(10);
        mStringList.add("aaa");
        mStringList.add("bbb");
        mStringList.add("cc");
        mStringList.add("ddd");
        JSONArray periodTimeArray = new JSONArray();
        JSONObject json = new JSONObject();
        JSONObject periodTimeJson = new JSONObject();
        JSONObject timeJson = new JSONObject();
        periodTimeJson.put(TimeLockConstant.TIME_PERIOD_ID, UUID.randomUUID());
        periodTimeJson.put(TimeLockConstant.IS_ENABLE,true);
        periodTimeJson.put(TimeLockConstant.START_TIME, "15:00");
        periodTimeJson.put(TimeLockConstant.END_TIME, "16:00");
        periodTimeJson.put(TimeLockConstant.IS_REPEAT, "true");
        periodTimeJson.put(TimeLockConstant.REAPEAT_DAYS,mStringList);
        json.put("periodTime",periodTimeJson);
        timeJson.put(TimeLockConstant.TIME_PERIOD_ID, UUID.randomUUID());
        timeJson.put(TimeLockConstant.IS_ENABLE,true);
        timeJson.put(TimeLockConstant.START_TIME, "15:00");
        timeJson.put(TimeLockConstant.END_TIME, "16:00");
        timeJson.put(TimeLockConstant.IS_REPEAT, "true");
        timeJson.put(TimeLockConstant.REAPEAT_DAYS,mStringList);
        json.put("aviabledTime",timeJson);
        periodTimeArray.add(json);
        System.out.println(periodTimeArray.toString());
    }
    @Test
    public void testJsonParse() {
        String json = "[{\"repeatDays\":[\"sunday\",\"monday\",\"tuesday\",\"wednesday\",\"thursday\",\"friday\",\"saturday\"],\"time_period_id\":\"b6c7c0b3-2b9f-4911-bc3e-5a33a9edab04\",\"startTime\":\"10:00\",\"endTime\":\"13:00\",\"isRepeat\":false,\"isEnable\":false},{\"repeatDays\":[\"monday\",\"wednesday\",\"thursday\",\"friday\",\"saturday\"],\"time_period_id\":\"908fc289-4ec0-4cc8-ae06-0499a44d98eb\",\"startTime\":\"10:00\",\"endTime\":\"18:00\",\"isRepeat\":true,\"isEnable\":true},{\"repeatDays\":[\"sunday\",\"monday\",\"tuesday\",\"wednesday\",\"thursday\",\"friday\",\"saturday\"],\"time_period_id\":\"93ec017a-5e71-4d8b-a629-fe73f11fbdae\",\"startTime\":\"12:00\",\"endTime\":\"13:00\",\"isRepeat\":true,\"isEnable\":true}]";
        List<TimePeriodModel> timePeriodList = JSONArray.parseArray(json, TimePeriodModel.class);
        long mCount = timePeriodList.stream().filter(TimePeriodModel::isEnable).count();
        List<TimePeriodModel> mList = timePeriodList.stream()
                .filter(TimePeriodModel::isEnable)
                .filter(x -> isAvailableTime(x.getStartTime(), x.getEndTime()))
                .filter(x -> {
                    if (x.isRepeat()) {
                        LocalDate toDay = LocalDate.now();
                        DayOfWeek week = toDay.getDayOfWeek();
                        String nowDay = week.toString().toLowerCase();
                        Long mCollect = x.getRepeatDays().stream()
                                .filter(y -> y.equals(nowDay))
                                .collect(Collectors.counting());
                        return mCollect > 0;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());

        System.out.println(mList.toString());
        // 测试转换
        List<String> mCollect = mList.stream().map(TimePeriodModel::getTimePeriodId).collect(Collectors.toList());
        System.out.println(mCollect.toString());
        JSONArray timePeriods = JSONArray.parseArray(json);
        mCollect.forEach(v -> {
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
        System.out.println(timePeriods.toString());
        // 测试筛选出可用时间
        List<TimePeriodModel> timePeriodLists = JSONArray.parseArray(json, TimePeriodModel.class);
        List<TimePeriodModel> availbable = new ArrayList<>(timePeriodLists.size());
        timePeriodLists.forEach(x -> {
            if (!x.isEnable()) {
                availbable.add(x);
            } else if (isAvailableTime(x.getStartTime(), x.getEndTime())) {
                if (!x.isRepeat()) {
                    availbable.add(x);
                } else if (repeatDaysIncludeToDay(x)) {
                    availbable.add(x);
                }
            }
        });
        System.out.println(availbable.toString());

    }
    private boolean repeatDaysIncludeToDay (TimePeriodModel model) {
        LocalDate toDay = LocalDate.now();
        DayOfWeek week = toDay.getDayOfWeek();
        String nowDay = week.toString().toLowerCase();
        return model.getRepeatDays().stream()
                .filter(y -> Objects.equals(y, nowDay))
                .collect(Collectors.counting()) > 0;
    }
    private boolean isAvailableTime (String startTime, String endTime) {
        LocalTime now = LocalTime.now();
        LocalTime start = getTime(startTime);
        LocalTime end = getTime(endTime);
        return now.isBefore(end) && now.isAfter(start);
    }
    private LocalTime getTime(String time) {
        String[] startTimeArr = time.split(":");
        return LocalTime.of(Integer.valueOf(startTimeArr[0]), Integer.valueOf(startTimeArr[1]));
    }
    @Test
    public void testToJson() {
        ConcurrentHashMap<String, Object> restInfoMap = new ConcurrentHashMap<>(4);
        restInfoMap.put(TimeLockConstant.RestConstant.REST_ENABLE,true);
        restInfoMap.put(TimeLockConstant.RestConstant.REST_LEARN_LENGTH,15);
        restInfoMap.put(TimeLockConstant.RestConstant.REST_REST_LENGTH,30);
        System.out.println(JSON.toJSON(restInfoMap).toString());
    }

}