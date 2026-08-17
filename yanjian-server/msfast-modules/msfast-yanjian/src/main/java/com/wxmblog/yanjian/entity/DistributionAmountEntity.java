package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户分销金额
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-21 16:46:20
 */
@Data
@TableName(value = "distribution_amount", autoResultMap = true)
public class DistributionAmountEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    //1-奖励 2-提现
    @TableField("type")
    private String type;
    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 消费用户
     */
    @TableField("pay_user_id")
    private String payUserId;
    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;
    /**
     * 消费描述
     */
    @TableField("body")
    private String body;

    //奖励理由 类型 1-颜币充值 2-vip充值 3-被解锁微信 4-邀请用户被解锁微信 5-被私信 6-邀请用户被私信
    @TableField("reason")
    private String reason;
}
