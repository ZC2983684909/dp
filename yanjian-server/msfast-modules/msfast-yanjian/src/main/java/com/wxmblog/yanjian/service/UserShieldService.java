package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.entity.UserShieldEntity;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-27 22:49:44
 */
public interface UserShieldService extends IService<UserShieldEntity> {

    PageResult<UserInfoPageResponse> getPage(Integer pageIndex, Integer pageSize);

    void shield(String id);

    void cancel(String id);
}

