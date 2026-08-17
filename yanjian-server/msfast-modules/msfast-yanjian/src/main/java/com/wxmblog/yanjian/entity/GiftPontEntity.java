package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-01-06 23:05:29
 */
@Data
@TableName(value = "gift_pont", autoResultMap = true)
public class GiftPontEntity extends BaseEntity {


    /**
     *
     */
    @TableField("user_id")
    private String userId;
    /**
     * 0-未使用 1-使用
     */
    @TableField("status")
    private String status;
    /**
     * 使用次数
     */
    @TableField("num")
    private Integer num;

}
