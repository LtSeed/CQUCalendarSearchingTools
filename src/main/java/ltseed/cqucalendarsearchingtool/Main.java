package ltseed.cqucalendarsearchingtool;

import net.fortuna.ical4j.model.component.VEvent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Main {
    public static final boolean DEBUG = true;
    public static final File FOLDER = new File("F:\\CQU-class2ics-main\\conf_classInfo");
    public static String Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NjQzOTAxMDEsInVzZXJfbmFtZSI6IjMwMDExNjM5IiwiYXV0aG9yaXRpZXMiOlsi5a2m55SfJktSX1NNUyJdLCJqdGkiOiJhYTNlOTgyMC03Y2ViLTQyOGItOTg4Zi1mNGJjYzVjYzFkMDQiLCJjbGllbnRfaWQiOiJlbnJvbGwtcHJvZCIsInNjb3BlIjpbImFsbCJdfQ.UclVuSRI-BG1uPU0ePsGOeXXULRn07JegvrgxA1B9J4";
    public static String Cookie = "FSSBBIl1UgzbN7NO=5Ffb8XvhTt4THlTe_.CYSq6xPvXAQPvYk.1WsX3AGRM2hPREXNWtj7_RzGCex.Z.MAR9TcsJJyP100ZNBdfMefq; Hm_lvt_fbbe8c393836a313e189554e91805a69=1662825907,1663225094,1663334331; enable_FSSBBIl1UgzbN7N=true; FSSBBIl1UgzbN7NP=53upFdCW0KyAqqqDkfEJCWGIvWQGsWFSUjddKv9r3pvl2rlM4HfQZWXCWIgqDcKFroOGhcza6mDOCDSZdJD5uys_4C0wsbGzv09iM4dJ3LVdxBRjPQ.HHdlPUFrdiQyMqrZQdIuGE__s1sosadtsmqv0b4.3vcCgH.0oUVS_UeQVAokzZVrMlClgJ_pSYYns2DYDOOc66QrfAQLoyH5A.xBBhl6h8YZSc.1AVPNkq.A4jDLRobKLB7UD0t.kCV2XKEFElZDzhnGWvYuXl.uVdM0; SESSION=NWY3YWYwNGEtODNiYS00MDM2LTk5NjQtNGVmYzdjYjEwNWQ4";
    public static void main(String[] args) throws IOException, URISyntaxException {

//        Student student = Student.requestStudent(20212192);
//        assert student != null;
//        List<Class> w = new ArrayList<>(student.classes);
//        student = Student.requestStudent(20214565);
//        assert student != null;
//        w.addAll(student.classes);
//        IcsFileParser.outputIcsFileFromClasses(w,"adding");


//        try {
//            for (int i = 20190000; i < 20197000; i++) {
//                System.out.println(i);
//                Student.getStudent(i);
//                if(i%500==0)Save.saveStudentInfo();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Thread.sleep(100000000000000L);

        countPeopleTime();
    }

    /**
     * 统计人员在该天的日程情况
     * */
    public static void countPeopleTime() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请分行输入所有人的学号，并以单独一行的#end结尾");
        List<Student> list = new ArrayList<>();
        while (true){
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("#end"))break;
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
            return;
        }
        System.out.println("请输入查询星期（1-7）");
        int weekday_quest = scanner.nextInt();
        countDayCalendar(list,week_quest,weekday_quest);
    }

    private static List<VEvent> countDayCalendar(List<Student> list, int week_quest, int weekday_quest) {
        List<Class> classesByWeek = new ArrayList<>();
        for (Student student : list) {
            classesByWeek.addAll(student.getClassesByWeek(week_quest));
        }
        List<Class> classesByDay = new ArrayList<>();
        for (Class aClass : classesByWeek) {
            if (aClass.class_time.week_day== weekday_quest) {
                classesByDay.add(aClass);
                if(DEBUG)
                    aClass.show();
            }
        }
        Map<EVERY_CLASS_TIME,Integer> data = new TreeMap<>();
        for (Class aClass : classesByDay) {
            for (EVERY_CLASS_TIME value : EVERY_CLASS_TIME.values()) {
                if(value.tp.isIn(aClass.class_time.time_period)){
                    Integer integer = data.getOrDefault(value, 0);
                    data.put(value,integer+1);
                }
            }
        }
        List<VEvent> result = new ArrayList<>();
        System.out.println("\n\nweek: "+ week_quest+", week_day: "+ weekday_quest);
        for (EVERY_CLASS_TIME class_time : data.keySet()) {
            result.add(IcsFileParser.getClassEvent("此时段有"+ data.get(class_time) + "人有课！",
                    class_time.toClassTime(week_quest,weekday_quest),week_quest,null,null));
            System.out.println(class_time +" 此时段有"+ data.get(class_time) + "人有课！");
        }
        return result;
    }
}
