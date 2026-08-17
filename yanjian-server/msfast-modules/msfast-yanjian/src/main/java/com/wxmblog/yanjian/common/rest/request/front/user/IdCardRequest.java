package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IdCardRequest {

    @NotBlank(message = "身份证不可为空")
    @ApiModelProperty(value = "身份证")
    private String idCard;

    @NotBlank(message = "姓名不可为空")
    @ApiModelProperty(value = "姓名")
    private String name;

    @NotBlank(message = "微信code不可为空")
    @ApiModelProperty(value = "微信code")
    private String code;

}
