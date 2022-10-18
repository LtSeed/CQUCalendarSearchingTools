package ltseed.cqucalendarsearchingtool;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ltseed.cqucalendarsearchingtool.IcsFileParser.outputIcsFileFromClasses;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Main {
    public static final boolean DEBUG = false;
    public static final File FOLDER = new File("F:\\CQU-class2ics-main\\conf_classInfo");
    public static String Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NjYxMDUyODAsInVzZXJfbmFtZSI6IjMwMDExNjM5IiwiYXV0aG9yaXRpZXMiOlsi5a2m55SfJktSX1NNUyJdLCJqdGkiOiJmZGJiZjNiOS03NDA3LTQxMjctODgwOC0yMTVkMzY4MzJkMzQiLCJjbGllbnRfaWQiOiJlbnJvbGwtcHJvZCIsInNjb3BlIjpbImFsbCJdfQ.Tx0Rnmn40Th_kiLhVOeUqQXGlcK_bp_DLEDjCHTbXx0";
    public static String Cookie = "FSSBBIl1UgzbN7NO=5Ffb8XvhTt4THlTe_.CYSq6xPvXAQPvYk.1WsX3AGRM2hPREXNWtj7_RzGCex.Z.MAR9TcsJJyP100ZNBdfMefq; Hm_lvt_fbbe8c393836a313e189554e91805a69=1662825907,1663225094,1663334331; enable_FSSBBIl1UgzbN7N=true; FSSBBIl1UgzbN7NP=530_DgKJif6ZqqqDk_.4fhAox5ZgtRT.jEPhxQSoV9.Wj.IkHq8I1mOVRk8u1jXzh44r.oRZepP9ovdh_Pk2vtyEwRN9K9woOSAc4ETNGCExayWy4tlXjAbfAn1RBO8FRqDmA0WSZ53imitsa3aOUO7xnS_8TYX.75VglVGGaQMuPmwpn48v9JF0ojZvxl8jIRTbbo.TQ_fVV_uchRJCLu6IcwfJUksTV_IuUKyb5orhaENgKET21mvGgkAyAA8mx6VMTfRtmsXK9s..SBGE5Zn; SESSION=NDdhNjk1ZjItN2JhOC00OWIxLTk1NjItYTYzNzNhNGUyMTBh";
    public static void main(String[] args) throws IOException, InterruptedException {
        //saveStudentInfo();
        //countPeopleTime();
        //getAllStudent();
        Student.StudentWithMoreInfo pst = Student.requestStudent("20212002");
        assert pst != null;
        System.out.println(pst.more_info);
        //assert a != null;
        //a.showAllClass();
//        Scanner s = new Scanner(System.in);
//        while (true){
//            try {
//                Student student = Student.requestStudentClasses(s.nextInt());
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
        student = Student.requestStudentClasses(20214565);
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
    private static void countPeopleTime() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请分行输入所有人的学号或姓名，并以单独一行的#end结尾");
        List<Student> list = new ArrayList<>();
        while (true){
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("#end"))break;
            if(!line.contains("2")) line = Student.getStudentIdByName(line);
            if(line == null) continue;
            Student.StudentWithMoreInfo student = Student.requestStudent(Integer.parseInt(line));

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

    private static void saveWorkInGrand() throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);
        String t = in.nextLine();
        JSONObject info = new JSONObject();
        while (!t.equalsIgnoreCase("#end")){
            if(t.contains("2")){
                String[] s = t.split(" ");

            }
            t = in.nextLine();
        }
        File gWork = new File("F:\\gWork.json");
        //noinspection ResultOfMethodCallIgnored
        gWork.createNewFile();
        Save.write(info.toString(), gWork);
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
                    if(Student.students_info.containsKey(aClass.owner)) {
                        JSONObject stdnt_info = Student.students_info.getJSONObject(aClass.owner);
                        aData.info.append(StringUtils.rightPad(stdnt_info.getString("class"), 9, ' ')).append(' ');
                        aData.info.append("\t");
                        aData.info.append(StringUtils.rightPad(stdnt_info.getString("name"), 4, ' '));
                        aData.info.append("\t");
                        aData.info.append("(").append(StringUtils.rightPad(aClass.owner, 8, ' ')).append(")");
                        aData.info.append("\t");
                        aData.info.append(stdnt_info.getString("phone_number"));
                    } else {
                        aData.info.append(StringUtils.rightPad("", 9, ' ')).append(' ');
                        aData.info.append("\t");
                        aData.info.append(StringUtils.rightPad("", 4, ' '));
                        aData.info.append("\t");
                        aData.info.append("(").append(StringUtils.rightPad(aClass.owner, 8, ' ')).append(")");
                        aData.info.append("\t");
                        aData.info.append("           ");
                    }
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
    private static void getAllStudent() throws InterruptedException {
        List<Student.StudentWithMoreInfo> list = new ArrayList<>();
        AtomicInteger started = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        for (int i = 2021; i < 2022; i++) {
            for (int j = 1500; j < 2000; j++) {
                int finalI = i;
                int finalJ = j;
                Thread thread = new Thread(() -> {
                    String id = finalI + StringUtils.leftPad(String.valueOf(finalJ), 4, '0');
                    System.out.println(id);
                    Student.StudentWithMoreInfo student = Student.requestStudent(id);
                    if (student == null) return;
                    list.add(student);
                });
                threads.add(thread);
                thread.start();
                while (threads.size() > 24){
                    List<Thread> del = new ArrayList<>();
                    for (Thread thread1 : threads) {
                        if (!thread1.isAlive()) del.add(thread1);
                    }
                    for (Thread thread1 : del) {
                        threads.remove(thread1);
                    }
                }

            }
        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Thread thread : threads) {
                if(thread.isAlive()){
                    Thread.sleep(500);
                    flag = true;
                    break;
                }
            }
        }
        List<Student> l2 = new ArrayList<>();
        for (Student.StudentWithMoreInfo student : list) {
            for (Class aClass : student.classes) {
                if(aClass.teacher[0].equals("魏珊珊")&&aClass.class_time.time_period.time_code.equals("11")&&aClass.class_time.week_day == 3)
                    l2.add(student);
            }
        }
        EasyExcel.write("F:\\名单2.xlsx", Student.StudentWithMoreInfo.class).sheet("sheet1").doWrite(l2);
    }
}
