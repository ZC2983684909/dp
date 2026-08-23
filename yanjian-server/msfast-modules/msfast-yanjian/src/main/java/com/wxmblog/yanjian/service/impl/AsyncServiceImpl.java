package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.constant.ConfigConstants;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.response.NotifyData;
import com.wxmblog.base.websocket.common.enums.MsgSendType;
import com.wxmblog.base.websocket.common.rest.request.BaseMessageInfo;
import com.wxmblog.base.websocket.service.IMessageService;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.VipPriceVo;
import com.wxmblog.yanjian.common.rest.vo.RewardSendVo;
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
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("asyncService")
public class AsyncServiceImpl implements AsyncService {
    private static final Logger log = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Autowired(required = false)
    JsapiServiceExtension service;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    MsfConfigService msfConfigService;


    @Transactional
    @Override
    public void meApply(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            UserApplyService userApplyService = SpringUtils.getBean(UserApplyService.class);
            Wrapper<UserApplyEntity> wrapper = new UpdateWrapper<UserApplyEntity>()
                    .lambda()
                    .in(UserApplyEntity::getId, ids)
                    .eq(UserApplyEntity::getApplyWaitStatus, "0")
                    .set(UserApplyEntity::getApplyWaitStatus, "1");
            userApplyService.update(wrapper);
        }
    }

    @Transactional
    @Override
    public void starMeRead(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            UserStarService userStarService = SpringUtils.getBean(UserStarService.class);
            Wrapper<UserStarEntity> wrapper = new UpdateWrapper<UserStarEntity>()
                    .lambda()
                    .in(UserStarEntity::getId, ids)
                    .eq(UserStarEntity::getReadStatus, "0")
                    .set(UserStarEntity::getReadStatus, "1");
            userStarService.update(wrapper);
        }
    }

    @Transactional
    @Override
    public void readVist(List<String> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            Wrapper<UserVisitEntity> updateWrapper = new UpdateWrapper<UserVisitEntity>().lambda()
                    .in(UserVisitEntity::getId, idList)
                    .eq(UserVisitEntity::getStatus, "0")
                    .set(UserVisitEntity::getStatus, "1");
            UserVisitService userVisitService = SpringUtils.getBean(UserVisitService.class);
            userVisitService.update(updateWrapper);
        }

    }

    @Transactional
    @Override
    public void reward(RewardSendVo rewardSendVo) {

        log.info("用户解锁微信发放奖励{}", JSON.toJSONString(rewardSendVo));
        Wrapper<UserApplyOrderEntity> wrapper = new UpdateWrapper<UserApplyOrderEntity>().lambda()
                .eq(UserApplyOrderEntity::getUserId, rewardSendVo.getPayUserId())
                .eq(UserApplyOrderEntity::getStatus, "2")
                .isNotNull(UserApplyOrderEntity::getPlatformTradeNo)
                .in(UserApplyOrderEntity::getType, "1", "2")
                .orderByDesc(UserApplyOrderEntity::getCreateTime)
                .last("limit 1");
        UserApplyOrderService userApplyOrderService = SpringUtils.getBean(UserApplyOrderService.class);
        UserApplyOrderEntity userApplyOrderEntity = userApplyOrderService.getOne(wrapper);
        if (userApplyOrderEntity != null) {
            Wrapper<UserApplyEntity> userApplyEntityWrapper = new UpdateWrapper<UserApplyEntity>().lambda()
                    .ne(UserApplyEntity::getId, rewardSendVo.getApplyId())
                    .eq(UserApplyEntity::getUserId, rewardSendVo.getPayUserId())
                    .ge(UserApplyEntity::getCreateTime, userApplyOrderEntity.getCreateTime());
            UserApplyService userApplyService = SpringUtils.getBean(UserApplyService.class);


            Wrapper<UserChatEntity> userChatEntityWrapper = new UpdateWrapper<UserChatEntity>().lambda()
                    .ne(UserChatEntity::getId, rewardSendVo.getApplyId())
                    .eq(UserChatEntity::getUserId, rewardSendVo.getPayUserId())
                    .ge(UserChatEntity::getCreateTime, userApplyOrderEntity.getCreateTime());
            UserChatService userChatService = SpringUtils.getBean(UserChatService.class);

            if (userApplyService.count(userApplyEntityWrapper) == 0
                    && userChatService.count(userChatEntityWrapper) == 0) {
                //用户充值后第一次消费
                TUserService tUserService = SpringUtils.getBean(TUserService.class);
                TUserEntity tUserEntity = tUserService.getById(rewardSendVo.getApplyUserId());
                if (tUserEntity != null && FrUserStatusEnum.ENABLE.equals(tUserEntity.getStatus())) {
                    Wrapper<DistributionAmountEntity> distributionAmountEntityWrapper = new QueryWrapper<DistributionAmountEntity>().lambda()
                            .eq(DistributionAmountEntity::getUserId, tUserEntity.getId())
                            .in(DistributionAmountEntity::getReason, "3", "5")
                            .eq(DistributionAmountEntity::getOrderId, userApplyOrderEntity.getId());
                    DistributionAmountService distributionAmountService = SpringUtils.getBean(DistributionAmountService.class);
                    MsfConfigService msfConfigService = SpringUtils.getBean(MsfConfigService.class);
                    if (distributionAmountService.count(distributionAmountEntityWrapper) == 0) {

                        DistributionAmountEntity distributionAmountEntity = new DistributionAmountEntity();
                        distributionAmountEntity.setUserId(tUserEntity.getId());
                        distributionAmountEntity.setBody("1".equals(rewardSendVo.getType()) ? "被解锁微信" : "被私聊");
                        distributionAmountEntity.setOrderId(userApplyOrderEntity.getId());
                        distributionAmountEntity.setReason("1".equals(rewardSendVo.getType()) ? "3" : "5");
                        distributionAmountEntity.setType("1");
                        distributionAmountEntity.setPayUserId(userApplyOrderEntity.getUserId());
                        BigDecimal ratio = new BigDecimal("0");

                        String prizeRatioConfig = msfConfigService.getValueByCode("prizeRatio");
                        if (StringUtils.isNotBlank(prizeRatioConfig)) {
                            JSONObject jsonObject = JSONObject.parseObject(prizeRatioConfig);
                            ratio = jsonObject.getBigDecimal("myWechat");
                        }
                        if (ratio.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal prizeAmount = userApplyOrderEntity.getAmount().multiply(ratio.divide(new BigDecimal("100"), 2, RoundingMode.DOWN)).setScale(2, RoundingMode.DOWN);
                            if (prizeAmount.compareTo(BigDecimal.ZERO) > 0) {
                                distributionAmountEntity.setAmount(prizeAmount);
                                distributionAmountService.save(distributionAmountEntity);
                            }
                        }
                    }


                    //查看被解锁微信的用户是否有上级
                    Wrapper<RegisterDistributionEntity> registerDistributionEntityWrapper = new QueryWrapper<RegisterDistributionEntity>().lambda()
                            .eq(RegisterDistributionEntity::getUserId, tUserEntity.getId())
                            .orderByDesc(RegisterDistributionEntity::getCreateTime)
                            .last("limit 1");
                    RegisterDistributionService registerDistributionService = SpringUtils.getBean(RegisterDistributionService.class);
                    RegisterDistributionEntity registerDistributionEntity = registerDistributionService.getOne(registerDistributionEntityWrapper);
                    if (registerDistributionEntity != null && StringUtils.isNotBlank(registerDistributionEntity.getDistributionPersonId()) && !userApplyOrderEntity.getUserId().equals(registerDistributionEntity.getDistributionPersonId())) {

                        TUserEntity distributionUserEntity = tUserService.getById(registerDistributionEntity.getDistributionPersonId());
                        if (distributionUserEntity != null && FrUserStatusEnum.ENABLE.equals(distributionUserEntity.getStatus())) {
                            Wrapper<DistributionAmountEntity> distributionAmountEntityWrapperLast = new QueryWrapper<DistributionAmountEntity>().lambda()
                                    .eq(DistributionAmountEntity::getUserId, distributionUserEntity.getId())
                                    .in(DistributionAmountEntity::getReason, "4", "6")
                                    .eq(DistributionAmountEntity::getOrderId, userApplyOrderEntity.getId());
                            if (distributionAmountService.count(distributionAmountEntityWrapperLast) == 0) {
                                DistributionAmountEntity distributionAmountEntity = new DistributionAmountEntity();
                                distributionAmountEntity.setUserId(distributionUserEntity.getId());
                                distributionAmountEntity.setBody("1".equals(rewardSendVo.getType()) ? "邀请用户被解锁微信" : "邀请用户被私聊");
                                distributionAmountEntity.setOrderId(userApplyOrderEntity.getId());
                                distributionAmountEntity.setReason("1".equals(rewardSendVo.getType()) ? "4" : "6");
                                distributionAmountEntity.setType("1");
                                distributionAmountEntity.setPayUserId(userApplyOrderEntity.getUserId());
                                BigDecimal ratio = new BigDecimal("0");
                                String prizeRatioConfig = msfConfigService.getValueByCode("prizeRatio");
                                if (StringUtils.isNotBlank(prizeRatioConfig)) {
                                    JSONObject jsonObject = JSONObject.parseObject(prizeRatioConfig);
                                    ratio = jsonObject.getBigDecimal("nextWechat");
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


            }

        }

    }

    @Transactional
    @Override
    public void reNewVip(String userId) {

        if (service == null) {
            log.warn("微信支付未启用，跳过续费订单校验");
            return;
        }

        UserApplyOrderService userApplyOrderService = SpringUtils.getBean(UserApplyOrderService.class);
        List<UserApplyOrderEntity> userApplyOrderEntityList = userApplyOrderService.list(new QueryWrapper<UserApplyOrderEntity>().lambda()
                .eq(UserApplyOrderEntity::getUserId, userId)
                .eq(UserApplyOrderEntity::getStatus, "1")
                .eq(UserApplyOrderEntity::getType, "1"));
        UserVipService userVipService = SpringUtils.getBean(UserVipService.class);
        for (UserApplyOrderEntity userApplyOrderEntity : userApplyOrderEntityList) {

            QueryOrderByOutTradeNoRequest queryOrderByOutTradeNoRequest = new QueryOrderByOutTradeNoRequest();
            queryOrderByOutTradeNoRequest.setOutTradeNo(userApplyOrderEntity.getOutTradeNo());
            queryOrderByOutTradeNoRequest.setMchid(ConfigConstants.PAY_WX_APPLET_MCHID());
            Transaction transaction = service.queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest);
            if (transaction != null && Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                NotifyData request = new NotifyData();
                request.setOutTradeNo(userApplyOrderEntity.getOutTradeNo());
                request.setPlatformTradeNo(transaction.getTransactionId());

                String vipPrice = msfConfigService.getValueByCode("vipPrice");
                if (StringUtils.isBlank(vipPrice)) {
                    continue;
                }
                List<VipPriceVo> vipPriceVoList = JSONObject.parseArray(vipPrice, VipPriceVo.class);
                VipPriceVo vipPriceVo = vipPriceVoList.stream().filter(p -> p.getPrice().compareTo(userApplyOrderEntity.getAmount()) == 0).findFirst().orElse(null);
                if (vipPriceVo == null) {
                    continue;
                }
                Map<String, String> attach = new HashMap<>();
                attach.put("month", vipPriceVo.getMonth().toString());
                request.setAttach(attach);

                RLock lock = redissonClient.getLock(request.getOutTradeNo());
                try {
                    lock.lock(15, TimeUnit.SECONDS);
                    ServiceR<Void> ret = userVipService.addVipNotify(request);
                    if (ServiceR.isError(ret)) {
                        log.error("添加vip失败：{}", ret.getMsg());
                        continue;
                    }
                } finally {
                    lock.unlock();
                }
                log.info("添加vip成功：{}", request.getOutTradeNo());
            } else if (transaction != null && (Arrays.asList(Transaction.TradeStateEnum.CLOSED,
                    Transaction.TradeStateEnum.REFUND,
                    Transaction.TradeStateEnum.NOTPAY,
                    Transaction.TradeStateEnum.REVOKED,
                    Transaction.TradeStateEnum.PAYERROR
            ).contains(transaction.getTradeState()))) {
                //订单已取消了
                userApplyOrderService.update(new UpdateWrapper<UserApplyOrderEntity>().lambda()
                        .eq(UserApplyOrderEntity::getId, userApplyOrderEntity.getId())
                        .set(UserApplyOrderEntity::getStatus, "3"));
            }
        }
    }

    @Transactional
    @Override
    public void addAmount(String userId) {

        if (service == null) {
            log.warn("微信支付未启用，跳过充值订单校验");
            return;
        }

        UserApplyOrderService userApplyOrderService = SpringUtils.getBean(UserApplyOrderService.class);
        List<UserApplyOrderEntity> userApplyOrderEntityList = userApplyOrderService.list(new QueryWrapper<UserApplyOrderEntity>().lambda()
                .eq(UserApplyOrderEntity::getUserId, userId)
                .eq(UserApplyOrderEntity::getStatus, "1")
                .eq(UserApplyOrderEntity::getType, "2"));
        UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
        for (UserApplyOrderEntity userApplyOrderEntity : userApplyOrderEntityList) {

            QueryOrderByOutTradeNoRequest queryOrderByOutTradeNoRequest = new QueryOrderByOutTradeNoRequest();
            queryOrderByOutTradeNoRequest.setOutTradeNo(userApplyOrderEntity.getOutTradeNo());
            queryOrderByOutTradeNoRequest.setMchid(ConfigConstants.PAY_WX_APPLET_MCHID());
            Transaction transaction = service.queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest);
            if (transaction != null && Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                NotifyData request = new NotifyData();
                request.setOutTradeNo(userApplyOrderEntity.getOutTradeNo());
                request.setPlatformTradeNo(transaction.getTransactionId());
                String addApply = msfConfigService.getValueByCode("addApply");
                List<JSONObject> jsonObjects = JSON.parseArray(addApply, JSONObject.class);
                // 查询出key 为price 的值等于 request.getPrice()的对象
                JSONObject jsonObject = jsonObjects.stream().filter(o -> o.getBigDecimal("price").compareTo(userApplyOrderEntity.getAmount()) == 0).findFirst().orElse(null);
                if (jsonObject == null) {
                    continue;
                }
                Map<String, String> attach = new HashMap<>();
                attach.put("amount", jsonObject.getString("num"));
                request.setAttach(attach);
                RLock lock = redissonClient.getLock(request.getOutTradeNo());
                try {
                    lock.lock(15, TimeUnit.SECONDS);
                    ServiceR<Void> serviceR = userAccountService.addBalance(request);
                    if (ServiceR.isError(serviceR)) {
                        log.error("充值颜币失败：{}", serviceR.getMsg());
                        continue;
                    }
                } finally {
                    lock.unlock();
                }
                log.info("充值颜币成功：{}", request.getOutTradeNo());
            } else if (transaction != null && (Arrays.asList(Transaction.TradeStateEnum.CLOSED,
                    Transaction.TradeStateEnum.REFUND,
                    Transaction.TradeStateEnum.NOTPAY,
                    Transaction.TradeStateEnum.REVOKED,
                    Transaction.TradeStateEnum.PAYERROR
            ).contains(transaction.getTradeState()))) {
                //订单已取消了
                userApplyOrderService.update(new UpdateWrapper<UserApplyOrderEntity>().lambda()
                        .eq(UserApplyOrderEntity::getId, userApplyOrderEntity.getId())
                        .set(UserApplyOrderEntity::getStatus, "3"));
            }
        }
    }

    @Override
    public void addMessageList(String userId) {
        String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");
        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity tUserEntity = tUserService.getById(adminPhoneList);
        IMessageService messageService = SpringUtils.getBean(IMessageService.class);
        BaseMessageInfo messageInfo = new BaseMessageInfo();
        messageInfo.setMessageFormat("text");
        messageInfo.setSendUserId(adminPhoneList);
        messageInfo.setAcceptUserId(userId);
        messageInfo.setSendName(tUserEntity.getNickName());
        messageInfo.setSendPortrait(tUserEntity.getAvatar());
        messageInfo.setTempMsgNo(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setMsgType(MsgSendType.INNER_MSG);
        messageInfo.setContent("您好，欢迎来到有趣的搭子，我是您的专属客服，有什么问题都可以向我咨询");
        messageService.send(messageInfo);
    }

    @Transactional
    @Override
    public void setArticleRecommend(ArticleEntity articleEntity) {

        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity frUserEntity = tUserService.getById(articleEntity.getUserId());
        if (frUserEntity == null) {
            return;
        }
        Wrapper<TUserEntity> wrapper = new QueryWrapper<TUserEntity>().lambda()
                .eq(TUserEntity::getOpenId, frUserEntity.getOpenId());
        List<TUserEntity> list = tUserService.list(wrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> userIds = list.stream().map(TUserEntity::getId).collect(Collectors.toList());
            String testUserShare = msfConfigService.getValueByCode("testUserShare");
            RegisterDistributionService registerDistributionService = SpringUtils.getBean(RegisterDistributionService.class);
            Wrapper<RegisterDistributionEntity> wrapper1 = new QueryWrapper<RegisterDistributionEntity>().lambda()
                    .eq(RegisterDistributionEntity::getDistributionPersonId, testUserShare)
                    .in(RegisterDistributionEntity::getUserId, userIds);
            long count = registerDistributionService.count(wrapper1);
            if (count > 0) {
                articleEntity.setRecommend("0");
                ArticleService articleService = SpringUtils.getBean(ArticleService.class);
                articleService.updateById(articleEntity);
            }
        }
    }

}
