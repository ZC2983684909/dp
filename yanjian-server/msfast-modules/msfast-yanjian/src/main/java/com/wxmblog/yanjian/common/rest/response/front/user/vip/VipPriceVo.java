package com.wxmblog.yanjian.common.rest.response.front.user.vip;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VipPriceVo {

    @ApiModelProperty(value = "会员月数")
    private Integer month;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal price;

    @ApiModelProperty(value = "会员单价")
    private BigDecimal unitPrice;
}
