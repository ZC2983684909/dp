package com.wxmblog.yanjian.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.rest.response.front.user.SignInResponse;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.SignInDao;
import com.wxmblog.yanjian.entity.SignInEntity;
import com.wxmblog.yanjian.service.SignInService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Service("signInService")
public class SignInServiceImpl extends ServiceImpl<SignInDao, SignInEntity> implements SignInService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TUserService tUserService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public SignInResponse getStatus() {

        Wrapper<SignInEntity> wrapper = new QueryWrapper<SignInEntity>().lambda()
                .eq(SignInEntity::getUserId, TokenUtils.getOwnerId())
                .eq(SignInEntity::getStatus, "0");
        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setCount(this.count(wrapper));
        return signInResponse;
    }

    @Transactional
    @Override
    public void add(String userId) {
        RLock lock = redissonClient.getLock("SIGN:" + userId);
        try {
            lock.lock(15, TimeUnit.SECONDS);
            addSign(userId);
        } finally {
            lock.unlock();
        }

    }

    @Transactional
    public void addSign(String userId) {

        //查询当天是否签到过
        Wrapper<SignInEntity> wrapper = new QueryWrapper<SignInEntity>().lambda()
                .eq(SignInEntity::getUserId, userId)
                .ge(SignInEntity::getModifyTime, DateUtils.getStartTimeOfDay(new Date()));
        if (this.count(wrapper) > 0) {
            return;
        }
        Wrapper<SignInEntity> wrapper1 = new QueryWrapper<SignInEntity>().lambda()
                .eq(SignInEntity::getUserId, userId)
                .eq(SignInEntity::getStatus, "0");
        if (this.count(wrapper1) >= 7) {
            return;
        }
        SignInEntity signInEntity = new SignInEntity();
        signInEntity.setUserId(userId);
        signInEntity.setStatus("0");
        this.save(signInEntity);

    }

    @Transactional
    @Override
    public void exchange() {

        RLock lock = redissonClient.getLock("SIGN:" + TokenUtils.getOwnerId());
        try {
            lock.lock(15, TimeUnit.SECONDS);

            Wrapper<SignInEntity> wrapper1 = new QueryWrapper<SignInEntity>().lambda()
                    .eq(SignInEntity::getUserId, TokenUtils.getOwnerId())
                    .eq(SignInEntity::getStatus, "0");
            if (this.count(wrapper1) < 7) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("签到不足7天");
            }
            Wrapper<SignInEntity> wrapper = new UpdateWrapper<SignInEntity>().lambda()
                    .eq(SignInEntity::getUserId, TokenUtils.getOwnerId())
                    .set(SignInEntity::getStatus, "1");
            this.update(wrapper);
            TUserEntity tUserEntity = tUserService.getById(TokenUtils.getOwnerId());
            if (tUserEntity != null) {

                UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                userProfileEntity.setApplyCount(userProfileEntity.getApplyCount() + 1);
                userProfileService.updateById(userProfileEntity);
            }

        } finally {
            lock.unlock();
        }
    }
}
