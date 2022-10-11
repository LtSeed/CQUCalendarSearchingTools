package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.List;

public class Save {
    public static final JSONObject saving_map = new JSONObject();
    public static final File SAVE_FOLDER = new File("F:\\save");
    public static void saveStudentInfo() throws IOException {
        saveStudentInfo(null);
    }
    public static void saveStudentInfo(List<Student> list) throws IOException {
        if(!SAVE_FOLDER.exists()) //noinspection ResultOfMethodCallIgnored
            SAVE_FOLDER.createNewFile();
        if(list != null)
            for (Student student : list) {
                addSave(student);
            }
        String s = saving_map.toJSONString();
        write(s,"save.txt");
    }

    public static void write(String string, String file){
        new Thread(()->{
            try {
                File file1 = new File(SAVE_FOLDER, file);
                //noinspection ResultOfMethodCallIgnored
                file1.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
                bw.write(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void write(String string, File file){
        new Thread(()->{
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(string);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String read(File file){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            sb.append(bf.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static String read(String file){
        return read(new File(Save.SAVE_FOLDER,file));
    }

    public static void addSave(Student n) {
        JSONObject info = new JSONObject();
        JSONArray class_array = new JSONArray();
        n.classes.forEach(aClass -> class_array.add(aClass.origen_info));
        info.put(String.valueOf(n.id),class_array);
        write(info.toString(),n.id+".json");
    }
}
