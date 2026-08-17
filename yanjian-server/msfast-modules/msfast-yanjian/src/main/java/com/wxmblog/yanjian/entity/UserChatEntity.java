package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户聊天解锁
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-01 17:38:50
 */
@Data
@TableName(value = "user_chat", autoResultMap = true)
public class UserChatEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 申请用户id
     */
    @TableField("apply_user_id")
    private String applyUserId;
    /**
     * 1-金额解锁 2-vip解锁 3-微信解锁
     */
    @TableField("apply_way")
    private String applyWay;

}
