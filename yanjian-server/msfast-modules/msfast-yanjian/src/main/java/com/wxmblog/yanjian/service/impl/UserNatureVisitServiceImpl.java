package com.wxmblog.yanjian.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.service.TUserService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserNatureVisitDao;
import com.wxmblog.yanjian.entity.UserNatureVisitEntity;
import com.wxmblog.yanjian.service.UserNatureVisitService;
import org.springframework.transaction.annotation.Transactional;


@Service("userNatureVisitService")
public class UserNatureVisitServiceImpl extends ServiceImpl<UserNatureVisitDao, UserNatureVisitEntity> implements UserNatureVisitService {

    @Transactional
    @Override
    public void executeDeleteNatureVisit() {
        this.getBaseMapper().deleteNatureVisit();
        this.getBaseMapper().updateNatureVisit();
    }
}
