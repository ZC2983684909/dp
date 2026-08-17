package com.wxmblog.yanjian.common.rest.response.front.distribution;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DistributionAmountResponse {

    @ApiModelProperty(value = "id")
    private String id;

    //分销明细字段有 用户头像（模糊），奖励金额，奖励时间，消费描述（颜币充值，vip充值）
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "奖励金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "奖励时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "消费金额")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "消费描述")
    private String body;

    @ApiModelProperty(value = "比例")
    private Integer ratio;

    //1-颜币充值 2-vip充值 3-被解锁微信 4-邀请用户被解锁微信 5-被私信 6-邀请用户被私信
    @ApiModelProperty(value = "类型 1-颜币充值 2-vip充值 3-被解锁微信 4-邀请用户被解锁微信 5-被私信 6-邀请用户被私信")
    private String reason;
}
