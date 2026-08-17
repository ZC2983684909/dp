package com.wxmblog.yanjian.common.rest.request.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessagePageRequest {

    @ApiModelProperty(value = "类型 1-评论 2-点赞")
    private Integer type;

    @ApiModelProperty(hidden = true)
    private String userId;
}
