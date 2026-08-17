package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.yanjian.common.rest.request.front.distribution.DistributionAmountRequest;
import com.wxmblog.yanjian.common.rest.response.front.distribution.DistributionAmountResponse;
import com.wxmblog.yanjian.common.rest.response.front.distribution.WithdrawRecordResponse;
import com.wxmblog.yanjian.entity.DistributionAmountEntity;


/**
 * 用户分销金额
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-21 16:46:20
 */
public interface DistributionAmountService extends IService<DistributionAmountEntity> {

    PageResult<DistributionAmountResponse> distributionAmountPage(DistributionAmountRequest request, Integer pageIndex, Integer pageSize);

    PageResult<WithdrawRecordResponse> withdrawRecordPage(Integer pageIndex, Integer pageSize);
}

