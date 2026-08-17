package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.authority.service.Wxh5Service;
import com.wxmblog.base.auth.common.rest.response.WxH5UserInfoResponse;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.response.front.home.PopUpResponse;
import com.wxmblog.yanjian.entity.PopupEntity;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.PopupService;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.PopupRecordsDao;
import com.wxmblog.yanjian.entity.PopupRecordsEntity;
import com.wxmblog.yanjian.service.PopupRecordsService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("popupRecordsService")
public class PopupRecordsServiceImpl extends ServiceImpl<PopupRecordsDao, PopupRecordsEntity> implements PopupRecordsService {

    @Autowired
    PopupService popupService;

    @Autowired
    TUserService tUserService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    private Wxh5Service wxh5Service;


    @Transactional
    @Override
    public ServiceR<List<PopUpResponse>> popup(String location) {

        if (StringUtils.isBlank(TokenUtils.getOwnerId())) {
            return ServiceR.ok();
        }

        Wrapper<PopupEntity> wrapper = new QueryWrapper<PopupEntity>().lambda()
                .le(PopupEntity::getStartTime, new Date())
                .ge(PopupEntity::getEndTime, new Date())
                .eq(PopupEntity::getLocation, location)
                .orderByDesc(PopupEntity::getCreateTime);
        List<PopupEntity> popupEntities = popupService.list(wrapper);
        List<PopUpResponse> popUpResponses = new ArrayList<>();

        List<String> popupIdList = popupEntities.stream().map(PopupEntity::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(popupIdList)) {
            return ServiceR.ok();
        }
        //查询是否已经弹出过
        Wrapper<PopupRecordsEntity> recordsWrapper = new QueryWrapper<PopupRecordsEntity>().lambda()
                .eq(PopupRecordsEntity::getUserId, TokenUtils.getOwnerId())
                .in(PopupRecordsEntity::getPopupId, popupIdList)
                .orderByDesc(PopupRecordsEntity::getCreateTime);
        List<PopupRecordsEntity> recordsEntityList = this.list(recordsWrapper);

        for (PopupEntity popupEntity : popupEntities) {

            if ("subscribe".equals(popupEntity.getType())) {

                TUserEntity tUserEntity = tUserService.getById(TokenUtils.getOwnerId());
                if (tUserEntity != null && StringUtils.isNotBlank(tUserEntity.getProfileId())) {
                    UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                    if (userProfileEntity != null && StringUtils.isNotBlank(userProfileEntity.getPublicOpenId())) {

                        String openid = userProfileEntity.getPublicOpenId();
                        ServiceR<WxH5UserInfoResponse> ret = wxh5Service.getUserInfoByOpenId(openid);
                        if (ServiceR.isSuccess(ret)) {
                            WxH5UserInfoResponse wxH5UserInfoResponse = ret.getData();
                            if (wxH5UserInfoResponse.getSubscribe() != 0) {
                                continue;
                            }
                        }
                    }
                }
            }

            PopUpResponse popUpResponse = new PopUpResponse();
            popUpResponse.setImage(popupEntity.getImage());
            popUpResponse.setLink(popupEntity.getLink());
            popUpResponse.setAttr(JSON.parseObject(popupEntity.getAttr()));

            PopupRecordsEntity records = recordsEntityList.stream().filter(item -> item.getPopupId().equals(popupEntity.getId())).findFirst().orElse(null);
            boolean isSend = false;
            if (records != null) {
                Date createdTime = records.getCreateTime();
                Integer popupNum = popupEntity.getPopupNum();
                /**
                 * 1.1天/次
                 * 2.周/次
                 * 3.月/次
                 * 4.一次
                 */
                switch (popupNum) {
                    case 1:
                        if (!DateUtil.isSameDay(createdTime, new Date())) {
                            isSend = true;
                        }
                        break;
                    case 2:
                        if (!DateUtil.isSameWeek(createdTime, new Date(), true)) {
                            isSend = true;
                        }
                        break;
                    case 3:
                        if (!DateUtil.isSameMonth(createdTime, new Date())) {
                            isSend = true;
                        }
                        break;
                }
            } else {
                isSend = true;
            }
            if (isSend) {
                popUpResponses.add(popUpResponse);
                PopupRecordsEntity entity = new PopupRecordsEntity();
                entity.setUserId(TokenUtils.getOwnerId());
                entity.setPopupId(popupEntity.getId());
                this.save(entity);
                //添加弹窗记录 下次不再弹出
                return ServiceR.ok(popUpResponses);
            }
        }
        return ServiceR.ok();
    }
}
