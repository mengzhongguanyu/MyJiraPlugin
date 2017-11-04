package com.atlassian.jira.event.mylisteners;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.atlassian.jira.event.ding.DoDing;

public class TestThread {

	public static void main(String[] args) {
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		final Future<String> future = executor.submit(new Callable<String>(){

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				
				return new TestThread().ding();
			}
			
		});
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					future.get(20, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					executor.shutdown();
				}
			}
		});
	}
	public String ding(){
		System.out.println("开始执行接口");
		try {
//			new DoDing().doDing("dongzhaohui@jetair.com.cn", "测试钉钉"+new Date());
			System.out.println("我是接口");
			Thread.sleep(1000);
			System.out.println("我是接口");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("接口执行结束");
		return "执行完了";
	}
}

