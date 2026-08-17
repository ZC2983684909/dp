package com.wxmblog.yanjian.service.impl;

import com.wxmblog.base.common.constant.Constants;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.NumberUtils;
import com.wxmblog.base.websocket.common.enums.MsgSendType;
import com.wxmblog.base.websocket.common.rest.request.BaseMessageInfo;
import com.wxmblog.base.websocket.service.impl.IImtServiceImpl;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import com.wxmblog.yanjian.service.MessageSendServcie;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-01-10 11:08
 **/
@Service
public class ImtServiceImpl extends IImtServiceImpl {

    @Autowired
    MessageSendServcie messageSendServcie;

    @Autowired
    RedisService redisService;


    @Override
    public void sendMessageAfter(BaseMessageInfo messageInfo) {

        if (MsgSendType.INNER_MSG.equals(messageInfo.getMsgType())) {
            return;
        }
        String onlineUser = redisService.getCacheObject(Constants.SOCKET_USER_ONLINE + messageInfo.getAcceptUserId());
        if (StringUtils.isBlank(onlineUser)) {
            //不在线才发送
            LocalDateTime endTime = LocalDateTime.now().plusMinutes(3);
            long executeNum = NumberUtils.getValueOpsByCode("msgPublicNotice:" + messageInfo.getAcceptUserId(), endTime);
            if (executeNum > 1) {
                //不再重复发送
                return;
            }
            //发送微信通知
            List<SmsData> smsDataList = new ArrayList<>();
            smsDataList.add(new SmsData("name", messageInfo.getSendName()));
            messageSendServcie.sendMessage(new SendUserMessageVo(
                    messageInfo.getAcceptUserId(),
                    SendUserMessageTypeEnum.MESSAGE,
                    SendUserMessageEnum.CHAT,
                    "快去看看ta说了什么吧!",
                    smsDataList, false
            ));

        }
    }

    @Override
    public void connectSuccessAfter(String userId) {
        redisService.setCacheObject(Constants.SOCKET_USER_ONLINE + userId, userId, 1L, TimeUnit.DAYS);
    }

    @Override
    public void close(String userId) {
        redisService.deleteObject(Constants.SOCKET_USER_ONLINE + userId);
    }
}
