package cn.gc.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	// 响应头
	private Map<String, List<String>> responseHeader = null;

	private static GcHttpFileDownLoad fileDownLoad;

	//是否追加到尾部
	private boolean isAppend = false;
	
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
			responseHeader = conn.getHeaderFields();
			// int length = conn.getContentLength();// 获取网络文件的长度
			if (file.exists() && !isAppend) {
				file.delete();
			}
			FileOutputStream out = new FileOutputStream(file,isAppend);
			InputStream inStream = conn.getInputStream();
			byte[] buffer = new byte[1024 * 1024 * 1024];// 1MB
			int len = 0;
			int off = isAppend?(int) file.length():0;
			while ((len = inStream.read(buffer)) != -1) {
				out.write(buffer, off, len);
				// TODO 下载进度
				System.out.println("downloading...");
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

	public void setHttpHeader(String key, Object value) {
		this.headers.put(key, value);
	}

	public void clearHttpHeaders() {
		this.headers.clear();
	}

	public void isAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}
	public static void main(String[] args) throws IOException {
		GcHttpFileDownLoad gcHttpFileDownLoad = GcHttpFileDownLoad.getInstance();
		gcHttpFileDownLoad.isAppend(true);
		gcHttpFileDownLoad.setHttpHeader("Cookie","sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221605d36a688c8-04774e32d7ef64-5e183017-1fa400-1605d36a6898f6%22%7D; analytics=GA1.2.2050039861.1514023348; ac_username=%E7%A7%8D%E7%B2%AE%E5%A4%A7%E6%88%B7; auth_key=2023365; auth_key_ac_sha1=-362524862; auth_key_ac_sha1_=BqEhNrpapo4qBJvgQEbIDDkRu7k=; checkReal=0; ac_userimg=http%3A%2F%2Fcdn.aixifan.com%2Fdotnet%2Fartemis%2Fu%2Fcms%2Fwww%2F201612%2F18104451ttfxcfyn.jpg; tma=191396026.48745852.1522236363980.1522236363980.1522478178539.2; tmd=3.191396026.48745852.1522236363980.; bfd_g=819951b0ac9d80e2000078f0000003d45aa900f4; XSRF-TOKEN=eyJpdiI6InNnZjNSbTUrK09jVXFrSUFcL2FvNStnPT0iLCJ2YWx1ZSI6Ik1NMGREcVFhY09PVFwvV3IxMFdMN1wvK2tKUnppXC9zRTRKc0ZwUXFKUlpIc0lSbFZ2SUlZNm1KNDJWMlhSSU42cDkyclN5UUFiQ2htYUhcL0xSME1uK2pqdz09IiwibWFjIjoiZjA0Y2ViZDI4OWE0YTlkNTIzZWJkZmFkMDI4MTk2YzhiNmI3MTE3YjYzNjBjNWZhODI3Y2NmZTNjNjk0OTI4MiJ9; ap_session=eyJpdiI6IkkxaDhMWDVmMWZEd2VZWlFOU0RnNWc9PSIsInZhbHVlIjoiUFZlVFFxMURXSTlHd2tkc29Xdkc1azczcjIwZ0hkZGp5dm5WMFpJZ2lac2hEUGd5NkFFYUVWaVBjWnEzU2p5WnB4aTF4eGxUeUpJcUhUc1BcL0hYZVdBPT0iLCJtYWMiOiI2NWM2NzM3ZWQ2NTQyY2IwYTVkMzE0MTI4MzQ3NzIzZjJiYzY4ZjI4NzU2OGY5NDAyOGJlODNiZDEzYWIyNDY0In0%3D; ac__avi=1010252503899e26dcc76df07ac192ae2d5b485c999crpcxd036ae2350f705f1667a487b8e511573; online_status=7620; userLevel=10; userGroupLevel=1; checkMobile=1; checkEmail=1; Hm_lvt_2af69bc2b378fb58ae04ed2a04257ed1=1522468461,1522478188,1522479037,1522482766; Hm_lpvt_2af69bc2b378fb58ae04ed2a04257ed1=1522483084");
						gcHttpFileDownLoad.downLoad(
				"http://video.acfun.cn/060140020400005AB384C9000100039D0000000000-0000-0000-01FF-2DE300000000.mp4?customer_id=5859fdaee4b0eaf5dd325b91&start=0.0&auth_key=1522492184-101025250693e6871f3ec013e73150e9a6b084a8ea5erpcx1236p34p10pa29f9f526e77e67-ACFUN-fef69738c8e296a041a19e49d6b53997",
				"D:\\SD", "2.mp4");
	}
}
