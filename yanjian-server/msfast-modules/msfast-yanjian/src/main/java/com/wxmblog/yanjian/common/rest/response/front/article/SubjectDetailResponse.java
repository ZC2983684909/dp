package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubjectDetailResponse extends SubjectPageResponse{

    @ApiModelProperty(value = "是否关注")
    private Boolean isStar;
}
