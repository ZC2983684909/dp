package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.user.ApplyWxRequest;
import com.wxmblog.yanjian.common.rest.response.front.chat.ApplyChatPreResponse;
import com.wxmblog.yanjian.common.rest.vo.RewardSendVo;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserChatDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("userChatService")
public class UserChatServiceImpl extends ServiceImpl<UserChatDao, UserChatEntity> implements UserChatService {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserVipService userVipService;

    @Autowired
    private MsfConfigService msfConfigService;

    @Autowired
    private UserShieldService userShieldService;

    @Autowired
    private AsyncService asyncService;

    @Override
    public ServiceR<ApplyChatPreResponse> chatPre(String userId) {

        if (userId.equals(TokenUtils.getOwnerId())) {
            return ServiceR.fail(UserExceptionEnum.APPLY_SELF_EXCEPTION);
        }
        ApplyChatPreResponse response = new ApplyChatPreResponse();
        response.setBalance(userAccountService.getBalance(TokenUtils.getOwnerId()));
        response.setIsVip(userVipService.isVip(TokenUtils.getOwnerId()));

        String applyAmount = msfConfigService.getValueByCode("applyAmount");
        if (StringUtils.isNotBlank(applyAmount)) {
            response.setPrice(Integer.parseInt(applyAmount));
        }

        if (Boolean.TRUE.equals(response.getIsVip())) {
            String vipApplyLimit = msfConfigService.getValueByCode("vipApplyLimit");
            if (StringUtils.isNotBlank(vipApplyLimit)) {
                Wrapper<UserChatEntity> userApplyEntityWrapperCount = new QueryWrapper<UserChatEntity>().lambda()
                        .eq(UserChatEntity::getUserId, TokenUtils.getOwnerId())
                        .eq(UserChatEntity::getApplyWay, "2")
                        .ge(UserChatEntity::getCreateTime, DateUtil.beginOfDay(new Date()));

                long count = this.count(userApplyEntityWrapperCount);
                response.setVipCount(Long.parseLong(vipApplyLimit) - count);
            }
        } else {
            response.setVipCount(0L);
        }

        return ServiceR.ok(response);
    }


    @RedissonLock(lockName = "applyChat:", isLockNameAppendOwner = true)
    @Transactional
    @Override
    public ServiceR<Void> applyChat(ApplyWxRequest request) {

        Wrapper<UserShieldEntity> shieldWrapper = new QueryWrapper<UserShieldEntity>().lambda()
                .eq(UserShieldEntity::getUserId, request.getApplyUserId())
                .eq(UserShieldEntity::getShieldId, TokenUtils.getOwnerId());
        if (userShieldService.count(shieldWrapper) > 0) {
            throw new JrsfException(UserExceptionEnum.SHIELD_USER_EXCEPTION);
        }

        if (request.getApplyUserId().equals(TokenUtils.getOwnerId())) {
            throw new JrsfException(UserExceptionEnum.APPLY_SELF_EXCEPTION);
        }
        TUserService userService = SpringUtils.getBean(TUserService.class);

        TUserEntity ownerUser = userService.getById(TokenUtils.getOwnerId());
        if (ownerUser == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if ("1".equals(ownerUser.getInvisible())) {
            throw new JrsfException(UserExceptionEnum.USER_IS_INVISIBLE);
        }
        BigDecimal price = new BigDecimal("0");
        if ("2".equals(request.getApplyWay())) {

            if (!Boolean.TRUE.equals(userVipService.isVip(TokenUtils.getOwnerId()))) {
                return ServiceR.fail(UserExceptionEnum.NOT_VIP_USER_EXCEPTION);
            }

            String vipApplyLimit = msfConfigService.getValueByCode("vipApplyLimit");
            if (StringUtils.isNotBlank(vipApplyLimit)) {
                Wrapper<UserChatEntity> userApplyEntityWrapperCount = new QueryWrapper<UserChatEntity>().lambda()
                        .eq(UserChatEntity::getUserId, TokenUtils.getOwnerId())
                        .eq(UserChatEntity::getApplyWay, "2")
                        .ge(UserChatEntity::getCreateTime, DateUtil.beginOfDay(new Date()));
                long count = this.count(userApplyEntityWrapperCount);
                if (count >= Long.parseLong(vipApplyLimit)) {
                    return ServiceR.fail(UserExceptionEnum.APPLY_COUNT_ERROR_EXCEPTION);
                }
            }

        } else if ("1".equals(request.getApplyWay())) {
            BigDecimal blance = userAccountService.getBalance(TokenUtils.getOwnerId());
            String applyAmount = msfConfigService.getValueByCode("applyAmount");
            price = new BigDecimal(applyAmount);
            if (blance.compareTo(price) < 0) {
                return ServiceR.fail(UserExceptionEnum.BALANCE_LESS);
            }
        } else {
            return ServiceR.fail("解锁方式错误");
        }
        //添加聊天
        UserChatEntity userChatEntity = new UserChatEntity();
        userChatEntity.setUserId(TokenUtils.getOwnerId());
        userChatEntity.setApplyUserId(request.getApplyUserId());
        userChatEntity.setApplyWay(request.getApplyWay());
        save(userChatEntity);

        UserChatEntity userApplyChatEntity = new UserChatEntity();
        userApplyChatEntity.setUserId(request.getApplyUserId());
        userApplyChatEntity.setApplyUserId(TokenUtils.getOwnerId());
        userApplyChatEntity.setApplyWay(request.getApplyWay());
        save(userApplyChatEntity);

        if ("1".equals(request.getApplyWay())) {
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(TokenUtils.getOwnerId());
            //price 相反数
            userAccountEntity.setAmount(price.negate());
            userAccountEntity.setRemarks("解锁私信");
            userAccountEntity.setSource("UserChatEntity");
            userAccountEntity.setSourceId(userChatEntity.getId());
            userAccountService.save(userAccountEntity);
        }

        List<SmsData> smsDataList = new ArrayList<>();
        smsDataList.add(new SmsData("name", ownerUser.getNickName()));
        MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
        messageSendServcie.sendMessage(new SendUserMessageVo(userChatEntity.getApplyUserId(),
                SendUserMessageTypeEnum.MESSAGE,
                SendUserMessageEnum.UNLOCK_PRIVATE_MESSAGE,
                "期待与你聊天",
                smsDataList,
                false
                ));

        //查询用户是否是充值vip或是充值颜币后第一次解锁用户微信
        RewardSendVo rewardSendVo = new RewardSendVo();
        rewardSendVo.setApplyId(userChatEntity.getId());
        rewardSendVo.setPayUserId(userChatEntity.getUserId());
        rewardSendVo.setApplyUserId(userChatEntity.getApplyUserId());
        rewardSendVo.setType("2");
        asyncService.reward(rewardSendVo);

        return ServiceR.ok();
    }
}
