package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.entity.UserApplyEntity;
import com.wxmblog.yanjian.entity.UserApplyOrderEntity;

import java.util.List;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
public interface UserApplyOrderService extends IService<UserApplyOrderEntity> {

   void updateTimeOut(String applyId,String userId);
}

