package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.yanjian.common.rest.response.front.pay.AddBalanceResponse;
import com.wxmblog.yanjian.common.rest.response.front.pay.BalancePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.pay.BalancePriceVo;
import com.wxmblog.yanjian.common.rest.response.front.user.PersonalCenterResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserAccountDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Service("userAccountService")
public class UserAccountServiceImpl extends ServiceImpl<UserAccountDao, UserAccountEntity> implements UserAccountService {


    @Autowired
    private MsfConfigService msfConfigService;

    @Autowired
    private UserApplyOrderService userApplyOrderService;

    @Autowired
    private RegisterDistributionService registerDistributionService;

    @Autowired
    private DistributionAmountService distributionAmountService;

    @Autowired
    AsyncService asyncService;

    @Override
    public BigDecimal getBalance(String userId) {
        return this.baseMapper.getBalance(userId);
    }

    @Override
    public ServiceR<AddBalanceResponse> getBalancePre() {

        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity tUserEntity = tUserService.getById(TokenUtils.getOwnerId());
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        AddBalanceResponse addBalanceResponse = new AddBalanceResponse();
        BeanUtils.copyProperties(tUserEntity, addBalanceResponse);
        addBalanceResponse.setBalance(getBalance(TokenUtils.getOwnerId()));
        String addApply = msfConfigService.getValueByCode("addApply");
        if (StringUtils.isNotBlank(addApply)) {
            List<BalancePriceVo> jsonObjects = JSON.parseArray(addApply, BalancePriceVo.class);
            addBalanceResponse.setBalancePriceVos(jsonObjects);
        }

        //二次检查订单是否成功
        asyncService.addAmount(TokenUtils.getOwnerId());

        return ServiceR.ok(addBalanceResponse);
    }

    @Transactional
    @Override
    public ServiceR<Void> addBalance(NotifyData request) {

        Wrapper<UserApplyOrderEntity> wrapper = new QueryWrapper<UserApplyOrderEntity>().lambda()
                .eq(UserApplyOrderEntity::getOutTradeNo, request.getOutTradeNo())
                .orderByDesc(UserApplyOrderEntity::getCreateTime)
                .last("limit 1");

        UserApplyOrderEntity userApplyOrderEntity = userApplyOrderService.getOne(wrapper);
        if (userApplyOrderEntity != null && "1".equals(userApplyOrderEntity.getStatus())) {
            userApplyOrderEntity.setPlatformTradeNo(request.getPlatformTradeNo());
            userApplyOrderEntity.setStatus("2");
            userApplyOrderService.updateById(userApplyOrderEntity);

            String amountStr = request.getAttach().get("amount");
            BigDecimal amount = new BigDecimal(amountStr);
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(userApplyOrderEntity.getUserId());
            userAccountEntity.setAmount(amount);
            userAccountEntity.setRemarks("颜币充值");
            userAccountEntity.setSource("UserApplyOrderEntity");
            userAccountEntity.setSourceId(userApplyOrderEntity.getId());
            UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
            userAccountService.save(userAccountEntity);

            //添加用户分销奖励
            Wrapper<RegisterDistributionEntity> registerDistributionEntityWrapper = new QueryWrapper<RegisterDistributionEntity>().lambda()
                    .eq(RegisterDistributionEntity::getUserId, userApplyOrderEntity.getUserId())
                    .orderByDesc(RegisterDistributionEntity::getCreateTime)
                    .last("limit 1");
            RegisterDistributionEntity registerDistributionEntity = registerDistributionService.getOne(registerDistributionEntityWrapper);
            if (registerDistributionEntity != null && StringUtils.isNotBlank(registerDistributionEntity.getDistributionPersonId())) {

                TUserService tUserService = SpringUtils.getBean(TUserService.class);
                TUserEntity tUserEntity = tUserService.getById(registerDistributionEntity.getDistributionPersonId());
                if (tUserEntity != null && FrUserStatusEnum.ENABLE.equals(tUserEntity.getStatus())) {
                    Wrapper<DistributionAmountEntity> distributionAmountEntityWrapper = new QueryWrapper<DistributionAmountEntity>().lambda()
                            .eq(DistributionAmountEntity::getUserId, registerDistributionEntity.getDistributionPersonId())
                            .eq(DistributionAmountEntity::getReason, "1")
                            .eq(DistributionAmountEntity::getOrderId, userApplyOrderEntity.getId());
                    if (distributionAmountService.count(distributionAmountEntityWrapper) == 0) {
                        DistributionAmountEntity distributionAmountEntity = new DistributionAmountEntity();
                        distributionAmountEntity.setUserId(registerDistributionEntity.getDistributionPersonId());
                        distributionAmountEntity.setBody("充值颜币");
                        distributionAmountEntity.setOrderId(userApplyOrderEntity.getId());
                        distributionAmountEntity.setReason("1");
                        distributionAmountEntity.setType("1");
                        distributionAmountEntity.setPayUserId(userApplyOrderEntity.getUserId());
                        BigDecimal ratio = new BigDecimal("0");
                        String prizeRatioConfig = msfConfigService.getValueByCode("prizeRatio");
                        if (StringUtils.isNotBlank(prizeRatioConfig)) {
                            JSONObject jsonObject = JSONObject.parseObject(prizeRatioConfig);
                            ratio = jsonObject.getBigDecimal("addAmount");
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
    public PageResult<BalancePageResponse> accountPage(Integer pageIndex, Integer pageSize) {
        Page<BalancePageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().accountPage(TokenUtils.getOwnerId());
        PageResult<BalancePageResponse> result = new PageResult<>(page);
        return result;
    }
}
