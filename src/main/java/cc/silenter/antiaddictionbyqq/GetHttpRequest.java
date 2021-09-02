package cc.silenter.antiaddictionbyqq;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static cc.silenter.antiaddictionbyqq.AntiaddictionByQQ.instance;
import static cc.silenter.antiaddictionbyqq.AntiaddictionByQQ.log;

public class GetHttpRequest{
    public static JsonObject SendPostJsonObject(String path,String post){
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(post);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                try {
                    int len;
                    byte[] arr = new byte[1024];
                    while((len=bis.read(arr))!= -1){
                        bos.write(arr,0,len);
                }
                finally {
                    bis.close();
                }
                bos.flush();
                JsonParser parse = new JsonParser();
                return (JsonObject)parse.parse(bos.toString("utf-8"));
            }
            finally {
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            instance.getConfig().set("settings.use_holiday",0);log.info("Fail to load json, reset use_holiday to 0");
        }
        return null;
    }

    public static JsonObject sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();

        BufferedReader in = null;

        try {
            String urlNameString = url + "?" + param;

            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }  catch (Exception e) {
                e.printStackTrace();
                instance.getConfig().set("settings.use_holiday",0);log.info("Fail to load json, reset use_holiday to 0");
            }
        }
        JsonParser parse = new JsonParser();
        try {return (JsonObject)parse.parse(result.toString());}catch (Exception e){e.printStackTrace();instance.getConfig().set("settings.use_holiday",0);log.info("Fail to load json, reset use_holiday to 0");}
        return null;
    }
}
