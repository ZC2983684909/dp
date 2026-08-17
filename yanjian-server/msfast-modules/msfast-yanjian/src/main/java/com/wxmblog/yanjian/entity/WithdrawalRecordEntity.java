package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 提现记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 15:22:35
 */
@Data
@TableName(value = "withdrawal_record", autoResultMap = true)
public class WithdrawalRecordEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 订单类型
     */
    @TableField("amount")
    private BigDecimal amount;

}
