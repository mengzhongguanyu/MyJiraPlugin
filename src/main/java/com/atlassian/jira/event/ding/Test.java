package com.atlassian.jira.event.ding;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections.FastArrayList;
import org.springframework.beans.factory.ListableBeanFactory;

import com.atlassian.crowd.cql.parser.antlr.CqlParser.string_return;
import com.atlassian.upm.Sys;

import electric.util.holder.longInOut;

public class Test {
	public static void main(String[] args) {
//		Long start = new Date().getTime();
//		try {
//			new DoDing().doDing("dongzhaohui@jetair.com.cn", "测试memcached"+new Date());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Long end = new Date().getTime();
//		System.out.println(end - start);
		
		try {
			List list = new Ding().getEmailList();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
