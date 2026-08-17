package com.wxmblog.yanjian.common.rest.request.front.chat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApplyChatRequest {

    @ApiModelProperty(value = "申请用户ID")
    @NotBlank(message = "申请用户ID不能为空")
    private String applyUserId;
}
