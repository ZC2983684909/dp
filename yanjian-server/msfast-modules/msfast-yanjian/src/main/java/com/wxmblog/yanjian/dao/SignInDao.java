package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.SignInEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户签到
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 16:38:05
 */
@Mapper
public interface SignInDao extends BaseMapper<SignInEntity> {
	
}
