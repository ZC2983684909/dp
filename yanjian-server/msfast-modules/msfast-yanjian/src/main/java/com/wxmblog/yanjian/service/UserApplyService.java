package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.SendMessageRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.UserApplyAuditRequest;
import com.wxmblog.yanjian.common.rest.response.front.apply.ApplyWechatPreResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserApplyPageResponse;
import com.wxmblog.yanjian.entity.UserApplyEntity;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
public interface UserApplyService extends IService<UserApplyEntity> {

    void audit(UserApplyAuditRequest request);

    void applyRead(String id);

    void readwait(String id);

    long getApplyMeCount(String ownerId);

    long getApplyWaitCount(String ownerId);

    ServiceR<ApplyWechatPreResponse> wechatPre(String userId);

    void executeUpdateStatus();
}

