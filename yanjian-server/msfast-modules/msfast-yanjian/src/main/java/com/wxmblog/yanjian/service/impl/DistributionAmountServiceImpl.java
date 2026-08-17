package com.wxmblog.yanjian.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.rest.request.front.distribution.DistributionAmountRequest;
import com.wxmblog.yanjian.common.rest.response.front.distribution.DistributionAmountResponse;
import com.wxmblog.yanjian.common.rest.response.front.distribution.WithdrawRecordResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.SharePageResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.DistributionAmountDao;
import com.wxmblog.yanjian.entity.DistributionAmountEntity;
import com.wxmblog.yanjian.service.DistributionAmountService;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service("distributionAmountService")
public class DistributionAmountServiceImpl extends ServiceImpl<DistributionAmountDao, DistributionAmountEntity> implements DistributionAmountService {

    @Override
    public PageResult<DistributionAmountResponse> distributionAmountPage(DistributionAmountRequest request, Integer pageIndex, Integer pageSize) {

        request.setOwnerId(TokenUtils.getOwnerId());
        Page<DistributionAmountResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().distributionAmountPage(request);
        PageResult<DistributionAmountResponse> result = new PageResult<>(page);
        result.getRows().forEach(sharePageResponse -> {
            if (sharePageResponse.getAmount() != null && sharePageResponse.getPayAmount() != null) {
                sharePageResponse.setRatio(sharePageResponse.getAmount().divide(sharePageResponse.getPayAmount(), 2, RoundingMode.DOWN).multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN).intValue());
            }

        });
        return result;
    }

    @Override
    public PageResult<WithdrawRecordResponse> withdrawRecordPage(Integer pageIndex, Integer pageSize) {
        Page<WithdrawRecordResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().withdrawRecordPage(TokenUtils.getOwnerId());
        PageResult<WithdrawRecordResponse> result = new PageResult<>(page);

        return result;
    }
}
