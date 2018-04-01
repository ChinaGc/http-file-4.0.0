package cn.gc.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

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

	// http请求头
	private Map<String, Object> headers = null;

	private static GcHttpFileDownLoad fileDownLoad;

	
	private GcHttpFileDownLoad() {
		headers = new HashMap<>();
	}

	public static GcHttpFileDownLoad getInstance() {
		if (fileDownLoad == null) {
			fileDownLoad = new GcHttpFileDownLoad();
		}
		return fileDownLoad;
	}

	public void downLoad(String url, String destPath) throws IOException {
		this.downLoad(url, destPath, getFileName(url));
	}

	public void downLoad(String url, String destPath, String destFileName) throws IOException {
		File path = new File(destPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(path, destFileName);
		URL downpath = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) downpath.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
			}
		}
		
		if (conn.getResponseCode() == 200) {
			//int length = conn.getContentLength();// 获取网络文件的长度
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream out = new FileOutputStream(file);
			InputStream inStream = conn.getInputStream();
			byte[] buffer = new byte[1024 * 1024 * 1024];// 1MB
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				// TODO 下载进度
				System.out.println("downloading...");
			}
			out.close();
			inStream.close();
		} else {
			System.out.println("http状态码" + conn.getResponseCode());
		}
	}
	
	//分段下载
	public void downLoad(String [] urls, String destPath, String [] destFileNames) throws IOException{
		for (int i=0;i<urls.length;i++) {
			downLoad(urls[i],destPath,destFileNames[i]);
		}
	}
	
	private String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

	public void setHttpHeader(String key, Object value) {
		this.headers.put(key, value);
	}

	public void clearHttpHeaders() {
		this.headers.clear();
	}
	public static void main(String[] args) throws Exception {
		GcHttpFileDownLoad gcHttpFileDownLoad = GcHttpFileDownLoad.getInstance();
		String cookie = "sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221605d36a688c8-04774e32d7ef64-5e183017-1fa400-1605d36a6898f6%22%7D; analytics=GA1.2.2050039861.1514023348; ac_username=%E7%A7%8D%E7%B2%AE%E5%A4%A7%E6%88%B7; auth_key=2023365; auth_key_ac_sha1=-362524862; auth_key_ac_sha1_=BqEhNrpapo4qBJvgQEbIDDkRu7k=; checkReal=0; ac_userimg=http%3A%2F%2Fcdn.aixifan.com%2Fdotnet%2Fartemis%2Fu%2Fcms%2Fwww%2F201612%2F18104451ttfxcfyn.jpg; tma=191396026.48745852.1522236363980.1522236363980.1522478178539.2; tmd=3.191396026.48745852.1522236363980.; bfd_g=819951b0ac9d80e2000078f0000003d45aa900f4; userGroupLevel=1; checkMobile=1; checkEmail=1; online_status=1; userLevel=10; ac__avi=101025310761e8d1b88e149e6f73039336c14859cfb8rpcx8e4737266f8472165884258ece200d3e; Hm_lvt_2af69bc2b378fb58ae04ed2a04257ed1=1522478188,1522479037,1522482766,1522543152; Hm_lpvt_2af69bc2b378fb58ae04ed2a04257ed1=1522543178";
		gcHttpFileDownLoad.setHttpHeader("Cookie",cookie);
		
		String [] urls = new String[]{"http://video.acfun.cn/060040020400005AB384C9000100039D0000000000-0000-0000-01FF-2DE300000000.mp4?customer_id=5859fdaee4b0eaf5dd325b91&start=0.0&auth_key=1522552188-10102531078758c2ddb7f8344dab543a44035a47fd50rpcx1236p35p31p2b5278ef7f0b014-ACFUN-de315a0f50567cd2c5306a324a1cdab4"
			,"http://video.acfun.cn/060140020400005AB384C9000100039D0000000000-0000-0000-01FF-2DE300000000.mp4?customer_id=5859fdaee4b0eaf5dd325b91&start=0.0&auth_key=1522557279-10102531078758c2ddb7f8344dab543a44035a47fd50rpcx1236p35p31p2b5278ef7f0b014-ACFUN-946bc3e2691bd3ccd6899037fc6dfe73"	
		}; 
		gcHttpFileDownLoad.downLoad(urls,"D:\\SD", new String[]{"1.mp4","2.mp4"});
	}
}
