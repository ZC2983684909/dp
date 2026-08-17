package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApplyWxRequest {

    @NotBlank(message = "申请用户不可为空")
    @ApiModelProperty(value = "申请用户")
    private String applyUserId;

    @ApiModelProperty(value = "申请方式 1-金额解锁 2-vip解锁")
    @NotBlank(message = "申请方式不可为空")
    private String applyWay;

    @ApiModelProperty(value = "申请描述 当对方微信没有公开时必填")
    private String applyDesc;

}
