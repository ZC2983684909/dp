package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.UserVisitEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户浏览记录
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-21 22:16:00
 */
@Mapper
public interface UserVisitDao extends BaseMapper<UserVisitEntity> {
	
}
