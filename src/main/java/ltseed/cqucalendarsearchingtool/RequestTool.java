package ltseed.cqucalendarsearchingtool;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RequestTool {

    /**
     * 发送get请求
     *
     * @param url   请求URL
     * @param header header
     * @return result, null when can not connect to Internet
     */
    public static String doGet(String url, Map<String, String> header) {

        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            if (header != null) {
                for (String key : header.keySet()) {
                    httpGet.setHeader(key, header.get(key));
                }
            }

            // 执行请求
            try {
                response = httpClient.execute(httpGet);
            } catch (ClientProtocolException e){
                System.out.println(e.getLocalizedMessage());
                return null;
            } catch (IOException e) {
                System.out.println("网络连接失败，请检查网络！");
                return null;
            }
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static void login(String user,String password){
        PythonInterpreter pi = new PythonInterpreter();
        pi.execfile("F:\\login.py");
        
    }
}

