package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.*;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.UserApplyAuditRequest;
import com.wxmblog.yanjian.common.rest.response.front.apply.ApplyWechatPreResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserApplyPageResponse;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import com.wxmblog.yanjian.common.rest.vo.UserLocationVo;
import com.wxmblog.yanjian.common.utils.LocationAnalysisUtils;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserApplyDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("userApplyService")
@Slf4j
public class UserApplyServiceImpl extends ServiceImpl<UserApplyDao, UserApplyEntity> implements UserApplyService {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserVipService userVipService;

    @Autowired
    private MsfConfigService msfConfigService;


    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    AsyncService asyncService;

    @Autowired
    private UserChatService userChatService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    UserApplyOrderService userApplyOrderService;

    @Resource
    private RedisService redisService;

    @Transactional
    @RedissonLock(lockName = "applyAuditLock:", isLockNameAppendOwner = true)
    @Override
    public void audit(UserApplyAuditRequest request) {

        UserApplyEntity userApplyEntity = this.getById(request.getAuditId());
        if (userApplyEntity == null) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("申请不存在");
        }
        userApplyEntity.setStatus(request.getResult() ? "2" : "3");
        userApplyEntity.setApplyWaitStatus("0");
        userApplyEntity.setApplyReadStatus("1");
        this.updateById(userApplyEntity);

        if (!request.getResult() && "1".equals(userApplyEntity.getApplyWay())) {
            //用户拒绝 返回金额
            String applyAmount = msfConfigService.getValueByCode("applyAmount");
            BigDecimal price = new BigDecimal(applyAmount);
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(userApplyEntity.getUserId());
            userAccountEntity.setAmount(price);
            userAccountEntity.setSource("UserApplyEntity");
            userAccountEntity.setSourceId(userApplyEntity.getId());
            userAccountEntity.setRemarks("申请微信被拒退回");
            userAccountService.save(userAccountEntity);
        }


        if (request.getResult()) {
            TUserService userService = SpringUtils.getBean(TUserService.class);
            TUserEntity ownerUser = userService.getById(TokenUtils.getOwnerId());
            if (ownerUser == null) {
                throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            }
            List<SmsData> smsDataList = new ArrayList<>();
            smsDataList.add(new SmsData("name", ownerUser.getNickName()));
            MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
            //发送通知
            messageSendServcie.sendMessage(new SendUserMessageVo(userApplyEntity.getUserId(),
                    SendUserMessageTypeEnum.FRIEND,
                    SendUserMessageEnum.APPLY_WECHAT_PASS,
                    "快去登陆小程序看看吧",
                    smsDataList,
                    false
            ));
        }

    }

    @Transactional
    @Override
    public void applyRead(String id) {

        Wrapper<UserApplyEntity> wrapper = new UpdateWrapper<UserApplyEntity>()
                .lambda()
                .eq(UserApplyEntity::getId, id)
                .set(UserApplyEntity::getApplyReadStatus, "1")
                .set(UserApplyEntity::getApplyWaitStatus, "0");
        this.update(wrapper);
    }

    @Transactional
    @Override
    public void readwait(String id) {

        Wrapper<UserApplyEntity> wrapper = new UpdateWrapper<UserApplyEntity>()
                .lambda()
                .eq(UserApplyEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserApplyEntity::getApplyUserId, id)
                .set(UserApplyEntity::getApplyWaitStatus, "1");
        this.update(wrapper);
    }

    @Override
    public long getApplyMeCount(String ownerId) {
        return this.getBaseMapper().getApplyMeCount(ownerId);
    }

    @Override
    public long getApplyWaitCount(String ownerId) {
        return this.getBaseMapper().getApplyWaitCount(ownerId);
    }

    @Override
    public ServiceR<ApplyWechatPreResponse> wechatPre(String userId) {

        if (userId.equals(TokenUtils.getOwnerId())) {
            return ServiceR.fail(UserExceptionEnum.APPLY_SELF_EXCEPTION);
        }

        ApplyWechatPreResponse response = new ApplyWechatPreResponse();
        response.setBalance(userAccountService.getBalance(TokenUtils.getOwnerId()));
        response.setIsVip(userVipService.isVip(TokenUtils.getOwnerId()));

        String applyAmount = msfConfigService.getValueByCode("applyAmount");
        if (StringUtils.isNotBlank(applyAmount)) {
            response.setPrice(Integer.parseInt(applyAmount));
        }

        if (Boolean.TRUE.equals(response.getIsVip())) {
            String vipApplyLimit = msfConfigService.getValueByCode("vipApplyLimit");
            if (StringUtils.isNotBlank(vipApplyLimit)) {
                Wrapper<UserApplyEntity> userApplyEntityWrapperCount = new QueryWrapper<UserApplyEntity>().lambda()
                        .eq(UserApplyEntity::getUserId, TokenUtils.getOwnerId())
                        .eq(UserApplyEntity::getApplyWay, "2")
                        .ge(UserApplyEntity::getCreateTime, DateUtil.beginOfDay(new Date()));

                long count = this.count(userApplyEntityWrapperCount);
                response.setVipCount(Long.parseLong(vipApplyLimit) - count);
            }
        } else {
            response.setVipCount(0L);
        }

        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        TUserEntity tUserEntity = tUserService.getById(userId);
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
        response.setWechatOpen(Boolean.TRUE.equals(userProfileEntity.getWechatOpen()));
        return ServiceR.ok(response);
    }

    @Transactional
    @Override
    public void executeUpdateStatus() {

        Integer pageIndex = 1;
        Integer pageSize = 100;
        Boolean isTrue = true;
        do {
            log.info("executeUpdateStatus pageIndex:{}", pageIndex);
            Page<String> page = PageHelper.startPage(pageIndex, pageSize);
            this.getBaseMapper().getTimeOutApplyList();
            PageResult<String> result = new PageResult<>(page);
            if (result.getRows().size() < pageSize) {
                isTrue = false;
            }
            pageIndex++;

            for (String id : result.getRows()) {
                UserApplyEntity userApplyEntity = this.getById(id);
                Date createTime = userApplyEntity.getCreateTime();
                Calendar sourceCal = Calendar.getInstance();
                sourceCal.setTime(createTime);
                // 获取今天的日期
                Calendar todayCal = Calendar.getInstance();
                todayCal.setTime(new Date());

                // 替换时分秒
                todayCal.set(Calendar.HOUR_OF_DAY, sourceCal.get(Calendar.HOUR_OF_DAY));
                todayCal.set(Calendar.MINUTE, sourceCal.get(Calendar.MINUTE));
                todayCal.set(Calendar.SECOND, sourceCal.get(Calendar.SECOND));
                todayCal.set(Calendar.MILLISECOND, sourceCal.get(Calendar.MILLISECOND));
                Date today = new Date();
                long seconds = DateUtil.between(today, todayCal.getTime(), DateUnit.SECOND);

                if (seconds > 60) {
                    ThreadUtil.getInstance().scheduledThreadPool.schedule(() -> {
                        userApplyOrderService.updateTimeOut(userApplyEntity.getId(), userApplyEntity.getUserId());
                    }, seconds, TimeUnit.SECONDS);
                } else {
                    userApplyOrderService.updateTimeOut(userApplyEntity.getId(), userApplyEntity.getUserId());
                }
            }

        } while (isTrue);
        log.info("executeUpdateStatus end");

    }
}
