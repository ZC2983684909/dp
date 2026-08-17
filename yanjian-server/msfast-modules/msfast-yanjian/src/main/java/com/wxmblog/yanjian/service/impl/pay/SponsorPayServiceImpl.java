package com.wxmblog.yanjian.service.impl.pay;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.MsfCommonTool;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.enums.PlatformEnum;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.base.pay.common.rest.response.PayOrderData;
import com.wxmblog.base.pay.service.IPayService;
import com.wxmblog.yanjian.common.rest.request.front.pay.SponsorAmountPay;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("SponsorPayServiceImpl")
public class SponsorPayServiceImpl extends IPayService<SponsorAmountPay> {


    @Autowired
    private TUserService tUserService;

    @Autowired
    private UserProfileService userProfileService;

    @Transactional
    @Override
    public ServiceR<PayOrderData> pay(SponsorAmountPay request) {
        PayOrderData payOrderData = new PayOrderData();
        TUserEntity frUserEntity = this.tUserService.getById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
        payOrderData.setOpenId(PlatformEnum.JS_API.equals(request.getPlatform()) ? userProfileEntity.getPublicOpenId() : frUserEntity.getOpenId());
        payOrderData.setBody("赞助");
        payOrderData.setOutTradeNo(MsfCommonTool.UUID());
        payOrderData.setTotalFee(request.getAmount().multiply(new BigDecimal("100")).intValue());
        return ServiceR.ok(payOrderData);
    }

    @Transactional
    @Override
    public ServiceR<Void> notify(NotifyData request) {

        return ServiceR.ok();
    }

}
