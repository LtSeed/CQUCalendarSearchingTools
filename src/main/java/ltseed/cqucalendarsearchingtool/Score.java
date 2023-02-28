package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Score {

    List<ScoreForOneTerm> terms = new ArrayList<>();

    class ScoreForOneTerm{
        List<ScoreForOneClass> clazz_list = new ArrayList<>();
        String year;
        double totCredit;
        String termName;

        public ScoreForOneTerm(JSONObject d) {
            termName = d.getString("term");
            totCredit = Double.parseDouble(d.getString("totalCredit"));
            year = d.getString("year");
            JSONArray c = d.getJSONArray("studentScoreDetailVOList");
            for (Object o : c) {
                JSONObject s = JSON.parseObject(o.toString());
                this.clazz_list.add(new ScoreForOneClass(s));
            }
        }
    }

    class ScoreForOneClass{
        final String attendance;
        final String courseCode;
        final String courseModuleType;
        final String courseName;
        final double credit;
        final double effectiveScore;
        final String examType;
        final String programType;

        ScoreForOneClass(JSONObject s) {
            this.attendance = s.getString("attendance");
            this.courseCode = s.getString("courseCode");
            this.courseModuleType = s.getString("courseModuleType");
            this.courseName = s.getString("courseName");
            this.credit = Double.parseDouble(s.getString("credit"));
            this.effectiveScore = s.getDouble("effectiveScore");
            this.examType = s.getString("examType");
            this.programType = s.getString("programType");
        }
    }
    Score(JSONObject score){
        JSONArray data = score.getJSONArray("data");
        for (Object datum : data) {
            JSONObject d = JSON.parseObject(datum.toString());
            this.terms.add(new ScoreForOneTerm(d));
        }
    }
}
