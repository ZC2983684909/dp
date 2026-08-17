package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 弹窗记录
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-21 16:40:54
 */
@Mapper
public interface UserProfileDao extends BaseMapper<UserProfileEntity> {
	
}
