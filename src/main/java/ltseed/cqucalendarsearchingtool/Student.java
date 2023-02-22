package ltseed.cqucalendarsearchingtool;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static ltseed.cqucalendarsearchingtool.Main.*;

@Getter
public class Student {

    public static final JSONObject students_info;
    public static final JSONObject ban_wei_info;
    public static final JSONObject nian_ji_zhi_wu;
    static {
        File students_json = new File("F:\\students.json");
        students_info = null;
        ban_wei_info = null;
        nian_ji_zhi_wu = null;
    }

    private Score score;

    public Student(Student a) {
        this.id = a.id;
        this.classes = a.classes;
        this.score = a.score;
    }

    public boolean hasWork(){
        return ban_wei_info.containsKey(String.valueOf(id)) ||
                nian_ji_zhi_wu.containsKey(String.valueOf(id));
    }

    public String getWork(){
        if(nian_ji_zhi_wu.containsKey(String.valueOf(id)))
            return nian_ji_zhi_wu.getString(String.valueOf(id));
        if(ban_wei_info.containsKey(String.valueOf(id)))
            return ban_wei_info.getString(String.valueOf(id));
        return null;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class StudentWithMoreInfo extends Student{
        @ExcelIgnore
        String work;

        @ExcelIgnore
        JSONObject more_info;


        @ExcelProperty(value = "姓名", index = 5)
        String name;
        @ExcelProperty(value = "学院", index = 0)
        String deptName;
        @ExcelProperty(value = "学号", index = 4)
        String userId;
        @ExcelProperty(value = "行政班级", index = 2)
        String adminClass;
        @ExcelProperty(value = "专业", index = 3)
        String major;
        @ExcelProperty(value = "年级", index = 1)
        String grade;
        @ExcelProperty(value = "电话号码", index = 6)
        String phone;
        @ExcelProperty(value = "绩点", index = 7)
        String gpa;

        public boolean hasWork(){
            return work != null;
        }
        public String getWork(){
            return work;
        }

        StudentWithMoreInfo(File jsonfile) throws FileNotFoundException {
            super(jsonfile);
            if(super.hasWork()) work = super.getWork();
        }

        StudentWithMoreInfo(Student a){
            super(a);
        }

        public void getData() {
            /* {"deptName":"机械与运载工程学院",
        "finishCredit":"0.0",
        "major":"机械设计制造及其自动化（智能制造方向）",
        "phone":"13551586682",
        "totalCredit":"170.5",
        "grade":"2021",
        "name":"黄明宇",
        "gpa":"3.0381",
        "adminClass":"21机自（智造）03",
        "userId":"20213128"} */
            try {
                name = more_info.getString("name");
                deptName = more_info.getString("deptName");
                adminClass = more_info.getString("adminClass");
                major = more_info.getString("major");
                grade = more_info.getString("grade");
                phone = more_info.getString("phone");
                gpa = more_info.getString("gpa");
                userId = more_info.getString("userId");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ExcelIgnore
    final int id;
    @ExcelIgnore
    final List<Class> classes;

    public static String getStudentIdByName(String name){
        for (Map.Entry<String, Object> entry : students_info.entrySet()) {
            Object o = entry.getValue();
            assert o instanceof JSONObject;
            JSONObject info = (JSONObject) o;
            if(info.getString("name").equals(name)) return info.getString("id");
        }
        return null;
    }

    public static StudentWithMoreInfo requestStudent(int id) throws InterruptedException {
        return requestStudent(String.valueOf(id));
    }

    public static StudentWithMoreInfo requestStudent(String idOrName) throws InterruptedException {

        StudentWithMoreInfo student = new StudentWithMoreInfo(Student.requestStudentClasses(idOrName));
        if(student.classes == null) return null;
        if(student.classes.size() == 0) return null;
        String url = "https://my.cqu.edu.cn/api/workspace/stud/personal-info/";
        if(student.id == -1){
            return student;
        }
        url = url + student.id;
        if (DEBUG) {
            System.out.println(url);
        }
        Map<String,String> pr = new HashMap<>();
        JSONObject info = new JSONObject();
        info = getJsonObjectFromMY(url, pr, info);
        assert info != null;
        student.more_info = info.getJSONObject("data");
        url = "https://my.cqu.edu.cn/api/workspace/stud/self-check/";
        url = url + student.id;
        pr = new HashMap<>();
        info = new JSONObject();
        info = getJsonObjectFromMY(url, pr, info);
        if(student.more_info == null) return null;
        assert info != null;
        if(info.getJSONObject("data") != null)
            student.more_info.putAll(info.getJSONObject("data"));
        student.getData();
        //System.out.println(student.more_info);
        if (DEBUG) {
            System.out.println(idOrName+" GET DA ZE!");
        }
        return student;
    }

    public static JSONObject getJsonObjectFromMY(String url, Map<String, String> pr, JSONObject info) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            pr.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            pr.put("Host","my.cqu.edu.cn");
            pr.put("Referer","http://my.cqu.edu.cn/enroll/CourseStuSelectionList");
            pr.put("Accept","application/json, text/plain, */*");
            pr.put("Accept-Encoding","gzip, deflate");
            pr.put("Connection","keep-alive");
            pr.put("Authorization",Authorization);
            pr.put("Cookie",Cookie);
            info = JSON.parseObject(RequestTool.doGet(url, pr));
            if(DEBUG&&info!=null) System.out.println(info);
            if(info != null) break;
        }
        return info;
    }

    public static Student requestStudentClasses(int id) throws InterruptedException {
        return requestStudentClasses(String.valueOf(id));
    }

    public static Student requestStudentClasses(String idOrName) throws InterruptedException {
        int id = 0;
        String url = "http://my.cqu.edu.cn/api/enrollment/timetable/student/";
        if(idOrName.contains("2")){
            try {
                id = Integer.parseInt(idOrName);
                url = url + idOrName;
            } catch (NumberFormatException e) {
                url = url + idOrName;
            }
        } else {
            String studentIdByName = getStudentIdByName(idOrName);
            if(studentIdByName != null){
                id = Integer.parseInt(studentIdByName);
                url = url + id;
            } else {
                id = -1;
                url = url + idOrName;
            }
        }
        if(DEBUG) System.out.println(url);
        Map<String,String> pr = new HashMap<>();
        JSONObject info = new JSONObject();
        info = getJsonObjectFromMY(url, pr, info);
        if(info != null){
            JSONArray data = info.getJSONArray("data");
            return new Student(id,data);
        } else return null;
    }
    public void requestScore(){
        String url = "https://my.cqu.edu.cn/api/sam/statistic/dashboard/student/score-course/" + id;
        Map<String ,String > pr = new HashMap<>();
        pr.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        pr.put("Host","my.cqu.edu.cn");
        pr.put("Referer","https://my.cqu.edu.cn/sam/ResultInquiry");
        pr.put("Accept","application/json, text/plain, */*");
        pr.put("Accept-Encoding","gzip, deflate");
        pr.put("Connection","keep-alive");
        pr.put("Authorization",Authorization);
        pr.put("Cookie",Cookie);
        JSONObject info = JSON.parseObject(RequestTool.doGet(url, pr));
        if(DEBUG&&info!=null) System.out.println(info);
        if (info != null) {
            score = new Score(info);
        }
    }

    public static Student getStudent(int id) throws InterruptedException {
        return getStudent(String.valueOf(id));
    }

    public static Student getStudent(String id) throws InterruptedException {
        if(Save.SAVE_FOLDER.exists()){
            JSONObject info = null;
            if(new File(Save.SAVE_FOLDER,id+".json").exists()){
                info = JSON.parseObject(Save.read(id+".json"));
            }

            if(info != null){
                if (info.containsKey(String.valueOf(id))){
                    return new Student(Integer.parseInt(id),info.getJSONArray(String.valueOf(id)));
                } else {
                    Student n = Student.requestStudentClasses(Integer.parseInt(id));
                    if (n != null) {
                        Save.addSave(n);
                        System.out.println("adding");
                    }
                    return n;
                }

            } else {
                Student n = Student.requestStudentClasses(Integer.parseInt(id));
                if (n != null) {
                    Save.addSave(n);
                    System.out.println("adding");
                }
                return n;
            }

        }
        Student s;
        try {
            s = new Student(new File(FOLDER,id+".json"));
        } catch (FileNotFoundException e) {
            s = requestStudentClasses(Integer.parseInt(id));
        }
        assert s != null;
        Save.addSave(s);
        return s;
    }

    public List<Class.ClassOfAStudent> getClassesByWeek(int week,String owner){
        List<Class.ClassOfAStudent> list = new ArrayList<>();
        for (Class aClass : classes) {
            if(aClass.class_time.isWeek(week)) {
                Class.ClassOfAStudent data = new Class.ClassOfAStudent(aClass,owner);
                list.add(data);
            }
        }
        return list;
    }

    public void showAllClass(){
        System.out.println("-------------------------");
        System.out.println("id = " + id);
        System.out.println();
        for (Class aClass : classes) {
            aClass.show();
            System.out.println();
        }
        System.out.println("-------------------------");
    }

    public void decode(String json){
        JSONObject info = JSON.parseObject(json);
        JSONArray classes_info = info.getJSONArray("classInfo");

        for (int i = 0; i < classes_info.size(); i++) {
            /*
            * {"className": "工程力学I 郭开元",
            *  "week": {"startWeek": 1, "endWeek": 5},
            *  "weekday": 2, "classTime": 1,
            *  "classroom": "D1320"},
            */
            JSONObject class_info = classes_info.getJSONObject(i);
            String name = class_info.getString("className");
            JSONObject class_week = class_info.getJSONObject("week");
            int start_week = class_week.getInteger("startWeek");
            int end_week = class_week.getInteger("endWeek");
            int weekday = class_info.getInteger("weekday");
            int class_time = class_info.getInteger("classTime");
            String classroom = class_info.getString("classroom");
            Class c = new Class(name.split(" ")[0],
                    name.split(" ")[1],
                    start_week,
                    end_week,
                    weekday,
                    class_time,
                    classroom);
            boolean add = true;
            for (Class aClass : classes) {
                if(aClass.hashCode() == c.hashCode()) add = false;
            }
            if(add)
                classes.add(c);
        }
    }

    Student(File jsonfile) throws FileNotFoundException {
        if(jsonfile.getName().contains("."))
            id = Integer.parseInt(jsonfile.getName().split("\\.")[0]);
        else id = Integer.parseInt(jsonfile.getName());
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(jsonfile)),"gbk");
        while(scanner.hasNextLine())sb.append(scanner.nextLine());
        if(Main.DEBUG) System.out.println(sb);
        classes = new ArrayList<>();
        this.decode(sb.toString());
    }

    Student(int id,JSONArray data) {
        this.id = id;
        classes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            classes.add(new Class(data.getJSONObject(i)));
        }
    }


}
