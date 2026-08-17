package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.wxmblog.base.auth.authority.service.Wxh5Service;
import com.wxmblog.base.auth.common.rest.request.wx.h5.MiniprogramDataVo;
import com.wxmblog.base.auth.common.rest.request.wx.h5.WxH5TemplateMsgRequest;
import com.wxmblog.base.auth.common.rest.request.wx.WxTemplateData;
import com.wxmblog.base.common.constant.ConfigConstants;
import com.wxmblog.base.common.constant.Constants;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.service.ISendSmsService;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.MessageSendServcie;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wxmblog.yanjian.common.constant.PropertiesConstants;

import java.util.Date;

@Service
public class MessageSendServcieImpl implements MessageSendServcie {

    private static final Logger log = LoggerFactory.getLogger(MessageSendServcieImpl.class);
    @Autowired
    TUserService frUserService;

    @Autowired
    Wxh5Service wxh5Service;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    RedisService redisService;

    @Autowired
    ISendSmsService sendSmsService;

    @Override
    public void sendMessage(SendUserMessageVo request) {

        String success = redisService.getCacheObject(Constants.SOCKET_USER_ONLINE + request.getUserId());
        if (StringUtils.isNotBlank(success)) {
            return;
        }

        WxH5TemplateMsgRequest wxH5TemplateMsgRequest = new WxH5TemplateMsgRequest();
        wxH5TemplateMsgRequest.setTemplateId(PropertiesConstants.Wx_Template());
        TUserEntity frUserEntity = frUserService.getById(request.getUserId());
        if (frUserEntity != null) {
            if ("1".equals(frUserEntity.getInvisible()) || !FrUserStatusEnum.ENABLE.equals(frUserEntity.getStatus())) {
                //用户隐身不发送消息
                return;
            }
            UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
            if (userProfileEntity == null || StringUtils.isBlank(userProfileEntity.getPublicOpenId())) {
                //没有公众号id 发送短信
                sendSms(frUserEntity.getPhone(), request);
                return;
            }
            wxH5TemplateMsgRequest.setTouser(userProfileEntity.getPublicOpenId());
            wxH5TemplateMsgRequest.setData(Lists.newArrayList(
                    new WxTemplateData("thing19", request.getType().getDesc()),
                    new WxTemplateData("thing6", request.getTitle().getTitle()),
                    new WxTemplateData("thing65", request.getContent().length() > 20 ? request.getContent().substring(0, 20) : request.getContent()),
                    new WxTemplateData("time22", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
            ));
            MiniprogramDataVo miniprogramDataVo = new MiniprogramDataVo();
            miniprogramDataVo.setAppid(ConfigConstants.WX_APPLET_APPID());
            miniprogramDataVo.setPagepath("pages/index/index");
            wxH5TemplateMsgRequest.setMiniprogram(miniprogramDataVo);
            try {
                wxh5Service.sendWxTemplateMessage(wxH5TemplateMsgRequest);
            } catch (Exception e) {
                log.info("发送模板消息失败{}", e.getMessage());
                sendSms(frUserEntity.getPhone(), request);
                return;
            }

            long day = DateUtil.between(frUserEntity.getLatelyTime(), new Date(), DateUnit.DAY);
            if ((day < 60 && day >= 3) || Boolean.TRUE.equals(request.getSmsAlways())) {
                sendSms(frUserEntity.getPhone(), request);
            }
        }
    }

    private void sendSms(String phone, SendUserMessageVo request) {
        if (StringUtils.isNotBlank(phone)
                && request.getTitle() != null
                && StringUtils.isNotBlank(request.getTitle().getSmsCode())) {
            try {
                if (request.getSmsDataList() == null) {
                    request.setSmsDataList(Lists.newArrayList());
                }
                request.getSmsDataList().add(new SmsData("signName", "查看"));
                if (ListUtil.of(SendUserMessageEnum.APPLY_WECHAT,
                        SendUserMessageEnum.APPLY_WECHAT_PASS,
                        SendUserMessageEnum.CHAT,
                        SendUserMessageEnum.UNLOCK_PRIVATE_MESSAGE,
                        SendUserMessageEnum.WECHAR_APPLY_NOT_PROCESS).contains(request.getTitle())) {
                    sendSmsService.sendSms(phone, request.getSmsDataList(), request.getTitle().getSmsCode());
                }

            } catch (Exception e) {
                log.info("发送短信失败{}", e.getMessage());
            }

        }
    }
}
