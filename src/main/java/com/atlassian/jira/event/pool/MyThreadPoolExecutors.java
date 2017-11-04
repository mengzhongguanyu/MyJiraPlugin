package com.atlassian.jira.event.pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutors {
	private static ThreadPoolExecutor pool ;
	
	public static ThreadPoolExecutor getPool(){
		if (pool == null) {
			pool = new ThreadPoolExecutor(2, 3, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			return pool;
		}
		return pool;
	}
}
