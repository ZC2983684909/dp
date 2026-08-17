package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.entity.RegisterDistributionEntity;
import org.springframework.scheduling.annotation.Async;


/**
 * 用户推广
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:08:10
 */
public interface RegisterDistributionService extends IService<RegisterDistributionEntity> {

    Long indirectCount(String userId);

    @Async
    void sendAward(String userId);

    Long distributionCount(String ownerId);
}

