package com.badou.mworking.net;

import com.badou.mworking.util.FileUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {

	/**
	 * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容 1.创建一个URL对象
	 * 2.通过URL对象,创建一个HttpURLConnection对象 3.得到InputStream 4.从InputStream当中读取数据
	 * 
	 * @param urlStr
	 * @return
	 */
	public static String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
//			buffer = new BufferedReader(new InputStreamReader(
//					urlConn.getInputStream()));
//			while ((line = buffer.readLine()) != null) {
//				sb.append(line);
//			}
			
//			InputStream in = null;
//			in = urlConn.getInputStream();  
			InputStreamReader isr = new InputStreamReader(url.openStream(),"utf-8");
//            byte[] buf = new byte[1024];  
            char[] ch = new char[1024];
            for (int n; (n = isr.read(ch)) != -1;) {  
            	sb.append(new String(ch, 0, n));  
            }  
            
            isr.close();  
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(buffer!=null){
					buffer.close();
					buffer = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return -1:文件下载出错 0:文件下载成功 1:文件已经存在
	 */
	public static int downFile(String urlStr, String path,
			DownloadListener downloadListener) {
		InputStream inputStream = null;
		try {

			String tempPath = path + ".temp";
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urlStr);
			HttpResponse execute = client.execute(httpGet);
			int totalLength = (int) execute.getEntity().getContentLength();
			if (downloadListener != null)
				downloadListener.setTotalSize(totalLength);
			inputStream = execute.getEntity().getContent();
			File tempFile = FileUtils.write2SDFromInput(tempPath, inputStream,
					downloadListener);
			if (tempFile == null || !tempFile.exists()) {
				return -1;
			}
			if (tempFile.length() < totalLength) {
				tempFile.delete();
				return -1;
			}
			File resultFile = new File(path);
			if(resultFile.exists())
				resultFile.delete();
			if(!FileUtils.cutGeneralFile(tempPath, path)){
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (downloadListener != null)
			downloadListener.onDownloadFinish(path);
		return 0;
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 */
	public static InputStream getInputStreamFromURL(String urlStr,
			DownloadListener downloadListener) {
		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			if (downloadListener != null)
				downloadListener.setTotalSize(urlConn.getContentLength());
			inputStream = urlConn.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputStream;
	}

	/**
	 * 发送消息体到服务端
	 * 
	 * @param params
	 * @param encode
	 * @return
	 */
	public static String sendPostMessage(URL url, byte[] data) {

		try {
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setConnectTimeout(3000);
			urlConnection.setRequestMethod("POST"); // 以post请求方式提交
			urlConnection.setDoInput(true); // 读取数据
			urlConnection.setDoOutput(true); // 向服务器写数据
			// 设置请求体的类型是文本类型,表示当前提交的是文本数据
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			// 获得输出流，向服务器输出内容
			OutputStream outputStream = urlConnection.getOutputStream();
			// 写入数据
			outputStream.write(data, 0, data.length);
			outputStream.close();
			// 获得服务器响应结果和状态码
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == 200) {
				// 取回响应的结果
				return changeInputStream(urlConnection.getInputStream(),
						"UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将一个输入流转换成指定编码的字符串
	 * 
	 * @param inputStream
	 * @param encode
	 * @return
	 */
	private static String changeInputStream(InputStream inputStream,
			String encode) {

		// 内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
