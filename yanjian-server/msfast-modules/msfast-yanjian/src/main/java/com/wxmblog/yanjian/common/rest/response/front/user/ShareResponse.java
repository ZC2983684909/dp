package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShareResponse {

    //需要展示的字段有 已邀请人数，累计推广金额，已提现金额，待提现金额，可提现金额 我的推广码
    //已邀请
    @ApiModelProperty(value = "已邀请人数")
    private Long inviteCount;

    //金额
    @ApiModelProperty(value = "累计推广金额")
    private BigDecimal amount;

    //累计可提现金额
    @ApiModelProperty(value = "累计可以提现的金额", hidden = true)
    private BigDecimal surplusTotalAmount;

    @ApiModelProperty(value = "已提现金额")
    private BigDecimal withdrawAmount;

    @ApiModelProperty(value = "全部待提现金额")
    private BigDecimal waitAmount;

    //可提现金额
    @ApiModelProperty(value = "可提现金额")
    private BigDecimal withdrawAbleAmount;

    @ApiModelProperty(value = "推广码")
    private String distributionCode;

}
