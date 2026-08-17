package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.UserVipDescResponse;
import com.wxmblog.yanjian.entity.UserVipEntity;


/**
 * 用户vip
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 14:23:42
 */
public interface UserVipService extends IService<UserVipEntity> {

    Boolean isVip(String userId);

    ServiceR<UserVipDescResponse> getPriceList();

    ServiceR<Void> addVipNotify(NotifyData request);

    ServiceR<UserVipDescResponse> getIsVip();
}

