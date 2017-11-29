package cn.gc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import cn.gc.file.upload.FormFile;
import cn.gc.file.util.FileHelper;

/**
 * 
 * @ClassName: GcHttpFileUpload
 * @Description: 文件上传
 * @author 郭灿
 * @date 2017年11月28日 下午2:51:53
 *
 */
// TODO 断点续传
public class GcHttpFileUpload {

    private static GcHttpFileUpload fileUpload = null;

    private FileHelper fileHelper = null;

    private GcHttpFileUpload() {
        fileHelper = FileHelper.getInstance();
    }

    public static GcHttpFileUpload getInstance() {
        if (fileUpload == null) {
            fileUpload = new GcHttpFileUpload();
        }
        return fileUpload;
    }

    // 上传文件 Socket
    public void upLoad(String path, Map<String, String> params, FormFile[] files) throws UnknownHostException, IOException {
        final String BOUNDARY = "---------------------------7da2137580612"; // 定义参数分隔符
        final String endline = "--" + BOUNDARY + "--\r\n";// 定义结束标记
        int fileDataLength = 0;
        for (FormFile uploadFile : files) {// 计算文件参数的长度
            StringBuilder fileExplain = new StringBuilder();
            fileExplain.append("--");
            fileExplain.append(BOUNDARY);
            fileExplain.append("\r\n");
            fileExplain.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileExplain.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            fileDataLength += fileExplain.length();
            if (uploadFile.getInStream() != null) {
                fileDataLength += uploadFile.getFile().length();
            } else {
                fileDataLength += uploadFile.getData().length;
            }
            fileDataLength += "\r\n".length();
        }
        StringBuilder textEntity = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {// 计算文本参数的长度
                textEntity.append("--");
                textEntity.append(BOUNDARY);
                textEntity.append("\r\n");
                textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                textEntity.append(entry.getValue());
                textEntity.append("\r\n");
            }
        }
        // 计算总长度
        int dataLength = textEntity.toString().getBytes().length + fileDataLength + endline.getBytes().length;
        URL url = new URL(path);
        int port = url.getPort() == -1 ? 80 : url.getPort();
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
        OutputStream outStream = socket.getOutputStream();
        // 向服务器输出头字段信息
        String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
        outStream.write(requestmethod.getBytes());
        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
        outStream.write(accept.getBytes());
        String language = "Accept-Language: zh-CN\r\n";
        outStream.write(language.getBytes());
        String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
        outStream.write(contenttype.getBytes());
        String contentlength = "Content-Length: " + dataLength + "\r\n";
        outStream.write(contentlength.getBytes());
        String alive = "Connection: Keep-Alive\r\n";
        outStream.write(alive.getBytes());

        String host = "Host: " + url.getHost() + ":" + port + "\r\n";
        outStream.write(host.getBytes());
        outStream.write("\r\n".getBytes());
        // 向服务器输出文本参数的实体数据
        outStream.write(textEntity.toString().getBytes());
        // 向服务器输出文件参数的实体数据
        for (FormFile uploadFile : files) {
            StringBuilder fileEntity = new StringBuilder();
            fileEntity.append("--");
            fileEntity.append(BOUNDARY);
            fileEntity.append("\r\n");
            fileEntity.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileEntity.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            outStream.write(fileEntity.toString().getBytes());
            if (uploadFile.getInStream() != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                long block = uploadFile.getFile().length();// 文件总长度
                while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
                    outStream.write(buffer, 0, len);
                    // TODO 上传进度
                }
                uploadFile.getInStream().close();
            } else {
                outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
            }
            outStream.write("\r\n".getBytes());
        }

        InputStream in = socket.getInputStream();
        byte[] responseData = fileHelper.readByte(in);
        // TODO 服务端返回
        outStream.flush();
        outStream.close();
        in.close();
        socket.close();
    }
}
