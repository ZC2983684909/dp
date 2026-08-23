package com.wxmblog.base.websocket.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.JwtUtils;
import com.wxmblog.base.common.utils.SpringBeanUtils;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.websocket.common.enums.MessageTypeEnum;
import com.wxmblog.base.websocket.common.rest.request.BaseMessageInfo;
import com.wxmblog.base.websocket.common.rest.request.WebSocketMessage;
import com.wxmblog.base.websocket.service.IMessageService;
import com.wxmblog.base.websocket.service.IWebSocketService;
import com.wxmblog.base.websocket.utils.ChannelUtil;
import com.wxmblog.yanjian.service.UserChatService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 覆盖基础依赖中的 WebSocket 消息处理器，为连接和发信增加登录校验。
 */
public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private static final Map<String, String> CHANNEL_USERS = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext context) {
        IWebSocketService service = SpringBeanUtils.getBean(IWebSocketService.class);
        if (service != null) {
            service.connect(context.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        Channel channel = context.channel();
        String userId = CHANNEL_USERS.remove(channel.id().asLongText());
        ChannelMap.getManager().entrySet().removeIf(entry -> channel.equals(entry.getValue()));
        ChannelMap.getOnline().entrySet().removeIf(entry -> channel.equals(entry.getValue()));
        if (StringUtils.isNotBlank(userId)) {
            IWebSocketService service = SpringBeanUtils.getBean(IWebSocketService.class);
            if (service != null) {
                service.close(userId);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, TextWebSocketFrame frame) {
        Channel channel = context.channel();
        ChannelUtil channelUtil = SpringUtils.getBean(ChannelUtil.class);
        WebSocketMessage socketMessage;
        try {
            socketMessage = JSON.parseObject(frame.text(), WebSocketMessage.class);
        } catch (Exception exception) {
            sendError(channelUtil, channel, "消息格式错误");
            return;
        }

        if (socketMessage == null || socketMessage.getMessageType() == null) {
            sendError(channelUtil, channel, "消息类型不能为空");
            return;
        }

        if (MessageTypeEnum.CONNECT.equals(socketMessage.getMessageType())) {
            connect(channelUtil, channel, socketMessage.getInfo());
            return;
        }

        String authenticatedUserId = CHANNEL_USERS.get(channel.id().asLongText());
        if (StringUtils.isBlank(authenticatedUserId)) {
            sendError(channelUtil, channel, "登录已失效，请重新登录");
            channel.close();
            return;
        }

        if (MessageTypeEnum.ALIVE.equals(socketMessage.getMessageType())) {
            return;
        }

        if (MessageTypeEnum.IM_MESSAGE.equals(socketMessage.getMessageType())) {
            sendChatMessage(channelUtil, channel, authenticatedUserId, socketMessage.getInfo());
            return;
        }

        if (MessageTypeEnum.ANSWER.equals(socketMessage.getMessageType())) {
            if (StringUtils.isNotBlank(socketMessage.getInfo())) {
                SpringUtils.getBean(RedisService.class).deleteObject(socketMessage.getInfo());
            }
            return;
        }

        IWebSocketService service = SpringBeanUtils.getBean(IWebSocketService.class);
        if (service != null) {
            service.read(channel, frame.text());
        }
    }

    private void connect(ChannelUtil channelUtil, Channel channel, String info) {
        try {
            JSONObject connectInfo = JSON.parseObject(info);
            String userId = connectInfo.getString("userId");
            String token = connectInfo.getString("token");
            String tokenUserId = JwtUtils.getUserId(token);
            if (StringUtils.isBlank(userId) || !userId.equals(tokenUserId)) {
                throw new IllegalArgumentException("用户身份不一致");
            }

            CHANNEL_USERS.put(channel.id().asLongText(), userId);
            ChannelMap.put(userId, channel);
            channelUtil.sendText(channel, "CONNECT_SUCCESS");

            IWebSocketService service = SpringBeanUtils.getBean(IWebSocketService.class);
            if (service != null) {
                service.connectSuccessAfter(userId);
            }
        } catch (Exception exception) {
            log.warn("WebSocket 登录校验失败: {}", exception.getMessage());
            sendError(channelUtil, channel, "登录校验失败，请重新登录");
            channel.close();
        }
    }

    private void sendChatMessage(ChannelUtil channelUtil, Channel channel,
                                 String authenticatedUserId, String info) {
        try {
            BaseMessageInfo messageInfo = JSON.parseObject(info, BaseMessageInfo.class);
            if (messageInfo == null || StringUtils.isBlank(messageInfo.getAcceptUserId())
                    || StringUtils.isBlank(messageInfo.getContent())
                    || messageInfo.getContent().length() > 500) {
                sendError(channelUtil, channel, "消息内容不合法");
                return;
            }

            messageInfo.setSendUserId(authenticatedUserId);
            UserChatService userChatService = SpringUtils.getBean(UserChatService.class);
            if (!userChatService.canChat(authenticatedUserId, messageInfo.getAcceptUserId())) {
                sendError(channelUtil, channel, "请先解锁聊天，或对方已将你拉黑");
                return;
            }

            SpringUtils.getBean(IMessageService.class).send(messageInfo);
        } catch (Exception exception) {
            log.warn("WebSocket 消息发送失败: {}", exception.getMessage());
            sendError(channelUtil, channel, "消息发送失败");
        }
    }

    private void sendError(ChannelUtil channelUtil, Channel channel, String message) {
        JSONObject result = new JSONObject();
        result.put("type", "ERROR");
        result.put("message", message);
        channelUtil.sendText(channel, result.toJSONString());
    }
}
