package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserMessageReadResponse {

    //未读点赞数
    @ApiModelProperty(value = "未读点赞数")
    private long likeCount = 0;
    //未读评论数
    @ApiModelProperty(value = "未读评论数")
    private long commentCount = 0;
    //未读消息数
    @ApiModelProperty(value = "未读消息数")
    private long messageCount = 0;
}
