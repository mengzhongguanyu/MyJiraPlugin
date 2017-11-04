package com.atlassian.jira.event.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class TestPool {

	private ExecutorService pool;
	public void test1(){
		pool = MyThreadPool.getPool();
		final Future<String> future = pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
			
					try {
						System.out.println("test1"+"\t");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return null;
			}
		});
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					future.get(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					System.out.println("test1超时");
					future.cancel(true);
					e.printStackTrace();
				}
			}
		});
	}
	public void test2(){
		pool = MyThreadPool.getPool();
		final Future<String> future = pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				for (int i = 0; i < 6; i++) {
					try {
						System.out.println("test2"+"\t"+i);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}
		});
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					future.get(3 , TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					System.out.println("test2超时");
					future.cancel(true);
					e.printStackTrace();
				}
			}
		});
	}
	public void test3(){
		pool = MyThreadPool.getPool();
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < 5; i++) {
					try {
						System.out.println("test3"+"\t"+i);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	public void test4(){
		pool = MyThreadPool.getPool();
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < 5; i++) {
					try {
						System.out.println("test4"+"\t"+i);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new TestPool().test1();
		}
//		new TestPool().test2();
//		new TestPool().test3();
//		new TestPool().test4();
	}
}
