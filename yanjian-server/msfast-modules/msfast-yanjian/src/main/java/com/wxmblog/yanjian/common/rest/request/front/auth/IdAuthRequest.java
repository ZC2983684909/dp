package com.wxmblog.yanjian.common.rest.request.front.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IdAuthRequest {

    @NotBlank(message = "身份证不能为空")
    private String idCard;

    @NotBlank(message = "姓名不能为空")
    private String name;
}
