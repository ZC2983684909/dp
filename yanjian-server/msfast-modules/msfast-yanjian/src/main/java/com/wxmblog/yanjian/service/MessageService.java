package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.yanjian.common.rest.request.front.article.AddMessageRequest;
import com.wxmblog.yanjian.common.rest.request.front.article.MessagePageRequest;
import com.wxmblog.yanjian.common.rest.response.front.article.MessagePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.article.UserMessageReadResponse;
import com.wxmblog.yanjian.entity.MessageEntity;
import org.springframework.scheduling.annotation.Async;


/**
 * 消息 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-14 16:20:16
 */
public interface MessageService extends IService<MessageEntity> {

    @Async
    void addMessage(AddMessageRequest request);


    UserMessageReadResponse unreadCount();
}

