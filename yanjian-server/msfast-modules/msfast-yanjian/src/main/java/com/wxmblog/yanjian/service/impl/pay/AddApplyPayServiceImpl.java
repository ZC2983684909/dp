package com.wxmblog.yanjian.service.impl.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.MsfCommonTool;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.enums.PlatformEnum;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.base.pay.common.rest.response.PayOrderData;
import com.wxmblog.base.pay.service.IPayService;
import com.wxmblog.yanjian.common.rest.request.front.pay.ApplyPay;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserApplyOrderEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserAccountService;
import com.wxmblog.yanjian.service.UserApplyOrderService;
import com.wxmblog.yanjian.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("AddApplyPayServiceImpl")
public class AddApplyPayServiceImpl extends IPayService<ApplyPay> {

    @Autowired
    private MsfConfigService msfConfigService;

    @Autowired
    private UserApplyOrderService userApplyOrderService;

    @Autowired
    private TUserService tUserService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserAccountService userAccountService;

    @Transactional
    @RedissonLock(lockName = "addAmount:", isLockNameAppendOwner = true)
    @Override
    public ServiceR<PayOrderData> pay(ApplyPay request) {
        PayOrderData payOrderData = new PayOrderData();
        TUserEntity frUserEntity = this.tUserService.getById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
        payOrderData.setOpenId(PlatformEnum.JS_API.equals(request.getPlatform()) ? userProfileEntity.getPublicOpenId() : frUserEntity.getOpenId());
        payOrderData.setBody("充值颜币");
        payOrderData.setOutTradeNo(MsfCommonTool.UUID());
        String addApply = msfConfigService.getValueByCode("addApply");
        List<JSONObject> jsonObjects = JSON.parseArray(addApply, JSONObject.class);
        // 查询出key 为price 的值等于 request.getPrice()的对象
        JSONObject jsonObject = jsonObjects.stream().filter(o -> o.getBigDecimal("price").compareTo(request.getPrice()) == 0).findFirst().orElse(null);
        if (jsonObject == null) {
            return ServiceR.fail("找不到对应的价格");
        }
        BigDecimal amount = jsonObject.getBigDecimal("price");
        payOrderData.setTotalFee(amount.multiply(new BigDecimal("100")).intValue());
        Map<String, String> attach = new HashMap<>();
        attach.put("amount", jsonObject.getString("num"));
        payOrderData.setAttach(attach);

        UserApplyOrderEntity userApplyOrderEntity = new UserApplyOrderEntity();
        userApplyOrderEntity.setBody(payOrderData.getBody());
        userApplyOrderEntity.setOutTradeNo(payOrderData.getOutTradeNo());
        userApplyOrderEntity.setAmount(amount);
        userApplyOrderEntity.setUserId(TokenUtils.getOwnerId());
        userApplyOrderEntity.setStatus("1");
        userApplyOrderEntity.setType("2");
        userApplyOrderEntity.setResult(payOrderData.getBody() + ":" + jsonObject.getString("num") + "个");
        userApplyOrderService.save(userApplyOrderEntity);
        return ServiceR.ok(payOrderData);
    }

    @Transactional
    @Override
    public ServiceR<Void> notify(NotifyData request) {

        RLock lock = redissonClient.getLock(request.getOutTradeNo());
        try {
            lock.lock(15, TimeUnit.SECONDS);

            ServiceR<Void> serviceR = userAccountService.addBalance(request);
            if (ServiceR.isError(serviceR)) {
                log.error("充值颜币失败：{}", serviceR.getMsg());
                return serviceR;
            }
        } finally {
            lock.unlock();
        }

        log.info("充值颜币成功：{}", request.getOutTradeNo());
        return ServiceR.ok();
    }

}
