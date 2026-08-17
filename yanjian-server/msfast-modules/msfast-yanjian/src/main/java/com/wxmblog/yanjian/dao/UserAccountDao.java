package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.response.front.pay.BalancePageResponse;
import com.wxmblog.yanjian.entity.UserAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 13:57:13
 */
@Mapper
public interface UserAccountDao extends BaseMapper<UserAccountEntity> {

    BigDecimal getBalance(String userId);

    List<BalancePageResponse> accountPage(String ownerId);
}
