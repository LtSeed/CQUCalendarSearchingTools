package ltseed.cqucalendarsearchingtool;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.fortuna.ical4j.model.component.VEvent;
import org.python.antlr.ast.Str;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ltseed.cqucalendarsearchingtool.IcsFileParser.getClassEvent;
import static ltseed.cqucalendarsearchingtool.IcsFileParser.getDayEvent;

public class Class {
    ClassTime class_time;
    String classroom;
    String class_name;
    String[] teacher;
    JSONObject origen_info;

    public static class ClassOfAStudent extends Class{
        String owner;

        public ClassOfAStudent(String s, String s1, int start_week, int end_week, int weekday, int class_time, String classroom) {
            super(s, s1, start_week, end_week, weekday, class_time, classroom);
        }
        public ClassOfAStudent(JSONObject jsonObject) {
            super(jsonObject);
        }

        public ClassOfAStudent(Class aClass, String owner) {
            super(aClass);
            this.owner = owner;
        }
    }
    public List<VEvent> exportToIcs(){
        List<VEvent> result = new ArrayList<>();
        for (int i = 0; i < class_time.week_code.length(); i++) {
            if(class_time.week_code.charAt(i) == '1')
                if(class_time.week_day == 0) result.add(getDayEvent(class_name, i, 1, null, getClassDescription()));
                else result.add(getClassEvent(class_name, class_time, i, classroom, getClassDescription()));
        }
        return result;
    }

    public Class(Class copy) {
        this.class_time = copy.class_time;
        this.classroom = copy.classroom;
        this.class_name = copy.class_name;
        this.teacher = copy.teacher;
        this.origen_info = copy.origen_info;
    }

    private String getClassDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("教师：");
        sb.append(teacher[0]);
        if(teacher.length>1){
            for (int i1 = 1; i1 < teacher.length; i1++) {
                sb.append(", ").append(teacher[i1]);
            }
        }
        sb.append('\n');
        sb.append("课程代码：").append(origen_info.getString("classNbr")).append("\n");
        return sb.toString();
    }

    public Class(String s, String s1, int start_week, int end_week, int weekday, int class_time, String classroom) {
        class_name = s;
        teacher = new String[1];
        teacher[0] = s1;
        this.classroom = classroom;
        this.class_time = new ClassTime(start_week,end_week,class_time,weekday);
    }

    public Class(JSONObject jsonObject) {
        origen_info = jsonObject;
        classroom = jsonObject.getString("roomName");
        class_name = jsonObject.getString("courseName");
        JSONArray teachers = jsonObject.getJSONArray("classTimetableInstrVOList");
        int teacher_amount = teachers.size();
        teacher = new String[teacher_amount];
        for (int i = 0; i < teacher_amount; i++) {
            teacher[i] = teachers.getJSONObject(i).getString("instructorName");
        }
        class_time = new ClassTime(jsonObject);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void show() {
        System.out.println("class_name: " + class_name);
        System.out.println("classroom: " + classroom);
        System.out.println("teacher: " + Arrays.toString(teacher));
        class_time.show();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Class)) return false;
        Class aClass = (Class) o;
        return Objects.equals(class_time, aClass.class_time) && Objects.equals(classroom, aClass.classroom) && Objects.equals(class_name, aClass.class_name) && Arrays.equals(teacher, aClass.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(class_time, classroom, class_name, Arrays.hashCode(teacher));
    }
}
