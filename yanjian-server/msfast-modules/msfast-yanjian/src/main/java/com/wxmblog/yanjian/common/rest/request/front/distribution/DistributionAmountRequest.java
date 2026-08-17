package com.wxmblog.yanjian.common.rest.request.front.distribution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DistributionAmountRequest {

    @ApiModelProperty(value = "用户id",hidden = true)
    private String ownerId;

}
