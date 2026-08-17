package com.wxmblog.yanjian.common.rest.request.admin.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class UserPayRequest {

    @ApiModelProperty(value = "用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @ApiModelProperty(value = "提现金额")
    @NotNull(message = "提现金额不能为空")
    private BigDecimal amount;
}
