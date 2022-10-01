package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class ClassTime {
    public static Date FIRST_DAY = null;
    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            FIRST_DAY = sdf.parse("2022-08-29 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    final String week_code;
    TimePeriod time_period;
    int week_day;

    public ClassTime(int start_week, int end_week, int class_time, int weekday) {
        StringBuilder code_builder = encodeTime(start_week, end_week);
        week_code = code_builder.toString();
        this.week_day = weekday;
        time_period = new TimePeriod(class_time);
    }

    public ClassTime(int week, int i, int weekday) {
        week_code = "0".repeat(week-1) + '1';
        String time_code = "0".repeat(i-1) + '1';
        week_day = weekday;
        time_period = new TimePeriod(time_code);
    }

    public Date getStartTime(int week){
        int s = time_period.getStartTime();
        return getDate(week, s);
    }
    public Date getEndTime(int week){
        int s = time_period.getEndTime();
        return getDate(week, s);
    }

    private Date getDate(int week, int s) {
        long minute = (s%100) * 60 * 1000L;
        long clock = (s/100) * 60 * 60 * 1000L;
        long day = (week * 7L + week_day - 1) * ChronoUnit.DAYS.getDuration().toMillis();
        return new Date(FIRST_DAY.getTime() + minute + clock + day);
    }

    public static StringBuilder encodeTime(int start, int end) {
        StringBuilder code_builder = new StringBuilder();
        code_builder.append("0".repeat(Math.max(0, start - 1)));
        code_builder.append("1".repeat(Math.max(0, end - start + 1)));
        return code_builder;
    }

    public ClassTime(JSONObject classTimetable) {
        week_code = classTimetable.getString("teachingWeek");
        if (classTimetable.getString("weekDay") == null) {
            week_day = 0;
        } else week_day = Integer.parseInt(classTimetable.getString("weekDay"));
        time_period = new TimePeriod(classTimetable.getString("period"));
    }

    public void show() {
        System.out.println("weekday: " + week_day);
        System.out.println("week: " + week_code);
        System.out.println("time: " + time_period.time_code);
    }

    public boolean isWeek(int week) {
        boolean b;
        if(week_code.length()<=week) b = false;
        else b = week_code.charAt(week) == '1';
        if(Main.DEBUG) System.out.println("Comparing Week: "+week +" in "+
                week_code + " result:"+b);
        return b;
    }

}
