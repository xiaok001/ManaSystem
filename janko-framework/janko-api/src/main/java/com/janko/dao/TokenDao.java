package com.janko.dao;

import com.janko.entity.TokenEntity;

/**
 * 用户Token
 * 
 * @author janko
 * @email 
 * @date 2017-03-23 15:22:07
 */
public interface TokenDao extends BaseDao<TokenEntity> {
    
    TokenEntity queryByUserId(Long userId);

    TokenEntity queryByToken(String token);
	
}
