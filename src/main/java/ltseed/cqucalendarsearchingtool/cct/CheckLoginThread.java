package ltseed.cqucalendarsearchingtool.cct;

import static ltseed.cqucalendarsearchingtool.cct.SeleniumLogin.checkLogin;
import static ltseed.cqucalendarsearchingtool.cct.SeleniumLogin.login;

@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
class CheckLoginThread extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("开始检查登录");
            boolean b = !checkLogin();
            System.out.println("登录状态：" + b);
            if (b) {
                login();
            }
            try {
                Thread.sleep(3600000); // 暂停1小时
            } catch (InterruptedException ignored) {
            }
        }
    }
}
