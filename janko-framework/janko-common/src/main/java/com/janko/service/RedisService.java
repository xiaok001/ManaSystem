package com.janko.service;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.redis.core.RedisTemplate;



public interface RedisService{

	/**
	 * 批量新增 使用pipeline方式
	 * 
	 * @author 
	 * @param list
	 *            其中存放的对象，需要包含一个key,作为存入redis中的key
	 * @return boolean
	 */
	boolean addObjectList(List<T> list, RedisTemplate redisTemplate);

	/**
	 * 写入缓存
	 * 
	 * @author 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, String value, RedisTemplate redisTemplate);

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @param redisTemplate
	 * @return
	 */
	public Object getObj(final String key, RedisTemplate redisTemplate);

	/**
	 * 删除 <br>
	 * ------------------------------<br>
	 * 
	 * @param key
	 */
	void delete(String key, RedisTemplate redisTemplate);

	/**
	 * 删除多个 <br>
	 * ------------------------------<br>
	 * 
	 * @param keys
	 */
	void delete(List<String> keys, RedisTemplate redisTemplate);

}
