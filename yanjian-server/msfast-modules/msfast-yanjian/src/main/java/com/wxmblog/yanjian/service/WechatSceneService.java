package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.area.UnlimitedQRequest;
import com.wxmblog.yanjian.common.rest.response.front.home.UnlimitedQResponse;
import com.wxmblog.yanjian.common.rest.response.front.home.WechatSceneResponse;
import com.wxmblog.yanjian.entity.WechatSceneEntity;


/**
 * 微信小程序码
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-25 10:35:40
 */
public interface WechatSceneService extends IService<WechatSceneEntity> {

    ServiceR<UnlimitedQResponse> unlimitedQrCode(UnlimitedQRequest request);

    ServiceR<WechatSceneResponse> getScene(String id);
}

