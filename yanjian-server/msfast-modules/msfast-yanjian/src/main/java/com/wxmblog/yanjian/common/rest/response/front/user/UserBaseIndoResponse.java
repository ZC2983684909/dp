package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBaseIndoResponse {

    @ApiModelProperty("是否注册")
    private Boolean isRegister;

    @ApiModelProperty("性别")
    private String sex;
}
