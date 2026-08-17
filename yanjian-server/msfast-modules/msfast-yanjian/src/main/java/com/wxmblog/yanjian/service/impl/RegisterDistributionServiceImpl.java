package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.RegisterDistributionDao;
import com.wxmblog.yanjian.entity.RegisterDistributionEntity;
import com.wxmblog.yanjian.service.RegisterDistributionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service("registerDistributionService")
@Slf4j
public class RegisterDistributionServiceImpl extends ServiceImpl<RegisterDistributionDao, RegisterDistributionEntity> implements RegisterDistributionService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public Long indirectCount(String userId) {
        return this.getBaseMapper().indirectCount(userId);
    }

    @Transactional
    @Override
    public void sendAward(String userId) {

        RLock lock = redissonClient.getLock("SENDAWARD:" + userId);
        try {
            lock.lock(15, TimeUnit.SECONDS);

            Wrapper<RegisterDistributionEntity> wrapper = new QueryWrapper<RegisterDistributionEntity>().lambda()
                    .eq(RegisterDistributionEntity::getUserId, userId)
                    .eq(RegisterDistributionEntity::getType, "2")
                    .orderByDesc(RegisterDistributionEntity::getCreateTime)
                    .last("limit 1");
            RegisterDistributionEntity registerDistributionEntity = this.getOne(wrapper);
            if (registerDistributionEntity != null) {

                //查看是否奖励过
                if ("0".equals(registerDistributionEntity.getRewardStatus())) {
                    TUserService tUserService = SpringUtils.getBean(TUserService.class);
                    TUserEntity tUserEntity = tUserService.getById(userId);
                    log.info("开始发放奖励用户信息：" + JSON.toJSONString(tUserEntity));

                    if (tUserEntity != null && "3".equals(tUserEntity.getPhotoAuth()) && "3".equals(tUserEntity.getIdAuth()) && "3".equals(tUserEntity.getEduAuth())) {

                        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                        userProfileEntity.setApplyCount(userProfileEntity.getApplyCount() + 3);
                        tUserService.updateById(tUserEntity);

                        TUserEntity tUserEntityLast = tUserService.getById(registerDistributionEntity.getDistributionPersonId());
                        if (tUserEntityLast != null) {
                            UserProfileEntity userProfileEntityLast = userProfileService.getById(tUserEntityLast.getProfileId());
                            userProfileEntityLast.setApplyCount(userProfileEntityLast.getApplyCount() + 3);
                            tUserService.updateById(tUserEntityLast);
                        }
                        registerDistributionEntity.setRewardStatus("1");
                        this.updateById(registerDistributionEntity);
                    }


                }

                if ("0".equals(registerDistributionEntity.getHigherRewardStatus())) {
                    //查询上级的上级是否可以领取奖励

                    Wrapper<RegisterDistributionEntity> wrapperHigher = new QueryWrapper<RegisterDistributionEntity>().lambda()
                            .eq(RegisterDistributionEntity::getUserId, registerDistributionEntity.getDistributionPersonId())
                            .eq(RegisterDistributionEntity::getType, "2")
                            .orderByDesc(RegisterDistributionEntity::getCreateTime)
                            .last("limit 1");
                    RegisterDistributionEntity registerDistributionEntityHigher = this.getOne(wrapperHigher);
                    List<String> registerDistributionList = this.getBaseMapper().getRegisterDistribution(registerDistributionEntityHigher.getDistributionPersonId());
                    if (registerDistributionList.size() >= 3) {
                        int awardCount = registerDistributionList.size() / 3;
                        TUserService tUserService = SpringUtils.getBean(TUserService.class);
                        TUserEntity tUserEntity = tUserService.getById(registerDistributionEntityHigher.getDistributionPersonId());
                        if (tUserEntity != null) {
                            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                            userProfileEntity.setApplyCount(userProfileEntity.getApplyCount() + (awardCount * 3));
                            tUserService.updateById(tUserEntity);
                        }
                        List<String> updateList = registerDistributionList.subList(0, (awardCount * 3));
                        if (CollectionUtil.isNotEmpty(updateList)) {
                            Wrapper<RegisterDistributionEntity> distributionEntityWrapper = new UpdateWrapper<RegisterDistributionEntity>().lambda()
                                    .in(RegisterDistributionEntity::getId, updateList)
                                    .set(RegisterDistributionEntity::getHigherRewardStatus, "1");
                            this.update(distributionEntityWrapper);
                        }

                    }

                }

            }


        } finally {
            lock.unlock();
        }
    }

    @Override
    public Long distributionCount(String ownerId) {
        return this.getBaseMapper().distributionCount(ownerId);
    }
}
