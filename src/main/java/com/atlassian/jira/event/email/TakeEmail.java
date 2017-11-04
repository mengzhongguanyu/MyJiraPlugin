package com.atlassian.jira.event.email;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.atlassian.jira.event.ding.Ding;

public class TakeEmail {
	public static List<Map<String, String>> emails;
	static {
		try {
			emails = Ding.getEmailList();
			timer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static List getEamils() {
		if (emails == null) {
			try {
				emails = Ding.getEmailList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return emails;
		}
		return emails;
	}

	public static void timer() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date time = calendar.getTime();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					emails = Ding.getEmailList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, time, 1000 * 60 * 60 * 24);
	}

}
