package com.wxmblog.yanjian.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.yanjian.common.rest.vo.AddNatureVisitVo;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserNatureVisitEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserNatureVisitService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserVisitDao;
import com.wxmblog.yanjian.entity.UserVisitEntity;
import com.wxmblog.yanjian.service.UserVisitService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Service("userVisitService")
public class UserVisitServiceImpl extends ServiceImpl<UserVisitDao, UserVisitEntity> implements UserVisitService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private UserNatureVisitService userNatureVisitService;

    @Transactional
    @Override
    @RedissonLock(lockName = "addVisit:", isLockNameAppendOwner = true)
    public void addVisit(String userId, String visitUserId) {
        Wrapper<UserVisitEntity> userVisitEntityWrapper = new QueryWrapper<UserVisitEntity>().lambda()
                .eq(UserVisitEntity::getUserId, userId)
                .eq(UserVisitEntity::getVisitUserId, visitUserId)
                .orderByDesc(UserVisitEntity::getModifyTime)
                .last("limit 1");
        UserVisitEntity userVisitEntity = this.getOne(userVisitEntityWrapper);
        if (userVisitEntity != null) {
            userVisitEntity.setNum(userVisitEntity.getNum() + 1);
            userVisitEntity.setStatus("0");
            this.updateById(userVisitEntity);
        } else {
            userVisitEntity = new UserVisitEntity();
            userVisitEntity.setUserId(userId);
            userVisitEntity.setVisitUserId(visitUserId);
            userVisitEntity.setNum(1);
            userVisitEntity.setModifyTime(new Date());
            userVisitEntity.setStatus("0");
            this.save(userVisitEntity);
        }

    }

    @Transactional
    @RedissonLock(lockName = "addNatureVisit:", lockNameAppendField = "visitUserId")
    @Override
    public void addNatureVisit(AddNatureVisitVo addNatureVisitVo) {
        TUserService tUserService = SpringUtils.getBean(TUserService.class);
        Wrapper<UserNatureVisitEntity> userNatureVisitEntityWrapper = new QueryWrapper<UserNatureVisitEntity>().lambda()
                .eq(UserNatureVisitEntity::getUserId, addNatureVisitVo.getUserId())
                .eq(UserNatureVisitEntity::getVisitUserId, addNatureVisitVo.getVisitUserId())
                .eq(UserNatureVisitEntity::getType, "1")
                .orderByDesc(UserNatureVisitEntity::getModifyTime)
                .last("limit 1");
        UserNatureVisitEntity userNatureVisitEntity = userNatureVisitService.getOne(userNatureVisitEntityWrapper);
        if (userNatureVisitEntity == null) {
            userNatureVisitEntity = new UserNatureVisitEntity();
            userNatureVisitEntity.setUserId(addNatureVisitVo.getUserId());
            userNatureVisitEntity.setVisitUserId(addNatureVisitVo.getVisitUserId());
            userNatureVisitEntity.setNum(1);
            userNatureVisitEntity.setType("1");
            userNatureVisitService.save(userNatureVisitEntity);
        } else {
            userNatureVisitEntity.setNum(userNatureVisitEntity.getNum() + 1);
            userNatureVisitService.updateById(userNatureVisitEntity);
            if (userNatureVisitEntity.getNum() <= 10) {
                TUserEntity tUserEntity = tUserService.getById(addNatureVisitVo.getVisitUserId());
                if (tUserEntity != null) {
                    tUserEntity.setNatureSort(tUserEntity.getNatureSort() + 1);
                    tUserService.updateById(tUserEntity);
                }

            }
        }

        Wrapper<UserNatureVisitEntity> userNatureEntityStarWrapper = new QueryWrapper<UserNatureVisitEntity>().lambda()
                .eq(UserNatureVisitEntity::getUserId, addNatureVisitVo.getUserId())
                .eq(UserNatureVisitEntity::getVisitUserId, addNatureVisitVo.getVisitUserId())
                .eq(UserNatureVisitEntity::getType, "2")
                .orderByDesc(UserNatureVisitEntity::getModifyTime)
                .last("limit 1");
        UserNatureVisitEntity userNatureStarEntity = userNatureVisitService.getOne(userNatureEntityStarWrapper);
        if (userNatureStarEntity == null) {
            userNatureStarEntity = new UserNatureVisitEntity();
            userNatureStarEntity.setUserId(addNatureVisitVo.getUserId());
            userNatureStarEntity.setVisitUserId(addNatureVisitVo.getVisitUserId());
            userNatureStarEntity.setNum(1);
            userNatureStarEntity.setType("2");
            userNatureVisitService.save(userNatureStarEntity);

            TUserEntity tUserEntity = tUserService.getById(addNatureVisitVo.getVisitUserId());
            if (tUserEntity != null) {
                //关注用户权重增加5
                tUserEntity.setNatureSort(tUserEntity.getNatureSort() + 5);
                tUserService.updateById(tUserEntity);
            }
        }

    }
}
