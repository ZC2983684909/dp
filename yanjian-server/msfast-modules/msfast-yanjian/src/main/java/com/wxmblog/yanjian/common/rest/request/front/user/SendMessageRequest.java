package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SendMessageRequest {

    @ApiModelProperty(value = "审核id")
    @NotBlank
    private String userId;

    @NotBlank
    private String content;
}
