package com.wxmblog.yanjian.service.impl.pay;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.MsfCommonTool;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.enums.PlatformEnum;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.base.pay.common.rest.response.PayOrderData;
import com.wxmblog.base.pay.service.IPayService;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.pay.AmountPay;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.VipPriceVo;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("IPayServiceImpl")
public class IPayServiceImpl extends IPayService<AmountPay> {

    private static final Logger log = LoggerFactory.getLogger(IPayServiceImpl.class);
    @Autowired
    private MsfConfigService msfConfigService;

    @Autowired
    private UserApplyOrderService userApplyOrderService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserVipService userVipService;


    @Transactional
    @RedissonLock(lockName = "addVip:", isLockNameAppendOwner = true)
    @Override
    public ServiceR<PayOrderData> pay(AmountPay request) {

        PayOrderData payOrderData = new PayOrderData();

        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity frUserEntity = tUserService.getById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            return ServiceR.fail(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
        payOrderData.setOpenId(PlatformEnum.JS_API.equals(request.getPlatform()) ? userProfileEntity.getPublicOpenId() : frUserEntity.getOpenId());
        payOrderData.setBody("开通vip");
        payOrderData.setOutTradeNo(MsfCommonTool.UUID());
        String vipPrice = msfConfigService.getValueByCode("vipPrice");
        if (StringUtils.isBlank(vipPrice)) {
            return ServiceR.fail("没有配置价格");
        }
        List<VipPriceVo> vipPriceVoList = JSONObject.parseArray(vipPrice, VipPriceVo.class);
        VipPriceVo vipPriceVo = vipPriceVoList.stream().filter(p -> p.getPrice().compareTo(request.getAmount()) == 0).findFirst().orElse(null);
        if (vipPriceVo == null) {
            return ServiceR.fail("没有对应的价格");
        }
        payOrderData.setTotalFee(vipPriceVo.getPrice().multiply(new BigDecimal("100")).intValue());

        Map<String, String> attach = new HashMap<>();
        attach.put("month", vipPriceVo.getMonth().toString());
        payOrderData.setAttach(attach);

        UserApplyOrderEntity userApplyOrderEntity = new UserApplyOrderEntity();
        userApplyOrderEntity.setBody(payOrderData.getBody());
        userApplyOrderEntity.setOutTradeNo(payOrderData.getOutTradeNo());
        userApplyOrderEntity.setAmount(vipPriceVo.getPrice());
        userApplyOrderEntity.setUserId(TokenUtils.getOwnerId());
        userApplyOrderEntity.setStatus("1");
        userApplyOrderEntity.setType("1");
        userApplyOrderEntity.setResult(payOrderData.getBody() + ":" + vipPriceVo.getMonth() + "个月");
        userApplyOrderService.save(userApplyOrderEntity);
        return ServiceR.ok(payOrderData);
    }

    @Transactional
    @Override
    public ServiceR<Void> notify(NotifyData request) {

        RLock lock = redissonClient.getLock(request.getOutTradeNo());
        try {
            lock.lock(15, TimeUnit.SECONDS);
            ServiceR<Void> ret = userVipService.addVipNotify(request);
            if (ServiceR.isError(ret)) {
                log.error("添加vip失败：{}", ret.getMsg());
                return ret;
            }
        } finally {
            lock.unlock();
        }
        log.info("添加vip成功：{}", request.getOutTradeNo());
        return ServiceR.ok();
    }

}
