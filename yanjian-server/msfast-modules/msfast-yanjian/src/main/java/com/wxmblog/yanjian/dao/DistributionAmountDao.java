package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.front.distribution.DistributionAmountRequest;
import com.wxmblog.yanjian.common.rest.response.front.distribution.DistributionAmountResponse;
import com.wxmblog.yanjian.common.rest.response.front.distribution.WithdrawRecordResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.SharePageResponse;
import com.wxmblog.yanjian.entity.DistributionAmountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户分销金额
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-21 16:46:20
 */
@Mapper
public interface DistributionAmountDao extends BaseMapper<DistributionAmountEntity> {

    List<DistributionAmountResponse> distributionAmountPage(DistributionAmountRequest request);

    List<WithdrawRecordResponse> withdrawRecordPage(String ownerId);
}
