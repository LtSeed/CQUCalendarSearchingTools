package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Score {

    List<ScoreForOneTerm> terms = new ArrayList<>();

    public double countAvengeScore(String termName,String year) {
        double result = 0;
        double credit = 0;
        for (ScoreForOneTerm term : terms) {
            if((term.termName.equals(termName)||termName.equals(""))&&term.year.equals(year)){
                for (ScoreForOneClass scoreForOneClass : term.clazz_list) {
                    double score = scoreForOneClass.effectiveScore;
                    if(score != -1) {
                        result += scoreForOneClass.credit * score;
                        credit+=scoreForOneClass.credit;
                    }
                }
            }
//            if((!term.termName.equals(termName))&&term.year.equals(year)){
//                for (ScoreForOneClass scoreForOneClass : term.clazz_list) {
//                    if(scoreForOneClass.courseName.contains("金工实习")) {
//                        double score = scoreForOneClass.effectiveScore;
//                        if (score != -1) {
//                            result += scoreForOneClass.credit * score;
//                            credit += scoreForOneClass.credit;
//                        }
//                    }
//                }
//            }
        }
        return result / credit;
    }

    public double countGPA(String termName,String year) {
        double result = 0;
        double credit = 0;
        for (ScoreForOneTerm term : terms) {
            if(term.termName.equals(termName)&&term.year.equals(year)){
                for (ScoreForOneClass scoreForOneClass : term.clazz_list) {
                    double g;
                    g = (scoreForOneClass.effectiveScore-50)/10;
                    if(g <= 1) g = 1;
                    if(g >= 4) g = 4;
                    if(scoreForOneClass.effectiveScore != 0) {
                        result += scoreForOneClass.credit * g;
                        credit += scoreForOneClass.credit;
                    }
                }
            }
        }
        return result / credit;
    }

    public ScoreForOneTerm getTerm(String termName, String year) {
        for (ScoreForOneTerm term : terms) {
            if(term.termName.equals(termName)&&term.year.equals(year)){
                return term;
            }
        }
        return null;
    }

    static class ScoreForOneTerm{
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

    static class ScoreForOneClass{
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
            Double effectiveScore1 = s.getDouble("effectiveScore");
            if(effectiveScore1 != null)
                this.effectiveScore = effectiveScore1;
            else this.effectiveScore = -1;
            this.examType = s.getString("examType");
            this.programType = s.getString("programType");
        }
    }
    Score(JSONObject score){
        JSONArray data = score.getJSONObject("data").getJSONArray("sessionScores");
        for (Object datum : data) {
            JSONObject d = JSON.parseObject(datum.toString());
            this.terms.add(new ScoreForOneTerm(d));
        }
    }
}
