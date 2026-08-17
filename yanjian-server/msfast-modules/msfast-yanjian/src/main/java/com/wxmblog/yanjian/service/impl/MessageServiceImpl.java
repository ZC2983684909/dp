package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.rest.request.front.article.AddMessageRequest;
import com.wxmblog.yanjian.common.rest.request.front.article.MessagePageRequest;
import com.wxmblog.yanjian.common.rest.response.front.article.MessagePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.article.UserMessageReadResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.MessageDao;
import com.wxmblog.yanjian.entity.MessageEntity;
import com.wxmblog.yanjian.service.MessageService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {

    @Transactional
    @Override
    public void addMessage(AddMessageRequest request) {
        if (!request.getUserId().equals(request.getSendUserId())) {
            MessageEntity message = new MessageEntity();
            BeanUtils.copyProperties(request, message);
            message.setStatus("0");
            this.save(message);
        }

    }

    @Override
    public UserMessageReadResponse unreadCount() {
        UserMessageReadResponse userMessageReadResponse = new UserMessageReadResponse();
        if (StringUtils.isNotBlank(TokenUtils.getOwnerId())) {
            Wrapper<MessageEntity> wrapper = new UpdateWrapper<MessageEntity>()
                    .lambda()
                    .eq(MessageEntity::getUserId, TokenUtils.getOwnerId())
                    .eq(MessageEntity::getStatus, "0")
                    .in(MessageEntity::getType, "1", "2");
            userMessageReadResponse.setCommentCount(this.count(wrapper));

            Wrapper<MessageEntity> wrapper1 = new UpdateWrapper<MessageEntity>()
                    .lambda()
                    .eq(MessageEntity::getUserId, TokenUtils.getOwnerId())
                    .eq(MessageEntity::getStatus, "0")
                    .in(MessageEntity::getType, "3", "4");
            userMessageReadResponse.setLikeCount(this.count(wrapper1));
            userMessageReadResponse.setMessageCount(userMessageReadResponse.getLikeCount() + userMessageReadResponse.getCommentCount());
        }
        return userMessageReadResponse;
    }
}
