package com.janko.dao;

import com.janko.entity.UserEntity;

/**
 * 用户
 * 
 * @author janko
 * @email 
 * @date 2017-03-23 15:22:06
 */
public interface UserDao extends BaseDao<UserEntity> {

    UserEntity queryByMobile(String mobile);
}
