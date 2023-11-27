package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.apache.http.client.methods.HttpGet;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static ltseed.cqucalendarsearchingtool.Student.requestStudentClasses;

public class SeleniumLogin {
    private static final File SERVER_PATH = new File("E:\\SERVER");

    public static void main(String[] args) {
        login();
    }

    public static void login() {
        System.setProperty("webdriver.edge.driver", "E:\\SERVER\\msedgedriver.exe");
        System.setProperty("webdriver.chrome.driver", "E:\\SERVER\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();

        // 设置日志记录级别
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
        options.setCapability("acceptInsecureCerts", true);
        WebDriver driver = new ChromeDriver(options);

        driver.get("https://sso.cqu.edu.cn/login?service=https:%2F%2Fmy.cqu.edu.cn%2Fauthserver%2Fauthentication%2Fcas");

        WebDriverWait wait = new WebDriverWait(driver, 10000); // 等待最多10秒
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.ant-row.ng-star-inserted > nz-input-group > input")));

        // 使用XPath定位用户名和密码输入框
        WebElement usernameInput = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.ant-row.ng-star-inserted > nz-input-group > input"));
        WebElement passwordInput = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-item.passwordInput.ant-row > nz-input-group > input"));

        // 输入用户名和密码
        usernameInput.sendKeys("02024809");
        passwordInput.sendKeys("yds93823108");

        // 定位并点击登录按钮
        WebElement loginButton = driver.findElement(By.cssSelector("#login-normal > div:nth-child(2) > form > div.login-normal-button.ant-row > div > button"));
        loginButton.click();

        // 此处可以添加后续操作，例如验证登录成功、获取cookie等
        wait = new WebDriverWait(driver, 60000); // 等待最多10秒
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app > section > section > main > div > div > div > div:nth-child(3) > div > div.ant-card-head > div > div.ant-card-extra > div > div.week-selector > span")));


        LogEntries logEntries = null;
        try {
            Thread.sleep(20000);
            logEntries = driver.manage().logs().get(LogType.PERFORMANCE);
        } catch (Exception ignored) {
            driver.quit();
            return;
        }
        for (LogEntry logEntry : logEntries) {
            if (Main.DEBUG) System.out.println(logEntry.getMessage());
            if (Main.DEBUG) System.out.println();
        }

        BufferedWriter writer = null;
        try {
            File file = new File(SERVER_PATH, "selenium_logs" + new Date().getTime() + ".txt");
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));

            for (LogEntry entry : logEntries) {
                writer.write(entry.getMessage());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (LogEntry entry : logEntries) {
            String[] strings = extractUrlAndAuthorization(entry.getMessage());
            if(strings == null) continue;
            if(strings[0].contains("1045")){
                String authorization = strings[1];
                String cookie = driver.manage().getCookies().toString();
                if(authorization != null) {
                    if (Main.DEBUG) System.out.println("Authorization: " + authorization);
                    if (Main.DEBUG) System.out.println("Cookie: " + cookie);
                    System.out.println("重新获取登录凭据完成，准备再次检查登录");
                } else continue;
                Main.Authorization = authorization;
                Main.Cookie = cookie;
                if (checkLogin()) {
                    System.out.println("检查完成，成功重新登陆");
                    break;
                }
            }
        }
        driver.quit();
    }

    public static boolean checkLogin() {
        try {
            Student student = requestStudentClasses("20212192");
            if (student != null) {
                return true;
            } else if (Main.DEBUG) System.out.println("fuck");
        } catch (InterruptedException e) {
            if (Main.DEBUG) System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * 提取基础URL和Authorization字段的函数。
     *
     * @param jsonStr JSON字符串
     * @return 包含基础URL和Authorization的字符串数组。如果无法提取，相应位置将为null。
     */
    public static String[] extractUrlAndAuthorization(String jsonStr) {
        String[] result = new String[2]; // 数组第一个元素为基础URL，第二个元素为Authorization

        try {
            JSONObject jsonObj = JSONObject.parseObject(jsonStr);

            // 尝试提取URL
            String url = jsonObj.getJSONObject("message")
                    .getJSONObject("params")
                    .getJSONObject("request")
                    .getString("url");
            result[0] = url.split("\\?")[0]; // 提取基础URL

            // 尝试提取Authorization
            String authorization = jsonObj.getJSONObject("message")
                    .getJSONObject("params")
                    .getJSONObject("request")
                    .getJSONObject("headers")
                    .getString("Authorization");
            result[1] = authorization;

        } catch (Exception e) {
            // 处理异常，如果发生异常，相关元素将保持为null
            return null;
        }

        return result;
    }
}
