package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.common.rest.response.front.user.SignInResponse;
import com.wxmblog.yanjian.entity.SignInEntity;
import org.springframework.scheduling.annotation.Async;


/**
 * 用户签到
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 16:38:05
 */
public interface SignInService extends IService<SignInEntity> {

    SignInResponse getStatus();

    @Async
    void add(String userId);

    void exchange();
}

