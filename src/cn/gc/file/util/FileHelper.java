package cn.gc.file.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @ClassName: FileHelper
 * @Description: 操作本地文件
 * @author 郭灿
 * @date 2017年11月28日 下午2:29:11
 *
 */
public class FileHelper {

    private static FileHelper helper = null;

    private FileHelper() {

    }

    public static FileHelper getInstance() {
        if (helper == null) {
            helper = new FileHelper();
        }
        return helper;
    }

    /**
     * 
     * @Title: readByte
     * @Description: 从流中读取字节数
     * @author 郭灿
     * @param in
     * @return
     */
    public byte[] readByte(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];// 临时存储
        int length = 0;//
        try {
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);// 一边读取一边写入内存
            }
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @Title: writeByteToFile
     * @Description: 向文件中写入数据
     * @author 郭灿
     * @param data
     * @param file
     * @return
     */
    public boolean writeByteToFile(byte[] data, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
     * @Title: cloneFile
     * @Description:文件复制
     * @author 郭灿
     * @param srcFile
     * @param destPath
     * @return
     */
    public boolean cloneFile(File srcFile, String destPath) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            File destFile = new File(destPath, srcFile.getName());
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length = 0;//
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
     * @Title: readTextFromFile
     * @Description: 从文本文件中读取文本
     * @author 郭灿
     * @param textFile
     * @param charset
     * @return
     */
    public String readTextFromFile(File textFile, String charset) {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(textFile), charset);
            StringBuffer content = new StringBuffer();
            int length = 0;
            char[] buffer = new char[1024];
            while ((length = reader.read(buffer)) != -1) {
                content.append(buffer, 0, length);
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 
     * @Title: writeText
     * @Description: 向文本文件中写入文本
     * @author 郭灿
     * @param text
     * @param textFile
     * @param isAppend
     * @return
     */
    public boolean writeText(String text, File textFile, boolean isAppend) {
        try {
            FileWriter writer = new FileWriter(textFile, true);
            writer.write(text);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
     * @Title: writeText
     * @Description:向文本文件中写入文本
     * @author 郭灿
     * @param text
     * @param textFile
     * @param charset
     * @return
     */
    public boolean writeText(String text, File textFile, String charset) {
        try {
            return writeByteToFile(text.getBytes(charset), textFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
