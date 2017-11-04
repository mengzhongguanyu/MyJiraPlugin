package com.atlassian.jira.event.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class FileUtil {
	
	/**
	 * 读取配置文件中的某一项
	 * @param name
	 * @return
	 */
	public static synchronized String getProperties(String filename,String urlName){
		try {
            Properties properties = new Properties();
            InputStream file = FileUtil.class.getClassLoader().getResourceAsStream(filename);
            properties.load(file);
            return properties.getProperty(urlName);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;

	}
	
	
	/**
	 * 读取配置文件
	 * @return
	 */
	public static synchronized Properties getProperties(String propFile){
		try {
            Properties properties = new Properties();
            InputStream file = FileUtil.class.getClassLoader().getResourceAsStream(propFile);
            properties.load(file);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	/**
	 * 读取配置文件
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static String ParseFileToString(String propFile){
		InputStream file = FileUtil.class.getClassLoader().getResourceAsStream(propFile);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(file,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		StringBuilder sbuilder = new StringBuilder();
		String temp = "";
		try {
			temp = br.readLine();
			while(temp != null && temp != ""){
				sbuilder.append(temp);
				temp = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String orgData = sbuilder.toString();
		try {
			orgData = new String(orgData.getBytes());
//			System.out.println("orgData====" + orgData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orgData;
	}
}
