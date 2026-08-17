package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InvisibleRequest {

    @ApiModelProperty(value = "状态 1-隐身 2-公开")
    @NotBlank(message = "状态不能为空")
    private String status;
}
