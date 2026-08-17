package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.UserNatureVisitEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户浏览记录
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-03 16:57:08
 */
@Mapper
public interface UserNatureVisitDao extends BaseMapper<UserNatureVisitEntity> {

    void deleteNatureVisit();

    void updateNatureVisit();
}
