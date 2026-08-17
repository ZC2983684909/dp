package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户钱包
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 13:57:13
 */
@Data
@TableName(value = "user_account", autoResultMap = true)
public class UserAccountEntity extends BaseEntity {

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 用户
     */
    @TableField("user_id")
    private String userId;
    /**
     * 来源
     */
    @TableField("source")
    private String source;
    /**
     * 来源id
     */
    @TableField("source_id")
    private String sourceId;

    @TableField("remarks")
    private String remarks;

}
