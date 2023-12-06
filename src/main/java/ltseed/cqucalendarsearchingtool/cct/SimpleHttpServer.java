package ltseed.cqucalendarsearchingtool.cct;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import static ltseed.cqucalendarsearchingtool.cct.Student.requestStudentClasses;

public class SimpleHttpServer {
    public static final File ICS_FOLDER = new File("E:\\SERVER\\ics-out");
    public static final File Class_FOLDER = new File("E:\\SERVER\\class");
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {

        if(!ICS_FOLDER.exists()){
            ICS_FOLDER.mkdirs();
        }
        if(!Class_FOLDER.exists()){
            Class_FOLDER.mkdirs();
        }
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        Thread thread = new CheckLoginThread();
        thread.start();

        server.createContext("/student", exchange -> {
            try {
                handleRequest(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // 新增的 /file 路径
        server.createContext("/file", exchange -> {
            try {
                handleFileRequest(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is listening on port " + port);
    }

    private static void handleFileRequest(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        System.out.println(path);
        String id = path.replace("/file/", "").replace(".ics", "");

        if (id.isEmpty()) {
            System.out.println("error: " + id + " Invalid request");
            sendResponse(exchange, "Invalid request".getBytes(StandardCharsets.UTF_8), 400);
            return;
        }
        try {
            File file = new File(IcsFileParser.ICS_FOLDER, id + ".ics");
            if (!file.exists()) {
                IcsFileParser.outputIcsFileFromClasses(Objects.requireNonNull(requestStudentClasses(id)).classes, id);
                if (!file.exists()) {
                    System.out.println("error: " + id + " File not found");
                    sendResponse(exchange, "File not found".getBytes(StandardCharsets.UTF_8), 404);
                    return;
                }
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/calendar");
            exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + id + ".ics\"");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");

            sendResponse(exchange, fileContent, 200);
        } catch (Exception e) {
            System.out.println("error: " + id + " " + e.getMessage());
            sendResponse(exchange, e.getMessage().getBytes(StandardCharsets.UTF_8), 400);
        }
    }
    private static void sendResponse(HttpExchange exchange, byte[] responseBytes, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }


    private static void handleRequest(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String id = path.replace("/student/", "");
        System.out.println(path);

        if (id.isEmpty()) {
            System.out.println("error: " + id + " Invalid request");
            sendResponse(exchange, "Invalid request".getBytes(StandardCharsets.UTF_8), 400);
            return;
        }

        try {
            Student student = requestStudentClasses(id);

            if (student == null) {
                // 学生信息为 null，尝试从文件读取
                File file = new File(Class_FOLDER, id + ".txt");
                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    sendResponse(exchange, fileContent, 200);
                } else {
                    System.out.println("error: " + id + " Student data not found");
                    sendResponse(exchange, "Student data not found".getBytes(StandardCharsets.UTF_8), 404);
                }
            } else {
                // 学生信息不为 null，保存到文件并响应
                String response = student.toString();
                response = response.replaceAll("=", ":");
                response = response.replaceAll("'", "\"");

                File file = new File(Class_FOLDER, id + ".txt");
                Files.writeString(file.toPath(), response);

                sendResponse(exchange, response.getBytes(StandardCharsets.UTF_8), 200);
            }
        } catch (Exception e) {
            System.out.println("error: " + id + " " + e.getMessage());
            sendResponse(exchange, e.getMessage().getBytes(StandardCharsets.UTF_8), 400);
        }
    }


    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}