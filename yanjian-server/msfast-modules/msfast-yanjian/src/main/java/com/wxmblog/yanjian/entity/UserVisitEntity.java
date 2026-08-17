package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-21 22:16:00
 */
@Data
@TableName(value = "user_visit", autoResultMap = true)
public class UserVisitEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 浏览用户id
     */
    @TableField("visit_user_id")
    private String visitUserId;
    /**
     * 用户浏览次数
     */
    @TableField("num")
    private Integer num;

    /**
     * 读取状态 1-已读 0-未读
     */
    @TableField("status")
    private String status;
}
