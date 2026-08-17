package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserStarResponse;
import com.wxmblog.yanjian.entity.UserStarEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 备注
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 17:45:51
 */
@Mapper
public interface UserStarDao extends BaseMapper<UserStarEntity> {

    List<UserStarResponse> getPage(UserStarRequest request);

    List<UserStarResponse> mystarPage(UserStarRequest request);

    Long getCount(String userId);
}
