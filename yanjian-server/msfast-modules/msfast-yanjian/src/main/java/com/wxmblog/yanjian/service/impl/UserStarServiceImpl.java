package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserStarResponse;
import com.wxmblog.yanjian.common.rest.vo.UserLocationVo;
import com.wxmblog.yanjian.common.utils.LocationAnalysisUtils;
import com.wxmblog.yanjian.service.AsyncService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserStarDao;
import com.wxmblog.yanjian.entity.UserStarEntity;
import com.wxmblog.yanjian.service.UserStarService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("userStarService")
public class UserStarServiceImpl extends ServiceImpl<UserStarDao, UserStarEntity> implements UserStarService {

    @Autowired
    private AsyncService asyncService;

    @Resource
    private RedisService redisService;


    @Transactional
    @Override
    public void read(String id) {

        Wrapper<UserStarEntity> wrapper = new UpdateWrapper<UserStarEntity>().lambda()
                .eq(UserStarEntity::getUserId, id)
                .eq(UserStarEntity::getStarUserId, TokenUtils.getOwnerId())
                .set(UserStarEntity::getReadStatus, "1");
        this.update(wrapper);
    }

    @Override
    public long getCount(String userId) {
        return this.getBaseMapper().getCount(userId);
    }
}
