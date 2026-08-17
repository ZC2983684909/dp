package com.wxmblog.yanjian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.UserVipDescResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.VipPriceVo;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserVipDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;


@Service("userVipService")
public class UserVipServiceImpl extends ServiceImpl<UserVipDao, UserVipEntity> implements UserVipService {

    @Autowired
    MsfConfigService msfConfigService;

    @Autowired
    UserApplyOrderService userApplyOrderService;

    @Autowired
    RegisterDistributionService registerDistributionService;

    @Autowired
    DistributionAmountService distributionAmountService;

    @Autowired
    AsyncService asyncService;

    @Override
    public Boolean isVip(String userId) {
        Wrapper<UserVipEntity> userVipEntityWrapper = new QueryWrapper<UserVipEntity>().lambda().eq(UserVipEntity::getUserId, TokenUtils.getOwnerId()).eq(UserVipEntity::getLevel, "vip").gt(UserVipEntity::getExpirationDate, new Date());
        return count(userVipEntityWrapper) > 0;
    }

    @Override
    public ServiceR<UserVipDescResponse> getPriceList() {

        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity tUserEntity = tUserService.getById(TokenUtils.getOwnerId());
        if (tUserEntity == null) {
            return ServiceR.fail(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserVipDescResponse userVipDescResponse = new UserVipDescResponse();
        userVipDescResponse.setAvatar(tUserEntity.getAvatar());

        Wrapper<UserVipEntity> userVipEntityWrapper = new QueryWrapper<UserVipEntity>().lambda().eq(UserVipEntity::getUserId, TokenUtils.getOwnerId()).eq(UserVipEntity::getLevel, "vip").gt(UserVipEntity::getExpirationDate, new Date());
        UserVipEntity userVipEntity = getOne(userVipEntityWrapper);
        userVipDescResponse.setIsVip(userVipEntity != null);
        userVipDescResponse.setExpireTime(userVipEntity != null ? userVipEntity.getExpirationDate() : null);

        String vipPrice = msfConfigService.getValueByCode("vipPrice");
        if (StringUtils.isNotBlank(vipPrice)) {
            userVipDescResponse.setVipPriceList(JSONObject.parseArray(vipPrice, VipPriceVo.class));
        }

        //二次检查用户充值的vip订单 防止系统错误导致未充值成功
        asyncService.reNewVip(TokenUtils.getOwnerId());

        return ServiceR.ok(userVipDescResponse);
    }

    @Transactional
    @Override
    public ServiceR<Void> addVipNotify(NotifyData request) {

        Wrapper<UserApplyOrderEntity> wrapper = new QueryWrapper<UserApplyOrderEntity>().lambda().eq(UserApplyOrderEntity::getOutTradeNo, request.getOutTradeNo()).orderByDesc(UserApplyOrderEntity::getCreateTime).last("limit 1");

        UserApplyOrderEntity userApplyOrderEntity = userApplyOrderService.getOne(wrapper);
        if (userApplyOrderEntity != null && "1".equals(userApplyOrderEntity.getStatus())) {
            userApplyOrderEntity.setPlatformTradeNo(request.getPlatformTradeNo());
            userApplyOrderEntity.setStatus("2");
            userApplyOrderService.updateById(userApplyOrderEntity);

            //添加用户vip时间
            Wrapper<UserVipEntity> userVipEntityWrapper = new QueryWrapper<UserVipEntity>().lambda().eq(UserVipEntity::getUserId, userApplyOrderEntity.getUserId()).eq(UserVipEntity::getLevel, "vip");
            UserVipEntity userVipEntity = getOne(userVipEntityWrapper);
            if (userVipEntity == null) {

                userVipEntity = new UserVipEntity();
                userVipEntity.setUserId(userApplyOrderEntity.getUserId());
                userVipEntity.setLevel("vip");
                int month = Integer.parseInt(request.getAttach().get("month"));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, month);
                userVipEntity.setExpirationDate(calendar.getTime());
                save(userVipEntity);
            } else {
                if (userVipEntity.getExpirationDate().before(new Date())) {
                    int month = Integer.parseInt(request.getAttach().get("month"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, month);
                    userVipEntity.setExpirationDate(calendar.getTime());
                    updateById(userVipEntity);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(userVipEntity.getExpirationDate());
                    int month = Integer.parseInt(request.getAttach().get("month"));
                    calendar.add(Calendar.MONTH, month);
                    userVipEntity.setExpirationDate(calendar.getTime());
                    updateById(userVipEntity);
                }
            }

            //添加用户分销奖励
            Wrapper<RegisterDistributionEntity> registerDistributionEntityWrapper = new QueryWrapper<RegisterDistributionEntity>().lambda().eq(RegisterDistributionEntity::getUserId, userApplyOrderEntity.getUserId()).orderByDesc(RegisterDistributionEntity::getCreateTime).last("limit 1");
            RegisterDistributionEntity registerDistributionEntity = registerDistributionService.getOne(registerDistributionEntityWrapper);
            if (registerDistributionEntity != null && StringUtils.isNotBlank(registerDistributionEntity.getDistributionPersonId())) {

                TUserService tUserService = SpringUtils.getBean(TUserService.class);
                TUserEntity tUserEntity = tUserService.getById(registerDistributionEntity.getDistributionPersonId());
                if (tUserEntity != null && FrUserStatusEnum.ENABLE.equals(tUserEntity.getStatus())) {
                    Wrapper<DistributionAmountEntity> distributionAmountEntityWrapper = new QueryWrapper<DistributionAmountEntity>().lambda()
                            .eq(DistributionAmountEntity::getUserId, registerDistributionEntity.getDistributionPersonId())
                            .eq(DistributionAmountEntity::getReason, "2")
                            .eq(DistributionAmountEntity::getOrderId, userApplyOrderEntity.getId());
                    if (distributionAmountService.count(distributionAmountEntityWrapper) == 0) {
                        DistributionAmountEntity distributionAmountEntity = new DistributionAmountEntity();
                        distributionAmountEntity.setUserId(registerDistributionEntity.getDistributionPersonId());
                        distributionAmountEntity.setBody("开通vip");
                        distributionAmountEntity.setOrderId(userApplyOrderEntity.getId());
                        distributionAmountEntity.setReason("2");
                        distributionAmountEntity.setType("1");
                        distributionAmountEntity.setPayUserId(userApplyOrderEntity.getUserId());
                        BigDecimal ratio = new BigDecimal("0");
                        String prizeRatioConfig = msfConfigService.getValueByCode("prizeRatio");
                        if (StringUtils.isNotBlank(prizeRatioConfig)) {
                            JSONObject jsonObject = JSONObject.parseObject(prizeRatioConfig);
                            ratio = jsonObject.getBigDecimal("addVip");
                        }
                        if (ratio.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal prizeAmount = userApplyOrderEntity.getAmount().multiply(ratio.divide(new BigDecimal("100"), 2, RoundingMode.DOWN)).setScale(2, RoundingMode.DOWN);
                            if (prizeAmount.compareTo(BigDecimal.ZERO) > 0) {
                                distributionAmountEntity.setAmount(prizeAmount);
                                distributionAmountService.save(distributionAmountEntity);
                            }
                        }
                    }
                }
            }


        }
        return ServiceR.ok();
    }

    @Override
    public ServiceR<UserVipDescResponse> getIsVip() {
        UserVipDescResponse userVipDescResponse = new UserVipDescResponse();
        userVipDescResponse.setIsVip(isVip(TokenUtils.getOwnerId()));
        return ServiceR.ok(userVipDescResponse);
    }
}
