package ltseed.cqucalendarsearchingtool.cct;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static ltseed.cqucalendarsearchingtool.cct.Main.*;

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

    @Override
    public String toString() {
        return "{" +
                "'id':'" + id + '\'' +
                ", 'classes':" + classes +
                '}';
    }

    public Student(Student a) {
        this.id = a.id;
        this.classes = a.classes;
    }

    public Student() {}

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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StudentWithMoreInfo extends Student{

        StudentWithMoreInfo(){
            super();
        }
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
        @ExcelProperty(value = "照片", index = 8)
        private String studentImageUrl;
        @ExcelIgnore
        @JsonIgnore
        private BufferedImage image = null;

        {
            try {
                image = RequestTool.readImage(studentImageUrl);
            } catch (IOException ignored) {
            }
        }

        @ExcelProperty(value = "姓名拼音", index = 9)
        private String studentNamePy;
        @ExcelProperty(value = "婚姻状况", index = 10)
        private String maritalStatus;
        @ExcelProperty(value = "性别", index = 11)
        private String gender;
        @ExcelProperty(value = "生日", index = 12)
        private String birthday;
        @ExcelProperty(value = "政治面貌", index = 13)
        private String politicalStatus;
        @ExcelProperty(value = "民族", index = 14)
        private String nationality;
        @ExcelProperty(value = "证件类型", index = 15)
        private String idType;
        @ExcelProperty(value = "证件号码", index = 16)
        private String idNumber;
        @ExcelProperty(value = "邮箱", index = 17)
        private String email;
        @ExcelProperty(value = "统一认证号", index = 18)
        private String authId;
        @ExcelProperty(value = "家庭电话", index = 19)
        private String familyPhone;
        @ExcelProperty(value = "家庭邮政编码", index = 20)
        private String homeAddressZipCode;
        @ExcelProperty(value = "家庭住址", index = 21)
        private String homeAddress;
        @ExcelProperty(value = "学位", index = 22)
        private String degree;

        @ExcelProperty(value = "学制", index = 23)
        private String learningForm;

        @ExcelProperty(value = "学籍情况", index = 24)
        private String stuEnrollmentStatusName;

        @ExcelProperty(value = "入学时间", index = 25)
        private String enrollmentTime;
        @ExcelProperty(value = "生源地", index = 26)
        private String stuSourceRegion;
        @ExcelProperty(value = "生源单位", index = 27)
        private String stuSourceUnit;
        @ExcelProperty(value = "现校区", index = 28)
        private String campusName;
        @ExcelProperty(value = "获取学籍时间", index = 29)
        private String obtainSchoolRollTime;
        @ExcelProperty(value = "宿舍", index = 30)
        private String dormitoryAddress;
        @ExcelProperty(value = "特长", index = 31)
        private String strongPoint;


        @ExcelProperty(value = "辅修专业", index = 32)
        private String minorMajorName;

        @ExcelProperty(value = "辅修专业所在学院", index = 33)
        private String minorDeptName;

        @ExcelProperty(value = "专业排名", index = 34)
        private int majorRanking;

        @ExcelProperty(value = "大班id", index = 36)
        private String largeClassId;
        @ExcelProperty(value = "大班名", index = 37)
        private String largeClassName;

        @ExcelIgnore
        private List<Label> studentLabels;
        @ExcelIgnore
        private List<Object> studentPunish;
        @ExcelIgnore
        private List<AcademicAlert> studentAcademicAlert;
        @ExcelIgnore
        private List<CommonApply> studentCommonApply;
        @ExcelIgnore
        private List<Object> studentCommonApplyHistory;
        @ExcelProperty(value = "QQ", index = 35)
        private String qq;
        @ExcelIgnore
        private Score score;


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

        @Getter
        @Setter
        @NoArgsConstructor
        @Data
        static class Label {
            private String id = null;
            private String name = null;
            private String studentId = null;
            private String description = null;
            private String activeFlag = null;


            Label(String id, String name, String studentId, String description,String activeFlag) {
                this.id = id;
                this.name = name;
                this.studentId = studentId;
                this.description = description;
                this.activeFlag = activeFlag;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @Data
        static class AcademicAlert {
            private String sessionYear;
            private String sessionTerm;
            private String acquireCredit;
            private String warnOrder;

            AcademicAlert(String sessionYear, String sessionTerm, String acquireCredit, String warnOrder) {
                this.sessionYear = sessionYear;
                this.sessionTerm = sessionTerm;
                this.acquireCredit = acquireCredit;
                this.warnOrder = warnOrder;
            }

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @Data
        static class CommonApply {
            private String applyId;
            private String applyType;
            private String applyTime;
            private String documentNum;

            CommonApply(String applyId, String applyType, String applyTime, String documentNum) {
                this.applyId = applyId;
                this.applyType = applyType;
                this.applyTime = applyTime;
                this.documentNum = documentNum;
            }

            // getter 和 setter 方法
        }
        public void requestScore(int stack){
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
            JSONObject info = JSON.parseObject(RequestTool.doGet(url, pr,0));

            if (info != null && info.getJSONObject("data") != null) {
                score = new Score(info);
            } else {
                if(info!=null&&stack == 20) {
                    String msg = name + " " + info.get("msg") + "------------";
                    strings.add(msg);
                    System.out.println(msg);
                    score = null;
                } else {
                    requestScore(++stack);
                }
            }
        }
    }

    @ExcelIgnore
    String id;
    @ExcelIgnore
    List<Class> classes = new ArrayList<>();

    public static String getStudentIdByName(String name){
        for (Map.Entry<String, Object> entry : students_info.entrySet()) {
            Object o = entry.getValue();
            assert o instanceof JSONObject;
            JSONObject info = (JSONObject) o;
            if(info.getString("name").equals(name)) return info.getString("id");
        }
        return null;
    }


    public static StudentWithMoreInfo requestStudent(String idOrName,int stack) throws InterruptedException {
        if(stack == 20) return null;
        StudentWithMoreInfo student = new StudentWithMoreInfo(Student.requestStudentClasses(idOrName));
        if(student.classes == null) {
            System.out.println("N1");
            return requestStudent(idOrName, ++stack);
        }
        if(student.classes.size() == 0) {
            System.out.println("N2");
            return requestStudent(idOrName, ++stack);
        }

        if(Objects.equals(student.id, "-1")){
            return student;
        }

        String url;
        JSONObject info;
        student.more_info = new JSONObject();

        url = "https://my.cqu.edu.cn/api/workspace/stud/personal-info/";
        info = getJsonObjectFromJY(student, url);

        try {
            if (info == null) {
                if(DEBUG) System.out.println("N3");
            } else student.more_info.putAll(info.getJSONObject("data"));
        } catch (NullPointerException ignore) {
        }

        url = "https://my.cqu.edu.cn/api/workspace/stud/self-check/";
        info = getJsonObjectFromJY(student, url);

        try {
            if((info != null ? info.getJSONObject("data") : null) != null) {
                student.more_info.putAll(info.getJSONObject("data"));
                student.getData();
            }
        } catch (NullPointerException ignored) {
        }

        url = "https://my.cqu.edu.cn/api/shunt/student/management/enrollment/";
        info = getJsonObjectFromJY(student, url);



        try {
            if((info != null ? info.getJSONObject("data") : null) != null)
                student.more_info.putAll(info.getJSONObject("data"));
        } catch (NullPointerException ignored) {
            return null;
        }

        if(student.more_info.containsKey("msg")&&student.more_info.getString("msg").equalsIgnoreCase("操作失败")){
            student.more_info.remove("msg");
            return requestStudent(idOrName,++stack);
        }

        if (DEBUG) {
            //System.out.println(student.more_info);
            System.out.println(idOrName+" GET DA ZE!");
        }
        ObjectMapper om = new ObjectMapper();
        StudentWithMoreInfo studentWithMoreInfo = null;
        try {
            studentWithMoreInfo = om.readValue(student.more_info.toJSONString(), StudentWithMoreInfo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (studentWithMoreInfo != null) {
            studentWithMoreInfo.more_info = student.more_info;
            studentWithMoreInfo.classes = student.classes;
            studentWithMoreInfo.id = student.id;
        } else studentWithMoreInfo = student;
        return studentWithMoreInfo;
    }

    private static JSONObject getJsonObjectFromJY(StudentWithMoreInfo student, String url) throws InterruptedException {
        Map<String, String> pr;
        JSONObject info;
        url = url + student.id;
        pr = new HashMap<>();
        info = new JSONObject();
        info = getJsonObjectFromMY(url, pr, info);
        if(student.more_info == null) return null;
        assert info != null;
        if(DEBUG) System.out.println(info);
        return info;
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
            info = JSON.parseObject(RequestTool.doGet(url, pr,0));
            if(DEBUG&&info!=null) System.out.println(info);
            if(info != null) break;
        }
        return info;
    }

    public static Student requestStudentClasses(String idOrName) throws InterruptedException {
        String id = idOrName;
        String url = "http://my.cqu.edu.cn/api/enrollment/timetable/student/";

        try {
            Integer.parseInt(idOrName);
            id = idOrName;
            url = url + idOrName;
        } catch (NumberFormatException e) {
            url = url + idOrName;
        }

        if(DEBUG) System.out.println(url);
        Map<String,String> pr = new HashMap<>();
        JSONObject info = new JSONObject();
        info = getJsonObjectFromMY(url, pr, info);
        if(info != null){
            JSONArray data = info.getJSONArray("data");
            return new Student(id, data);
        } else {
            if(DEBUG) System.out.println(id + " Student null");
            return null;
        }
    }


    public static Student getStudent(String id) throws InterruptedException {
        if(Save.SAVE_FOLDER.exists()){
            JSONObject info = null;
            if(new File(Save.SAVE_FOLDER,id+".json").exists()){
                info = JSON.parseObject(Save.read(id+".json"));
            }

            if(info != null){
                if (info.containsKey(String.valueOf(id))){
                    return new Student(id,info.getJSONArray(String.valueOf(id)));
                } else {
                    Student n = Student.requestStudentClasses(id);
                    if (n != null) {
                        Save.addSave(n);
                        System.out.println("adding");
                    }
                    return n;
                }

            } else {
                Student n = Student.requestStudentClasses(id);
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
            s = requestStudentClasses(id);
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
            id = jsonfile.getName().split("\\.")[0];
        else id = jsonfile.getName();
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(jsonfile)),"gbk");
        while(scanner.hasNextLine())sb.append(scanner.nextLine());
        if(Main.DEBUG) System.out.println(sb);
        classes = new ArrayList<>();
        this.decode(sb.toString());
    }

    Student(String id,JSONArray data) {
        this.id = id;
        classes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Class e = new Class(data.getJSONObject(i));
            if(DEBUG) e.show();
            classes.add(e);
        }
        if(DEBUG) System.out.println("class size = " + data.size());
    }


}
