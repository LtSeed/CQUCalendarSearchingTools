package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.io.*;
import java.util.*;

import static ltseed.cqucalendarsearchingtool.Main.*;

public class Student {
    int id;
    List<Class> classes;

    public static Student requestStudent(int id){
        String url = "http://my.cqu.edu.cn/api/enrollment/timetable/student/" + id;
        Map<String,String> pr = new HashMap<>();
        JSONObject info = new JSONObject();
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
        if(info != null){
            JSONArray data = info.getJSONArray("data");
            return new Student(id,data);
        } else return null;
    }

    public static Student getStudent(int id){
        if(Save.SAVE_FOLDER.exists()){
            JSONObject info = null;
            if(new File(Save.SAVE_FOLDER,id+".json").exists()){
                info = JSON.parseObject(Save.read(id+".json"));
            }

            if(info != null){
                if (info.containsKey(String.valueOf(id))){
                    return new Student(id,info.getJSONArray(String.valueOf(id)));
                } else {
                    Student n = Student.requestStudent(id);
                    if (n != null) {
                        Save.addSave(n);
                        System.out.println("adding");
                    }
                    return n;
                }

            } else {
                Student n = Student.requestStudent(id);
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
            s = requestStudent(id);
        }
        assert s != null;
        Save.addSave(s);
        return s;
    }

    public List<Class> getClassesByWeek(int week){
        List<Class> list = new ArrayList<>();
        for (Class aClass : classes) {
            if(aClass.class_time.isWeek(week)) list.add(aClass);
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
        classes = new ArrayList<>();
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
