package cn.gc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.gc.file.util.FileHelper;

/**
 * @ClassName: GcHttpCllient
 * @Description: 基于HttpUrlConnection(s)实现http请求
 * @author 郭灿
 * @date 2017年11月28日 上午9:41:03
 */
public class GcHttpClient {

    // http请求头
    private Map<String, Object> headers = null;

    private String urlencoding = "UTF-8";
    
    private String defaultEncoding = "UTF-8";

    private FileHelper fileHelper = null;

    private HttpURLConnection conn = null;

    private Map<String, List<String>> responseHeader = null;

    private GcHttpClient() {
        headers = new HashMap<String, Object>();
        fileHelper = FileHelper.getInstance();
    }

    // 多例
    public static GcHttpClient getInstance() {
        return new GcHttpClient();
    }

    // 网络路径读取字节数据
    public byte[] getByteFromNet(String url) throws MalformedURLException, IOException {
        if (url.startsWith("https")) {
            conn = (HttpsURLConnection) new URL(url.toString()).openConnection();
        } else {
            conn = (HttpURLConnection) new URL(url.toString()).openConnection();
        }
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] responseData = fileHelper.readByte(in);
            return responseData;
        }
        return null;
    }

    // 发送HttpGet请求
    public String sendGETRequest(String path, Map<String, Object> params) throws MalformedURLException, IOException {
        StringBuilder url = new StringBuilder(path);
        url.append("?");
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    url.append(entry.getKey()).append("=");
                    if (urlencoding == null) {
                        url.append(URLEncoder.encode(entry.getValue().toString()));
                    } else {
                        url.append(URLEncoder.encode(entry.getValue().toString(), urlencoding));
                    }
                    url.append("&");
                }
            }
        }
        url.deleteCharAt(url.length() - 1);
        if (path.startsWith("https")) {
            conn = (HttpsURLConnection) new URL(url.toString()).openConnection();
        } else {
            conn = (HttpURLConnection) new URL(url.toString()).openConnection();
        }
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("accept-charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        conn.connect();// 建立实际的连接
        int responseCode = conn.getResponseCode();
        responseHeader = conn.getHeaderFields();
        System.out.println("responseCode=" + responseCode);
        if (responseCode == 200) {
            InputStream in = conn.getInputStream();
            byte[] responseData = fileHelper.readByte(in);
            return new String(responseData,defaultEncoding);
        }
        return String.valueOf(responseCode);
    }

    // 发送HttpPost请求
    public String sendPOSTRequest(String path, Map<String, Object> params) throws MalformedURLException, IOException {
        // 拼接参数
        StringBuilder data = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    data.append(entry.getKey()).append("=");
                    if (urlencoding == null) {
                        // data.append(entry.getValue());//
                        // body参数不需要进行URLEncoder
                    } else {
                        data.append(URLEncoder.encode(entry.getValue().toString(), urlencoding));
                    }
                    data.append("&");
                }
            }
            data.deleteCharAt(data.length() - 1);
        }
        // httpbody数据
        byte[] entity = data.toString().getBytes(defaultEncoding);
        if (path.startsWith("https")) {
            conn = (HttpsURLConnection) new URL(path).openConnection();
        } else {
            conn = (HttpURLConnection) new URL(path).openConnection();
        }
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);// 允许对外输出数据
        conn.setRequestProperty("Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        OutputStream outStream = conn.getOutputStream();
        outStream.write(entity);
        int statusCode = conn.getResponseCode();
        responseHeader = conn.getHeaderFields();
        System.out.println("responseCode=" + statusCode);
        if (statusCode == 200) {//
            InputStream in = conn.getInputStream();
            byte[] responseData = fileHelper.readByte(in);
            return new String(responseData,defaultEncoding);
        }
        return String.valueOf(statusCode);
    }

    public String sendPOSTRequest(String path, String jsonParams) throws MalformedURLException, IOException {
        // httpbody数据
        byte[] entity = jsonParams.getBytes(defaultEncoding);
        if (path.startsWith("https")) {
            conn = (HttpsURLConnection) new URL(path).openConnection();
        } else {
            conn = (HttpURLConnection) new URL(path).openConnection();
        }
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);// 允许对外输出数据
        conn.setRequestProperty("Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/json");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        OutputStream outStream = conn.getOutputStream();
        outStream.write(entity);
        int statusCode = conn.getResponseCode();
        responseHeader = conn.getHeaderFields();
        System.out.println("responseCode=" + statusCode);
        if (statusCode == 200) {//
            InputStream in = conn.getInputStream();
            byte[] responseData = fileHelper.readByte(in);
            return new String(responseData,defaultEncoding);
        }
        return String.valueOf(statusCode);
    }

    public void setHttpHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public void removeHttpHeaders(String key) {
        this.headers.remove(key);
    }

    public void clearHttpHeaders() {
        this.headers.clear();
    }

    public void setUrlEncoding(String encode) {
        this.urlencoding = encode;
    }

    public void setDefaultEncoding(String defaultEncoding){
        if(defaultEncoding!=null && defaultEncoding.length()>0){
            this.defaultEncoding = defaultEncoding;
        }
    }
    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        System.out.println(GcHttpClient.getInstance().sendGETRequest("http://www.baidu.com", null));
    }
}
