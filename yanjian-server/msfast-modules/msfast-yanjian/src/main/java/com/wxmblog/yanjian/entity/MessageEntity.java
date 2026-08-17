package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 消息 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-14 16:20:16
 */
@Data
@TableName(value = "message", autoResultMap = true)
public class MessageEntity extends BaseEntity {

    @TableField("user_id")
    private String userId;

    /**
     * 动态id
     */
    @TableField("article_id")
    private String articleId;
    /**
     * 评论id
     */
    @TableField("comment_id")
    private String commentId;
    /**
     * 回复类型
     */
    @TableField("type")
    private String type;

    /**
     * 读取状态 1-已读 0-未读
     */
    @TableField("status")
    private String status;

    //
    @TableField("send_user_id")
    private String sendUserId;

}
