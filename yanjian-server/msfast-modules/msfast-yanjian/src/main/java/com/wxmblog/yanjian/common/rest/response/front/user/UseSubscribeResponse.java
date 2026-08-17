package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UseSubscribeResponse {

    @ApiModelProperty(value = "是否展示消息订阅 true 时展示 为null或false 不展示")
    private Boolean useSubscribe;
}
