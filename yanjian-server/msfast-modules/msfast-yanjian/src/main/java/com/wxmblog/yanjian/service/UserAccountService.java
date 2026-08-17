package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.yanjian.common.rest.response.front.pay.AddBalanceResponse;
import com.wxmblog.yanjian.common.rest.response.front.pay.BalancePageResponse;
import com.wxmblog.yanjian.entity.UserAccountEntity;

import java.math.BigDecimal;


/**
 * 用户钱包
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 13:57:13
 */
public interface UserAccountService extends IService<UserAccountEntity> {

    BigDecimal getBalance(String userId);

    ServiceR<AddBalanceResponse> getBalancePre();

    ServiceR<Void> addBalance(NotifyData request);

    PageResult<BalancePageResponse> accountPage(Integer pageIndex, Integer pageSize);
}

