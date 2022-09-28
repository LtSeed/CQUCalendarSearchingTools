package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import static ltseed.cqucalendarsearchingtool.ClassTime.encodeTime;
import static ltseed.cqucalendarsearchingtool.EVERY_CLASS_TIME.getClassTime;

public class TimePeriod {

    public static final String TIME_JSON = "{\n" +
            "  \"classTime\": [\n" +
            "    {\n" +
            "        \"name\":\"1-2\",\n" +
            "        \"startTime\":\"0830\",\n" +
            "        \"endTime\":\"1010\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"3-4\",\n" +
            "        \"startTime\":\"1030\",\n" +
            "        \"endTime\":\"1210\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"1-3\",\n" +
            "        \"startTime\":\"0830\",\n" +
            "        \"endTime\":\"1115\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"1-4\",\n" +
            "        \"startTime\":\"0830\",\n" +
            "        \"endTime\":\"1210\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"1-6\",\n" +
            "        \"startTime\":\"0830\",\n" +
            "        \"endTime\":\"1510\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"5-6\",\n" +
            "        \"startTime\":\"1330\",\n" +
            "        \"endTime\":\"1510\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"5-7\",\n" +
            "        \"startTime\":\"1330\",\n" +
            "        \"endTime\":\"1605\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"6-7\",\n" +
            "        \"startTime\":\"1425\",\n" +
            "        \"endTime\":\"1605\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"8-9\",\n" +
            "        \"startTime\":\"1625\",\n" +
            "        \"endTime\":\"1805\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"6-9\",\n" +
            "        \"startTime\":\"1425\",\n" +
            "        \"endTime\":\"1805\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"10-11\",\n" +
            "        \"startTime\":\"1900\",\n" +
            "        \"endTime\":\"2040\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"10-12\",\n" +
            "        \"startTime\":\"1900\",\n" +
            "        \"endTime\":\"2135\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"10-13\",\n" +
            "        \"startTime\":\"1900\",\n" +
            "        \"endTime\":\"2230\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"5-8\",\n" +
            "        \"startTime\":\"1330\",\n" +
            "        \"endTime\":\"1710\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"5-12\",\n" +
            "        \"startTime\":\"1330\",\n" +
            "        \"endTime\":\"2135\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"12-13\",\n" +
            "        \"startTime\":\"2050\",\n" +
            "        \"endTime\":\"2230\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"9-12\",\n" +
            "        \"startTime\":\"1720\",\n" +
            "        \"endTime\":\"2135\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "\n";
    //public static final TimePeriod WHOLE_DAY = new TimePeriod(0,2400);

    public static JSONObject getTime(int id){
        JSONObject times = JSON.parseObject(TIME_JSON);
        return times.getJSONArray("classTime").getJSONObject(id);
    }


    final String time_code;

    public boolean isIn(TimePeriod compare_to){
        return isIn(compare_to.time_code);
    }

    /**
     * true when this time period is in the time period compare_to
     *  this:  1
     *  other: 11   return: true
     *  this:  01
     *  other: 001 return: false
     * */
    public boolean isIn(String code){
        boolean in = true;
        if(code.length()<time_code.length()) in = false;
        else for (int i = 0; i < time_code.length(); i++) {
            if (time_code.charAt(i) == '1' && code.charAt(i) != '1') {
                in = false;
                break;
            }
        }
        if(Main.DEBUG)
            System.out.println("Comparing "+ time_code +" to " + code+" " +in);

        return in;
    }

    TimePeriod(int id){
        int start,end;
        JSONObject time = getTime(id);
        start = Integer.parseInt(time.getString("startTime"));
        end = Integer.parseInt(time.getString("endTime"));
        time_code = encode_TimePeriod(start,end);
    }

    TimePeriod(int start, int end){
        time_code = encode_TimePeriod(start, end);
    }

    TimePeriod(String code){
        time_code = code;
    }

    private String encode_TimePeriod(int start, int end) {
        final String time_code;
        StringBuilder sb = new StringBuilder();
        int max = 0;
        boolean[] in = new boolean[13];
        for (int i = 1; i <= 13; i++) {
            if (getClassTime(i).tp.isIn(start, end)) {
                in[i] = true;
                max = i;
            }
        }
        for (int i = 0; i < max; i++) {
            sb.append(in[i]?'1':'0');
        }
        time_code = sb.toString();
        return time_code;
    }

    private boolean isIn(int start, int end) {
        int start_c=0,end_c=13;
        for (int i = 0; i < 13; i++) {
            EVERY_CLASS_TIME value = EVERY_CLASS_TIME.values()[i];
            if(value.in(start))start_c = start;
            if(value.in(end))end_c = end;
        }


        String code = encodeTime(start_c,end_c).toString();
        return isIn(code);
    }

    public int getStartTime() {
        return getClassTime(time_code.indexOf("1")+1).start;
    }
    public int getEndTime() {
        return getClassTime(time_code.lastIndexOf("1")+1).end;
    }
}
