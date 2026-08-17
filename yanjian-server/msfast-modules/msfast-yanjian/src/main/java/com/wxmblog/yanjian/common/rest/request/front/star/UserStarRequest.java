package com.wxmblog.yanjian.common.rest.request.front.star;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStarRequest {

    @ApiModelProperty(hidden = true)
    private String ownerId;

    //经度 纬度
    @ApiModelProperty(value = "经度")
    private String lon;

    @ApiModelProperty(value = "纬度")
    private String lat;

}
