package ltseed.cqucalendarsearchingtool.cct;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@EqualsAndHashCode
public class StuInfoInWord {
    @ExcelProperty(value = "姓名", index = 0)
    String name;
    @ExcelProperty(value = "宿舍号", index = 1)
    String dpId;
    @ExcelProperty(value = "性别", index = 2)
    String gender;
    @ExcelProperty(value = "民族", index = 3)
    String minZu;
    @ExcelProperty(value = "籍贯", index = 4)
    String jiGuan;
    @ExcelIgnore
    Zm zzmm;
    @ExcelProperty(value = "出生年月日", index = 6)
    String birthday;
    @ExcelProperty(value = "健康状况", index = 7)
    String healthyState;
    @ExcelProperty(value = "血型", index = 8)
    String bloodType;
    @ExcelProperty(value = "手机号码", index = 9)
    String phone;
    @ExcelProperty(value = "QQ", index = 10)
    String qq;
    @ExcelProperty(value = "微信号码", index = 11)
    String wechat;
    @ExcelProperty(value = "邮箱", index = 12)
    String email;
    @ExcelProperty(value = "身份证号", index = 13)
    String ID;
    @ExcelProperty(value = "户口所在地", index = 14)
    String hukou_suozaidi;
    @ExcelProperty(value = "兴趣爱好", index = 15)
    String habit;
    @ExcelProperty(value = "特长", index = 16)
    String advantage;
    @ExcelProperty(value = "是否困难认定及等级", index = 17)
    String hard;
    @ExcelProperty(value = "邮编", index = 18)
    String zipCode;
    @ExcelProperty(value = "住址", index = 19)
    String address;
    @ExcelProperty(value = "是否父母离异/单亲/孤儿/其他情况", index = 20)
    String familyState;
    @ExcelIgnore
    List<FamilyMember> family;
    @ExcelIgnore
    List<StudyExp> studyExps;
    @ExcelProperty(value = "重要获奖/参加活动", index = 21)
    String award;
    @ExcelProperty(value = "自我评价", index = 22)
    String selfEvaluation;

    StuInfoInWord(String s){
        System.out.println(s);
        name = getString(s,"姓名");
        dpId = getString(s,"宿舍号");
        gender = getString(s,"性别");
        minZu = getString(s,"民族");
        jiGuan = getString(s,"籍贯");
        zzmm = Zm.getZm(Objects.requireNonNull(getString(s, "政治面貌")));
        birthday = getString(s,"出生年月日");
        healthyState = getString(s,"健康状况");
        bloodType = getString(s,"血型");
        phone = getString(s,"手机号码");
        qq = getString(s,"QQ");
        wechat = getString(s,"微信号码");
        email = getString(s,"邮箱");
        ID = getString(s,"身份证号");
        hukou_suozaidi = getString(s,"户口所在地");
        habit = getString(s,"兴趣爱好");
        advantage = getString(s,"特长");
        hard = getString(s,"是否困难认定及等级");
        zipCode = getString(s,"邮编");
        address = getString(s,"住址");
        familyState = getString(s,"是否父母离异");
        award = getString(s,"重要获奖/参加活动");
        selfEvaluation = getString(s,"自我评价");
//        family = new ArrayList<>();
//        String[] sp = s.split("%");
//        for (int i = 0; i < sp.length; i++) {
//            if(sp[i].equals("家庭成员")){
//                int j = i + 1;
//                while(sp[j].length()<=2){
//                    if(sp[j].equals("称谓")) {
//                        j += 6;
//                        continue;
//                    }
//                    StringBuilder sb = new StringBuilder();
//                    for (int i1 = 0; i1 < 6; i1++) {
//                        sb.append(sp[j+i1]).append("%");
//                    }
//                    family.add(new FamilyMember(sb.toString()));
//                    j += 6;
//                }
//            }
//        }
//        studyExps = new ArrayList<>();
//        for (int i = 0; i < sp.length; i++) {
//            if(sp[i].equals("学习履历")){
//                int j = i + 1;
//                while(sp[j].length()<=4) {
//                    if(sp[j].equals("学习经历")) {
//                        while(!sp[j].equals("小学")) j++;
//                        continue;
//                    }
//                    StringBuilder sb = new StringBuilder();
//                    for (int i1 = 0; i1 < 5; i1++) {
//                        sb.append(sp[j+i1]).append("%");
//                    }
//                    studyExps.add(new StudyExp(sb.toString()));
//                    j += 5;
//                }
//            }
//        }
    }


    public static String getString(String line, String s) {
        String[] split = line.split("%");
        System.out.println(Arrays.toString(split));
        for (int i = 0; i < split.length - 1; i++) {
            String s1 = split[i];
            if(s1.contains(s)||s.contains(s1)) {

                return split[i + 1];
            }

        }
        return null;
    }

    static class StudyExp {
        String degree;
        String dur;
        String unit;
        String work;
        StudyExp(String s){
            String[] split = s.split("%");
            int i = 0;
            this.degree = split[i++];
            this.dur = split[i++];
            this.unit = split[i++];
            this.work = split[i];
        }
    }

    static class FamilyMember {
        String call;
        String name;
        String age;
        String unit;
        Zm zzmm;
        String phone;
        FamilyMember(String s){
            String[] split = s.split("%");
            int i = 0;
            call=split[i++];
            name=split[i++];
            age=split[i++];
            unit=split[i++];
            zzmm=Zm.getZm(split[i++]);
            phone=split[i];
        }
    }

    enum Zm{
        QZ("群众"),GQTY("共青团员"),GCDY("共产党员");
        final String name;

        public static Zm getZm(String s){
            if(s.contains("党员"))return GCDY;
            else if(s.contains("团员"))return GQTY;
            else return QZ;
        }

        Zm(String s) {
            name = s;
        }
    }

}
