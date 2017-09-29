package com.janko.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.janko.service.RedisService;

@Service("redisService")
public class RedisServiceImpl implements RedisService {

	/**
	 * 写入缓存
	 * 
	 * @author
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, final String value, RedisTemplate redisTemplate) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			redisTemplate.delete(key); // 先删除已有的
			// operations.append(key, value);//不对value进行序列化
			operations.set(key, value);// 对value进行序列化（value值前面会有前缀）
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 批量新增 使用pipeline方式
	 * 
	 * @author
	 * @param list
	 *            其中存放的对象，需要包含一个key,作为存入redis中的key
	 * @return boolean
	 */
	public boolean addObjectList(final List list, final RedisTemplate redisTemplate) {
		Assert.notEmpty(list);
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				for (Object obj : list) {
					byte[] key = null;
					byte[] object = null;
					java.lang.reflect.Field[] field = obj.getClass().getDeclaredFields();// 获取实体类的所有属性，返回Field数组
					for (int j = 0; j < field.length; j++) { // 遍历所有属性
						java.lang.reflect.Field f = field[j];
						String name = field[j].getName(); // 获取属性的名字
						if ("key".endsWith(name)) {
							f.setAccessible(true); // 设置些属性是可以访问的
							Object val = new Object();
							try {
								val = f.get(obj);// 获取此属性的值
								key = serializer.serialize((String) val);
								break;
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
					JSONObject json = (JSONObject) JSONObject.toJSON(obj);
					object = serializer.serialize((String) json.toString());
					System.out.println(key + "-----" + json.toString());
					if (key != null)
						connection.setNX(key, object);
				}
				return true;
			}
		}, false, true) != null;
		return result;
	}

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public Object getObj(final String key, RedisTemplate redisTemplate) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	/**
	 * 删除 <br>
	 * 
	 * @param key
	 */
	@Override
	public void delete(String key, RedisTemplate redisTemplate) {
		List<String> list = new ArrayList<String>();
		list.add(key);
		delete(list, redisTemplate);
	}

	/**
	 * 删除多个 <br>
	 * 
	 * @param keys
	 */

	@Override
	public void delete(List<String> keys, RedisTemplate redisTemplate) {
		redisTemplate.delete(keys);
	}
}
