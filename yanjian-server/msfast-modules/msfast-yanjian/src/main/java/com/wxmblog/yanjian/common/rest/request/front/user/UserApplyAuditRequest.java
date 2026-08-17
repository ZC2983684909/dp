package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserApplyAuditRequest {

    @ApiModelProperty(value = "审核id")
    @NotBlank
    private String auditId;

    @ApiModelProperty(value = "审核结果 true-通过 false-拒绝")
    @NotNull
    private Boolean result;
}
