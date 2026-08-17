package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.common.rest.request.front.user.ComplaintRequest;
import com.wxmblog.yanjian.entity.ComplaintEntity;


/**
 * 投诉
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-12-09 17:22:36
 */
public interface ComplaintService extends IService<ComplaintEntity> {

    void add(ComplaintRequest request);
}

