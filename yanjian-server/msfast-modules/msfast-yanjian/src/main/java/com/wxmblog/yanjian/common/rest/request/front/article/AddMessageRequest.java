package com.wxmblog.yanjian.common.rest.request.front.article;

import lombok.Data;

@Data
public class AddMessageRequest {

    private String userId;

    /**
     * 动态id
     */
    private String articleId;
    /**
     * 评论id
     */

    private String commentId;
    /**
     * 回复类型 消息 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论
     */
    private String type;

    private String sendUserId;
}
