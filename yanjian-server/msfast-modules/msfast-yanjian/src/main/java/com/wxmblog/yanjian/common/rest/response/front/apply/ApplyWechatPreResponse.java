package com.wxmblog.yanjian.common.rest.response.front.apply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyWechatPreResponse {

    @ApiModelProperty(value = "所需金额")
    private Integer price;

    @ApiModelProperty(value = "钱包余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "是否会员")
    private Boolean isVip;

    //VIP剩余次数
    @ApiModelProperty(value = "VIP剩余次数")
    private Long vipCount;

    @ApiModelProperty(value = "对方微信是否可以直接解锁")
    private Boolean wechatOpen;
}
