package com.wxmblog.yanjian.common.rest.response.front.pay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddBalanceResponse {

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "钱包余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "充值价格")
    private List<BalancePriceVo> balancePriceVos;
}
