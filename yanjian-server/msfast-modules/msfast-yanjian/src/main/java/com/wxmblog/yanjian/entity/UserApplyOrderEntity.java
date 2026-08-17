package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
@Data
@TableName(value = "user_apply_order", autoResultMap = true)
public class UserApplyOrderEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 支付订单号
     */
    @TableField("out_trade_no")
    private String outTradeNo;
    /**
     * 商户支付订单号
     */
    @TableField("platform_trade_no")
    private String platformTradeNo;
    /**
     * 订单描述
     */
    @TableField("body")
    private String body;
    /**
     * 订单金额
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 状态 1-待支付 2-已支付 3-已失效
     */
    @TableField("status")
    private String status;
    /**
     * 申请结果
     */
    @TableField("result")
    private String result;

    //订单类型 1-vip充值 2-颜币充值
    @TableField("type")
    private String type;

}
