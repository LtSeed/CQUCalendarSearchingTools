package ltseed.cqucalendarsearchingtool;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ltseed.cqucalendarsearchingtool.Student.*;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Main {
    public static final boolean DEBUG = false;
    public static final File FOLDER = new File("F:\\CQU-class2ics-main\\conf_classInfo");
    public static String Authorization = "";
    public static String Cookie = "";
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        //saveStudentInfo();
        //countPeopleTime();
        //getAllStudent();
        //System.out.println(getJsonObjectFromMY("https://my.cqu.edu.cn/api/sam/score/student/score/", new HashMap<>(), new JSONObject()));
        //System.out.println(requestStudent(20212192).more_info);
        //IcsFileParser.outputIcsFileFromClasses(Objects.requireNonNull(requestStudentClasses("20212192")).classes,"1");
//        StudentWithMoreInfo m = Student.requestStudent(20212192);
//        System.out.println(m.more_info);
//        m.requestScore();
//        Score score = m.getScore();
//        System.out.println(score);
        countScore();
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

    private static void countScore(){
        Workbook w;
        try {
            File file = new File("D:\\nameList.xlsx");

            w = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Sheet sheet = w.getSheet("sheet0");
        System.out.println(sheet.getLastRowNum());
        int topRow = sheet.getFirstRowNum();
        System.out.println(topRow);
        Row t = sheet.getRow(topRow);
        List<String> title =  new ArrayList<>();
        for (int i = 0; i < t.getLastCellNum(); i++) {
            String string = t.getCell(i).getStringCellValue();
            if(string != null)
                title.add(string);
        }
        System.out.println(title);
        List<List<String>> data = new ArrayList<>();
        int r = title.indexOf("学号");
        Map<String, Integer> number = new HashMap<>();
        for (int i = sheet.getFirstRowNum() + 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            List<String> student = new ArrayList<>();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                String string = row.getCell(j).getStringCellValue();
                if(string != null)
                    student.add(string);
            }
            data.add(student);
            number.put(student.get(r),i);
        }
        List<String> xuehao = new ArrayList<>();

        for (List<String> datum : data) {
            xuehao.add(datum.get(r));
        }
        List<StudentWithMoreInfo> students = new ArrayList<>();
        for (String s : xuehao) {
            StudentWithMoreInfo stu;
            try {
                stu = Student.requestStudent(Integer.parseInt(s));
            } catch (Exception e) {
                System.out.println(s);
                continue;
            }
            if(stu == null){
                System.out.println(s);
                continue;
            }
            stu.requestScore();
            Score score = stu.getScore();
            double avengeScore = score.countAvengeScore("第一学期","2022-2023学年");
            System.out.println(stu.name+" "+avengeScore);
            try {
                if(avengeScore > 0)
                    students.add(stu);
            } catch (Exception ignored) {
            }
        }

        students.sort(Comparator.comparingDouble(o->-o.getScore().countAvengeScore("第一学期","2022-2023学年")));
        Sheet sheet1 = w.createSheet();
        for (int i = 0; i < students.size(); i++) {
            Row row = sheet1.createRow(i);
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            row.createCell(1).setCellValue(i);
            StudentWithMoreInfo studentWithMoreInfo = students.get(i);
            double v = studentWithMoreInfo.getScore().countAvengeScore("第一学期","2022-2023学年");
            double gpa = studentWithMoreInfo.getScore().countGPA("第一学期","2022-2023学年");
            sb.append(" ").append(v).append(" ").append(studentWithMoreInfo.name);
            sb.append(" ").append(gpa);sb.append(" ").append(studentWithMoreInfo.id);
            row.createCell(2).setCellValue(studentWithMoreInfo.id);
            row.createCell(3).setCellValue(studentWithMoreInfo.name);
            row.createCell(4).setCellValue(v);
            Integer rown = number.get(String.valueOf(studentWithMoreInfo.id));
            Row row1 = sheet.getRow(rown);
            short lastCellNum = sheet.getRow(rown).getLastCellNum();
            row1.createCell(lastCellNum + 1).setCellValue(v);
            row1.createCell(lastCellNum + 2).setCellValue(gpa);
            row.createCell(5).setCellValue(gpa);
            System.out.println(sb);
        }

        try {

            w.write(new FileOutputStream("D:\\nameList.xlsx"));
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计人员在该天的日程情况
     * */
    private static void countPeopleTime() throws IOException, InterruptedException {
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

    private static void saveWorkInGrade() throws IOException, InterruptedException {
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
        for (int i = 2022; i < 2023; i++) {

            for (int j = 0; j < 7000; j++) {
                int finalI = i;
                int finalJ = j;
                Thread thread = new Thread(() -> {
                    String id = finalI + StringUtils.leftPad(String.valueOf(finalJ), 4, '0');
                    try {
                        if(Integer.parseInt(id) % 50 == 0) System.out.println(id);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    Student.StudentWithMoreInfo student = null;
                    try {
                        student = Student.requestStudent(id);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (student == null) return;
                    System.out.println(student.id + " " + student.name);
                    list.add(student);
                });
                threads.add(thread);
                thread.start();
                if(threads.size() > 100)
                    do {
                        List<Thread> del = new ArrayList<>();
                        for (Thread thread1 : threads) {
                            if (!thread1.isAlive()) del.add(thread1);
                        }
                        for (Thread thread1 : del) {
                            threads.remove(thread1);
                        }
                        System.out.println("waiting " + threads.size());
                        Thread.sleep(1000);
                    } while (threads.size() >= 64);

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

            EasyExcel.write("F:\\名单"+i+".xlsx", Student.StudentWithMoreInfo.class).sheet(String.valueOf(i)).doWrite(list);
            list.clear();
        }
    }
}
