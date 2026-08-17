package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessageReadResponse {

    @ApiModelProperty(value = "收藏我的")
    private long starMe;

    @ApiModelProperty(value = "申请我的")
    private long applyMe;

    @ApiModelProperty(value = "我的申请")
    private long myApply;
}
