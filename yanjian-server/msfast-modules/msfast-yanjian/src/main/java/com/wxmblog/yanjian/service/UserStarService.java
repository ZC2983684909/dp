package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserStarResponse;
import com.wxmblog.yanjian.entity.UserStarEntity;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 17:45:51
 */
public interface UserStarService extends IService<UserStarEntity> {

    void read(String id);

    long getCount(String userId);
}

