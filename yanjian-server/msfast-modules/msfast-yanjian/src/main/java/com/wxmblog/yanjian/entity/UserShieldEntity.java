package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-27 22:49:44
 */
@Data
@TableName(value = "user_shield", autoResultMap = true)
public class UserShieldEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 屏蔽用户id
     */
    @TableField("shield_id")
    private String shieldId;

}
