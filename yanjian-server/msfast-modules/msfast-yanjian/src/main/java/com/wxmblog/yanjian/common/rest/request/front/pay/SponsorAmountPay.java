package com.wxmblog.yanjian.common.rest.request.front.pay;

import com.wxmblog.base.pay.common.rest.request.BasePayRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SponsorAmountPay extends BasePayRequest {

    @NotNull(message = "赞助金额不可为空")
    private BigDecimal amount;
}
