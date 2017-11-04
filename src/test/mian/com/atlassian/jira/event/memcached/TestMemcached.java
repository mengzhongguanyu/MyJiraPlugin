package com.atlassian.jira.event.memcached;

import java.util.Set;

import com.danga.MemCached.MemCachedClient;

import electric.util.holder.booleanInOut;

public class TestMemcached {
	public static void main(String[] args) {
		MemCachedClient cached = EmailUtil.getMemCachedClient();
		setMcc();
		String getString = (String) cached.get("key");
		System.out.println(getString);
	}
	public static void setMcc(){
		MemCachedClient cached = EmailUtil.getMemCachedClient();
		boolean set = cached.set("key", "hello");
		System.out.println("boolean--set"+set);
	}
}
