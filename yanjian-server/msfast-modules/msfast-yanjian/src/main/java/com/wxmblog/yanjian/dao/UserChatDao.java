package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.UserChatEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户聊天解锁
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-01 17:38:50
 */
@Mapper
public interface UserChatDao extends BaseMapper<UserChatEntity> {
	
}
