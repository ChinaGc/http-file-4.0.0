package cn.gc.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @ClassName: GcHttpFileDownLoad
 * @Description: 文件下载
 * @author 郭灿
 * @date 2017年11月28日 下午3:07:35
 *
 */
// TODO 断点下载
public class GcHttpFileDownLoad {

    private static GcHttpFileDownLoad fileDownLoad;

    private GcHttpFileDownLoad() {

    }

    public static GcHttpFileDownLoad getInstance() {
        if (fileDownLoad == null) {
            fileDownLoad = new GcHttpFileDownLoad();
        }
        return fileDownLoad;
    }

    public void downLoad(String url, String destPath) throws IOException {
        File path = new File(destPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, getFileName(url));
        URL downpath = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) downpath.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            int length = conn.getContentLength();// 获取网络文件的长度
            if (file.exists()) {
                file.delete();
            }
            int block = length;
            FileOutputStream out = new FileOutputStream(file);
            InputStream inStream = conn.getInputStream();
            byte[] buffer = new byte[1024 * 1024];// 1kb
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                // TODO 下载进度
            }
            out.close();
            inStream.close();
        } else {
            System.out.println("http状态码" + conn.getResponseCode());
        }
    }

    private String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
