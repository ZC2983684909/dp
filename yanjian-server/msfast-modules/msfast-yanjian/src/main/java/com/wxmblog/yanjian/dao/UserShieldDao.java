package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.entity.UserShieldEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 备注
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-27 22:49:44
 */
@Mapper
public interface UserShieldDao extends BaseMapper<UserShieldEntity> {

    List<UserInfoPageResponse> getPage(UserStarRequest request);
}
