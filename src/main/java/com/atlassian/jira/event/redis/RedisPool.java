package com.atlassian.jira.event.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	private static JedisPool pool = null;
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(100);
			config.setMaxIdle(8);
			config.setMaxWait(1000);
			config.setTestOnBorrow(true);
			pool = new JedisPool(config, "127.0.0.1", 6379,1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public synchronized static Jedis getJedis(){
		try {
			if (pool != null) {
				Jedis resource = pool.getResource();
				return resource;
			}else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void returnResource(final Jedis jedis){
		if (jedis != null) {
			pool.returnResource(jedis);
		}
	}
}
