package com.atlassian.jira.event.memcached;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.event.ding.DoDing;
import com.atlassian.jira.event.mylisteners.DingListener;

public class TestEmail {
	public static void main(String[] args) throws Exception{
		Long startLong = new Date().getTime();
//		List<Map> list = new DoDing().getEmails();
		Long endLong = new Date().getTime();
		System.out.println("取list用时："+(endLong - startLong));
		Long start = new Date().getTime();
//		for (Map map : list) {
//			if ("dongzhaohui@jetair.com.cn".equals(map.get("email"))) {
//				
//				System.out.println(map.get("id"));
//			}
//		}
//		String name = new DingListener().getName("dongzhaohui@jetair.com.cn");
//		System.out.println(name);
		Long end = new Date().getTime();
		System.out.println("循环遍历用时："+(end - start));
	}
}
