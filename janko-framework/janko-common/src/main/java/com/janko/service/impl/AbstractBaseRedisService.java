package com.janko.service.impl;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * @Title: redis实例
 * @Description: 不同的业务操作不同的redis库，传入不同的实例。
 * @author :
 * @date :2017年6月26日
 */
public abstract class AbstractBaseRedisService<K, V> {

	/**
	 * 数据库0
	 */
	@Resource(name = "carCurrentRedisTemplate")
	protected RedisTemplate<K, V> carCurrentRedisTemplate;// 数据库0：

	/**
	 * 设置redisTemplate
	 * 
	 * @param redisTemplate
	 *            the redisTemplate to set
	 */
	public void setCarCurrentRedisTemplate(RedisTemplate<K, V> carCurrentRedisTemplate) {
		this.carCurrentRedisTemplate = carCurrentRedisTemplate;
	}

}
