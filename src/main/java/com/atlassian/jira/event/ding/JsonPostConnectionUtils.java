package com.atlassian.jira.event.ding;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class JsonPostConnectionUtils {
	
	public final static int BUFFER_SIZE = 4096;

	public static String doJsonHttpPost(String xmlInfo,String URL,String method,String ContentType) throws IOException{	        
		 //返回值
	        String returnvalue="";
		 
			URL url = new URL(URL);
	        //定义访问连接对象
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        //设置为output形式访问
	        conn.setDoOutput(true);
	        //设置Method
	        conn.setRequestMethod(method);//POST
	        conn.setDoOutput(true); 
	        conn.setDoInput(true); 
	        conn.setRequestProperty("Charset", "UTF-8"); 
	        //设置Content-Type
	        conn.setRequestProperty("Content-Type", ContentType);//ContentType "POST","application/json"
	        //设置没有缓存
	        conn.setUseCaches(false);
	        //设置连接6秒
	        conn.setConnectTimeout(6000);
	        //设置读取6秒
	        conn.setReadTimeout(6000);
	        //写入发送信息
	        conn.getOutputStream().write(xmlInfo.getBytes("UTF-8"));
	        //连接服务
	        conn.connect();
	        //返回信息读取接口
	        InputStream is =  conn.getInputStream();
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = is.read(data,0,BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
	        data = null;  
	        returnvalue = new String(outStream.toByteArray(),"UTF-8");  
	        //关闭流接口
	        outStream.close();
	        //br.close();
	        conn.disconnect(); 
				 
		 return returnvalue;
	} 
	public static String getHttpResult(String url,String method) throws Exception
	{
		URL requestUrl = new URL(url);
		HttpURLConnection httpConnection =  (HttpURLConnection) requestUrl.openConnection();
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		httpConnection.setRequestMethod(method);
		//连接服务
        httpConnection.connect();
        //连接超时时间为6秒
        httpConnection.setConnectTimeout(6000);
        //读取的超时时间为6秒
        httpConnection.setReadTimeout(6000);
        //得到返回的数据
        InputStream inputStream = httpConnection.getInputStream();
//        String returnString = "";
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
		String data = "";
        StringBuffer sb = new StringBuffer();
		while ((data = br.readLine()) != null) {
			sb.append(data);
		}

		br.close();
        
//        byte [] data = new byte[4096];
//        int count = 0;
//        while(( count = inputStream.read(data, 0, 4096)) > 0)
//        {
//        	outStream.write(data, 0, 4096);
//        }
//        data =null;
//        returnString = new String(outStream.toByteArray(), "utf-8");
		return sb.toString();
	}	 
}
