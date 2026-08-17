package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserApplyPageResponse;
import com.wxmblog.yanjian.entity.UserApplyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 备注
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
@Mapper
public interface UserApplyDao extends BaseMapper<UserApplyEntity> {

    List<UserApplyPageResponse> getPage(UserStarRequest request);

    List<UserApplyPageResponse> getMyApplyPage(UserStarRequest request);

    long getApplyMeCount(String ownerId);

    long getApplyWaitCount(String ownerId);

    long updateStatus();

    List<String> getTimeOutApplyList();
}
