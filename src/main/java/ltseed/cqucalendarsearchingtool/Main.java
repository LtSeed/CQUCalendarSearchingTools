package ltseed.cqucalendarsearchingtool;

import net.fortuna.ical4j.model.component.VEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ltseed.cqucalendarsearchingtool.IcsFileParser.outputIcsFileFromClasses;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Main {
    public static final boolean DEBUG = false;
    public static final File FOLDER = new File("F:\\CQU-class2ics-main\\conf_classInfo");
    public static String Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NjQ2NTE5NjMsInVzZXJfbmFtZSI6IjMwMDExNjM5IiwiYXV0aG9yaXRpZXMiOlsi5a2m55SfJktSX1NNUyJdLCJqdGkiOiJjYTdiNzczZC1hMzhjLTQ0NGEtOTA5Yi1kZjFlYmU1ZjdhMzQiLCJjbGllbnRfaWQiOiJ0aW1ldGFibGUtcHJvZCIsInNjb3BlIjpbImFsbCJdfQ.vIHEdO7bNXWYpZuUaylVZETxDOZVTKUYME7xrYFUcrw";
    public static String Cookie = "FSSBBIl1UgzbN7NO=5Ffb8XvhTt4THlTe_.CYSq6xPvXAQPvYk.1WsX3AGRM2hPREXNWtj7_RzGCex.Z.MAR9TcsJJyP100ZNBdfMefq; Hm_lvt_fbbe8c393836a313e189554e91805a69=1662825907,1663225094,1663334331; SESSION=NDg1OGIyMTUtNWJmNS00OGM1LTkxOGYtMWVhYWRkMTFiMjk4; FSSBBIl1UgzbN7NP=53ufReKWgKL0qqqDk2reLaqE4vQoNu42PuTuacVOc2snKMpDQvQejGRWEFWKO7VFF3FBb.YxgiN3DuMlhuoKIiqQFMzsCVX4mzYmvkwRzuyjDQdS0Z.JCM2.g6augD3d9jbcr9wn1sqSSGX3JotlJ2AoZEbiBLiS8knEZdV5g59ak_7scOKRbg4P.Gd1VvMuR9C2eYOrigbPPHeT.D1EvVf2zdFUCbcETaBWhokMRg7Fgotr9cm7i_wI7i5mmyhwFH1HhJaIPImKg04s7Mq1abpFMZ.53JyA99ALySo9FTWUA";
    public static void main(String[] args) throws IOException {

        Scanner s = new Scanner(System.in);
        while (true){
            Student student = Student.requestStudent(s.nextInt());
            assert student != null;
            outputIcsFileFromClasses(student.classes, String.valueOf(student.id));
            System.out.println("done");
        }

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
