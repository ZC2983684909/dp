package com.wxmblog.yanjian.common.rest.request.front.pay;

import com.wxmblog.base.pay.common.rest.request.BasePayRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AmountPay extends BasePayRequest {

    @NotNull(message = "金额不可为空")
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
