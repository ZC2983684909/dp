package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.common.rest.response.BaseUserInfo;
import com.wxmblog.base.websocket.service.IImService;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-02-10 13:58
 **/

@Service
public class IImServiceImpl implements IImService {

    @Autowired
    TUserService frUserService;

    @Override
    public BaseUserInfo getImUser(String userId) {
        BaseUserInfo imUserInfoResponse = new BaseUserInfo();
        TUserEntity frUserEntity = frUserService.getById(userId);
        if (frUserEntity != null) {
            imUserInfoResponse = new BaseUserInfo();
            imUserInfoResponse.setId(frUserEntity.getId());
            imUserInfoResponse.setNickName(frUserEntity.getNickName());
            imUserInfoResponse.setSex(frUserEntity.getSex());
            imUserInfoResponse.setHeadPortrait(frUserEntity.getAvatar());
        }
        return imUserInfoResponse;
    }

    @Override
    public List<BaseUserInfo> getImUserList(List<String> userIdList) {
        if (CollectionUtil.isEmpty(userIdList)) {
            return Collections.emptyList();
        }
        List<TUserEntity> frUserEntities = frUserService.listByIds(userIdList);
        return frUserEntities.stream().map(p -> {
            BaseUserInfo imUserInfoResponse = new BaseUserInfo();
            imUserInfoResponse.setId(p.getId());
            imUserInfoResponse.setNickName(p.getNickName());
            imUserInfoResponse.setSex(p.getSex());
            imUserInfoResponse.setHeadPortrait(p.getAvatar());
            return imUserInfoResponse;
        }).collect(Collectors.toList());
    }

}
