package com.atlassian.jira.event.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {

	private static ExecutorService pool;
	
	public static synchronized ExecutorService getPool(){
		if (pool == null) {
			pool = Executors.newFixedThreadPool(3);
			return pool;
		}
		return pool;
	}
}
