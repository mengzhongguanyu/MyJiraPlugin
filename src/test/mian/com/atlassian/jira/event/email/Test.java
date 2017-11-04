package com.atlassian.jira.event.email;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.atlassian.jira.event.ding.Ding;

public class Test {
//	static {
//		timerTest();
//	}
//	public static void timerTest(){
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, 15);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		Date time = calendar.getTime();
//		Timer timer = new Timer();
//		timer.scheduleAtFixedRate(new TimerTask(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				System.out.println("动作执行"+new Date());
//			}
//			
//		}, time, 1000*10);
//	}
	public static void main(String[] args) {
		List<Map> list;
		try {
			list = Ding.getEmailList();
			int count = 0;
			for (Map map : list) {
				count ++;
				if ("董朝辉".equals(map.get("name"))) {
					System.out.println(map.get("email"));
				}
//				System.out.println(map);
			}
			System.out.println(count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("结束");
	}
}
