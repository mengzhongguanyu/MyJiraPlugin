package com.atlassian.jira.event.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TestPool {
	@Test
	public void getMap(){
		Jedis jedis = RedisPool.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "lisi");
		map.put("age", "11");
		jedis.hmset("user", map);
		
	}
	@Test
	public void testList(){
		Jedis jedis = RedisPool.getJedis();
		List<String> list = jedis.hmget("user","name","age");
		for (String string : list) {
			System.out.println(string);
			
		}
	}
	
}
