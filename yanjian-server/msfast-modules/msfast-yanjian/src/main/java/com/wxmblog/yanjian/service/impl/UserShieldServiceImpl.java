package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserShieldDao;
import com.wxmblog.yanjian.entity.UserShieldEntity;
import com.wxmblog.yanjian.service.UserShieldService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;


@Service("userShieldService")
public class UserShieldServiceImpl extends ServiceImpl<UserShieldDao, UserShieldEntity> implements UserShieldService {

    @Override
    public PageResult<UserInfoPageResponse> getPage(Integer pageIndex, Integer pageSize) {

        UserStarRequest request = new UserStarRequest();
        request.setOwnerId(TokenUtils.getOwnerId());
        Page<UserInfoPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().getPage(request);
        PageResult<UserInfoPageResponse> result = new PageResult<>(page);
        result.getRows().forEach(userInfoPageResponse -> {
            if (CollectionUtil.isNotEmpty(userInfoPageResponse.getPersonalPhoto())) {
                userInfoPageResponse.setPhoto(userInfoPageResponse.getPersonalPhoto().get(0).getUrl());
                userInfoPageResponse.setSimilarity(userInfoPageResponse.getPersonalPhoto().get(0).getSimilarity());
            }
            userInfoPageResponse.setPersonalPhoto(null);

            if (userInfoPageResponse.getHeight() != null) {
                userInfoPageResponse.setHeightFormat(userInfoPageResponse.getHeight() + "cm");
            }

            if (userInfoPageResponse.getWeight() != null) {
                userInfoPageResponse.setWeightFormat(userInfoPageResponse.getWeight() + "kg");
            }

            if (userInfoPageResponse.getBirthDate() != null) {
                userInfoPageResponse.setAge(DateUtils.getAgeByBirth(userInfoPageResponse.getBirthDate()));
            }

            if (Boolean.FALSE.equals(userInfoPageResponse.getMainCity()) && StringUtils.isNotBlank(userInfoPageResponse.getCity()) && StringUtils.isNotBlank(userInfoPageResponse.getCounty())) {
                userInfoPageResponse.setCity(userInfoPageResponse.getCity() + userInfoPageResponse.getCounty());
            }
        });

        return result;
    }

    @Transactional
    @Override
    public void shield(String id) {

        if (id.equals(TokenUtils.getOwnerId())) {
            throw new JrsfException(UserExceptionEnum.APPLY_SELF_EXCEPTION);
        }

        Wrapper<UserShieldEntity> wrapper = new QueryWrapper<UserShieldEntity>().lambda()
                .eq(UserShieldEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserShieldEntity::getShieldId, id);
        long count = this.count(wrapper);
        if (count == 0) {
            UserShieldEntity entity = new UserShieldEntity();
            entity.setUserId(TokenUtils.getOwnerId());
            entity.setShieldId(id);
            this.save(entity);
        }
    }

    @Transactional
    @Override
    public void cancel(String id) {
        Wrapper<UserShieldEntity> wrapper = new QueryWrapper<UserShieldEntity>().lambda()
                .eq(UserShieldEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserShieldEntity::getShieldId, id);
        this.remove(wrapper);
    }
}
