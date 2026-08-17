package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户签到
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 16:38:05
 */
@Data
@TableName(value = "sign_in", autoResultMap = true)
public class SignInEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 状态 0-未兑换 1-已兑换
     */
    @TableField("status")
    private String status;

}
