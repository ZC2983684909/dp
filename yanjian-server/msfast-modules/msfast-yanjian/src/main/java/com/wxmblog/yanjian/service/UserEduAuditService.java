package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.user.EduRequest;
import com.wxmblog.yanjian.entity.UserEduAuditEntity;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-16 21:00:20
 */
public interface UserEduAuditService extends IService<UserEduAuditEntity> {

    R<EduRequest> checkEdu(String onlineVerificationCode);
}

