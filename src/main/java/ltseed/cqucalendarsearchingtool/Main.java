package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSONObject;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.StringUtils;
import org.python.core.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ltseed.cqucalendarsearchingtool.IcsFileParser.outputIcsFileFromClasses;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Main {
    public static final boolean DEBUG = false;
    public static final File FOLDER = new File("F:\\CQU-class2ics-main\\conf_classInfo");
    public static String Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NjU1MDk4MTksInVzZXJfbmFtZSI6IjMwMDExNjM5IiwiYXV0aG9yaXRpZXMiOlsi5a2m55SfJktSX1NNUyJdLCJqdGkiOiI0MjlkMjE4NS04M2MwLTQ5M2ItOTkzNS0yNzM0MzdmNDA1NTUiLCJjbGllbnRfaWQiOiJlbnJvbGwtcHJvZCIsInNjb3BlIjpbImFsbCJdfQ.gJck_5noRbbUkQYdvSlXSrlFiZ8qJqdRokPr3lRkVVE";
    public static String Cookie = "FSSBBIl1UgzbN7NO=5Ffb8XvhTt4THlTe_.CYSq6xPvXAQPvYk.1WsX3AGRM2hPREXNWtj7_RzGCex.Z.MAR9TcsJJyP100ZNBdfMefq; Hm_lvt_fbbe8c393836a313e189554e91805a69=1662825907,1663225094,1663334331; enable_FSSBBIl1UgzbN7N=true; FSSBBIl1UgzbN7NP=530Ya6bJmQUaqqqDkS0kLqGcSwnwvpbuzTzEPg97rpeV8_6RBGu2RVM6xkAstBbJXx4x1u9gJi6fzUgw8lsF6mRPaJwiB28B_CBa2zADkkuQ6W9ei0f4iHKYIZaVqi461u5CZ8PK9XXoY_gHHpMEEklYegSGnmlVPqSoNOAt32E4B6SQCVkCydG3YKkNT4mmoYrl0vWK2zsj6rFQaOoeIN4HPTMsTT4e6w3f496d47VlTSX44ZKfu7pXfoJkdREK0Z5A_a7pKf9V3InJ20aR5hI; SESSION=ZjNkM2UwODItYzA1Ny00ZDIxLWJkOTktNDkxYWIwNmIyNmIw";
    public static void main(String[] args) throws IOException, InterruptedException {
        //saveStudentInfo();
        countPeopleTime();

//        Scanner s = new Scanner(System.in);
//        while (true){
//            try {
//                Student student = Student.requestStudent(s.nextInt());
//                assert student != null;
//                outputIcsFileFromClasses(student.classes, String.valueOf(student.id));
//                System.out.println("done");
//            } catch (IOException e) {
//                break;
//            }
//        }

/*
        assert student != null;
        List<Class> w = new ArrayList<>(student.classes);
        student = Student.requestStudent(20214565);
        assert student != null;
        w.addAll(student.classes);
        IcsFileParser.outputIcsFileFromClasses(w,"adding");
        try {
            for (int i = 20190000; i < 20197000; i++) {
                System.out.println(i);
                Student.getStudent(i);
                if(i%500==0)Save.saveStudentInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(100000000000000L);
        countPeopleTime();
*/


    }

    /**
     * 统计人员在该天的日程情况
     * */
    public static void countPeopleTime() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请分行输入所有人的学号或姓名，并以单独一行的#end结尾");
        List<Student> list = new ArrayList<>();
        while (true){
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("#end"))break;
            if(!line.contains("2")) line = Student.getStudentIdByName(line);
            if(line == null) continue;
            Student student = Student.requestStudent(Integer.parseInt(line));

            if(student!=null) {
                list.add(student);
                if(DEBUG)
                    student.showAllClass();
            } else System.out.println(line + " 未能识别！");
        }
        System.out.println("请输入查询周数，全部查询输入0");
        int week_quest = scanner.nextInt();
        if(week_quest == 0){
            List<VEvent> count = new ArrayList<>();
            for (int week = 1; week <= 18; week++) {
                for (int day = 1; day <= 7; day++) {
                    count.addAll(countDayCalendar(list, week, day));
                }
            }
            IcsFileParser.outputIcsFileFromEvent(count,"count");
            System.out.println("是否打开文件？（y/n）");
            String q = scanner.next();
            if(q.equalsIgnoreCase("y")){
                Runtime.getRuntime().exec("count.ics",null,IcsFileParser.ICS_FOLDER);
            }
            System.out.println("操作已经完成！");
            return;
        }
        System.out.println("请输入查询星期（1-7）");
        int weekday_quest = scanner.nextInt();
        countDayCalendar(list,week_quest,weekday_quest);
    }

    private static void saveStudentInfo() throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);
        String t = in.nextLine();
        JSONObject info = new JSONObject();
        while (!t.equalsIgnoreCase("#end")){
            if(t.contains("2")){
                String[] s = t.split(" ");
                String id = s[1];
                long phone_number = Long.parseLong(s[3]);
                int document_number = Integer.parseInt(s[5]);
                JSONObject student = new JSONObject();
                student.put("name",s[0]);
                student.put("id",id);
                student.put("ethnic",s[2]);
                student.put("phone_number",phone_number);
                student.put("document",s[4]);
                student.put("document_number",document_number);
                student.put("class",s[6]);
                info.put(id,student);
            }
            t = in.nextLine();
        }
        File students_json = new File("F:\\students.json");
        //noinspection ResultOfMethodCallIgnored
        students_json.createNewFile();
        Save.write(info.toString(),students_json);
        Thread.sleep(1000);
    }

    private static List<VEvent> countDayCalendar(List<Student> list, int week_quest, int weekday_quest) {
        List<Class.ClassOfAStudent> classesByWeek = new ArrayList<>();
        for (Student student : list) {
            classesByWeek.addAll(student.getClassesByWeek(week_quest, String.valueOf(student.id)));
        }
        List<Class.ClassOfAStudent> classesByDay = new ArrayList<>();
        for (Class.ClassOfAStudent aClass : classesByWeek) {
            if (aClass.class_time.week_day== weekday_quest) {
                classesByDay.add(aClass);
                if(DEBUG)
                    aClass.show();
            }
        }
        class Data {
            int num;
            final StringBuilder info = new StringBuilder();
            Data(){
                info.append(StringUtils.rightPad("年级班级",10,' '));
                info.append("\t");
                info.append(StringUtils.rightPad("姓名",4,' '));
                info.append("\t");
                info.append(StringUtils.rightPad("学号",8,' '));
                info.append("\t");
                info.append(StringUtils.rightPad("电话号码",7,' '));
                info.append("\t");
                info.append("课程").append("\n");
            }
        }
        Map<EVERY_CLASS_TIME,Data> data = new TreeMap<>();
        for (Class.ClassOfAStudent aClass : classesByDay) {
            for (EVERY_CLASS_TIME value : EVERY_CLASS_TIME.values()) {
                if(value.tp.isIn(aClass.class_time.time_period)){
                    Data aData = data.getOrDefault(value, new Data());
                    aData.num ++;
                    JSONObject stdnt_info = Student.students_info.getJSONObject(aClass.owner);
                    aData.info.append(StringUtils.rightPad(stdnt_info.getString("class"),9,' ')).append(' ');
                    aData.info.append("\t");
                    aData.info.append(StringUtils.rightPad(stdnt_info.getString("name"),4,' '));
                    aData.info.append("\t");
                    aData.info.append("(").append(StringUtils.rightPad(aClass.owner,8,' ')).append(")");
                    aData.info.append("\t");
                    aData.info.append(stdnt_info.getString("phone_number"));
                    aData.info.append("\t");
                    aData.info.append(aClass.class_name);
                    aData.info.append("\n");
                    data.put(value,aData);
                }
            }
        }
        List<VEvent> result = new ArrayList<>();
        for (EVERY_CLASS_TIME class_time : data.keySet()) {
            result.add(IcsFileParser.getClassEvent(data.get(class_time).num + " / " +list.size(),
                    class_time.toClassTime(week_quest,weekday_quest),week_quest,null,data.get(class_time).info.toString()));
            System.out.println(class_time +" 此时段有"+ data.get(class_time).num + "人有课！");
        }
        return result;
    }
}
