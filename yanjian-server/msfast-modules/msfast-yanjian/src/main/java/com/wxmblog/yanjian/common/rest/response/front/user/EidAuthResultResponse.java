package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EidAuthResultResponse {

    @ApiModelProperty(value = "认证结果")
    private Boolean result;

    @ApiModelProperty(value = "认证信息")
    private String message;
}
