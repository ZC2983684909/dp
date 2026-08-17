package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-03 16:57:08
 */
@Data
@TableName(value = "user_nature_visit", autoResultMap = true)
public class UserNatureVisitEntity extends BaseEntity {


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
     * 浏览类型 1-浏览 2-关注
     */
    @TableField("type")
    private String type;
}
