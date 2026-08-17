package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 17:45:51
 */
@Data
@TableName(value = "user_star", autoResultMap = true)
public class UserStarEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 收藏用户id
     */
    @TableField("star_user_id")
    private String starUserId;

    @TableField("read_status")
    private String readStatus;

}
