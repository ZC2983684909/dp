package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IdApplyRequest {

    @ApiModelProperty(value = "身份认证业务token")
    @NotBlank(message = "请完成实名认证")
    private String eidToken;
}
