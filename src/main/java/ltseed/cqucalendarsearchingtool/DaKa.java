package ltseed.cqucalendarsearchingtool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ltseed.cqucalendarsearchingtool.Main.*;

public class DaKa {
    public static final String search_URL = "http://i.cqu.edu.cn/qljfwapp4/sys/lwStuReportEpidemic/modules/classInfoQuery/getClassInfoCount.do";
    public static final String list_URL = "http://i.cqu.edu.cn/qljfwapp4/sys/lwStuReportEpidemic/modules/classInfoQuery/listClassInfoQueryData.do";
    public static final String export_URL = "http://i.cqu.edu.cn/qljfwapp4/sys/emapcomponent/imexport/export.do";
    public static final String cookies = "EMAP_LANG=zh; THEME=indigo; _WEU=f7lfC_Wkw0cDFZxqsKKZHTR5cEnHTw4dBt0K70ml5ueDzl7ls3efNvy6XiNm9ZiuYnVUADA7qMYCDCNyAPC4axnwaIfYrJJRU0Nnvor0LAYEKYoGoAhm3DBCWTNxmyfuIglmpE7uCw5G8ZRrayPZR6OlH8hE3PqDvu2fBJ4u7dZB5oAXIexIwC1KV1a2O_qv; route=b804152302ecbd8619a6c144849ec19e; amp.locale=zh_CN; Hm_lvt_fbbe8c393836a313e189554e91805a69=1667190886; asessionid=b8ad1863-0f12-4891-bede-d7760668f1f5; MOD_AMP_AUTH=MOD_AMP_0c27b53e-805d-44a7-b682-b8a8c0f9d483; JSESSIONID=gQE15x1rvn2ua9ikdJfvvaa--4o7UmAdQI71OdqTZfSDOOnUfXMy!-1075406778" ;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(getJsonObjectFromI(export_URL, new HashMap<>(), new JSONObject()));
    }

    public static JSONObject getJsonObjectFromI(String url, Map<String, String> pr, JSONObject info) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            pr.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            pr.put("Host","i.cqu.edu.cn");
            pr.put("Referer","http://i.cqu.edu.cn/qljfwapp4/sys/lwStuReportEpidemic/index.do?t_s=1666698163732&amp_sec_version_=1&gid_=WWFMd1R3dG1nVDV6Qi9ScjZoRFJyTHNWYm16QzhiMXV4UjNJWlJQNVVKVDVlMHA2VTFSN1l0cnZYdHhJSk1JeWlNQWlGektSdzFlSXAxcDZwVWRxbmc9PQ&EMAP_LANG=zh&THEME=indigo");
            pr.put("Accept","application/json, text/plain, */*");
            pr.put("Accept-Encoding","gzip, deflate");
            pr.put("Connection","keep-alive");
            pr.put("Cookie",cookies);
            info = JSON.parseObject(RequestTool.doPOST(url, pr));
            if(DEBUG&&info!=null) System.out.println(info);
            if(info != null) break;
        }
        return info;
    }
}
