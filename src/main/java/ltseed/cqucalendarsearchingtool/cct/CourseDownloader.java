package ltseed.cqucalendarsearchingtool.cct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ltseed.cqucalendarsearchingtool.cct.SimpleHttpServer.Class_FOLDER;
import static ltseed.cqucalendarsearchingtool.cct.Student.requestStudentClasses;

public class CourseDownloader {

    private static final int THREAD_COUNT = 5; // 线程池的大小

    public static void downloadAndSaveCourses() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int year = 2018; year <= 2023; year++) {
            for (int number = 0; number <= 9999; number++) {
                String studentId = String.format("%d%04d", year, number);
                executor.submit(() -> {
                    try {
                        Student student = requestStudentClasses(studentId);
                        if (student != null && !student.classes.isEmpty()) {
                            saveToIcsAndTxt(student, studentId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing student ID " + studentId + ": " + e.getMessage());
                    }
                });
            }
        }

        executor.shutdown(); // 关闭线程池
        try {
            // 等待所有任务完成，或者超时
            if (!executor.awaitTermination(3, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void saveToIcsAndTxt(Student student, String studentId) throws IOException, IOException {
        // 保存到ICS
        IcsFileParser.outputIcsFileFromClasses(student.classes, studentId);
        // 保存到TXT
        String studentData = student.toString();
        File txtFile = new File(Class_FOLDER, studentId + ".txt");
        Files.writeString(txtFile.toPath(), studentData);
        System.out.println("Saved data for student ID " + studentId);
    }
}
