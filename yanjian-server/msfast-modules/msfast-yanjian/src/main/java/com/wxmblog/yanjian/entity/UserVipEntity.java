package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;


/**
 * 用户vip
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 14:23:42
 */
@Data
@TableName(value = "user_vip", autoResultMap = true)
public class UserVipEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 会员等级 vip svip
     */
    @TableField("level")
    private String level;
    /**
     * 到期时间
     */
    @TableField("expiration_date")
    private Date expirationDate;

}
