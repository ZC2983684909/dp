package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.response.front.home.PopUpResponse;
import com.wxmblog.yanjian.entity.PopupRecordsEntity;

import java.util.List;


/**
 * 系统配置
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-03 15:53:38
 */
public interface PopupRecordsService extends IService<PopupRecordsEntity> {

    ServiceR<List<PopUpResponse>> popup(String  location);
}

