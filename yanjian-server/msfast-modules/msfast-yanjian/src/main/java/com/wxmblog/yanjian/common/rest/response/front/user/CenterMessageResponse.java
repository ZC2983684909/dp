package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CenterMessageResponse {

    @ApiModelProperty(value = "访客未读消息")
    private long visitCount;
}
