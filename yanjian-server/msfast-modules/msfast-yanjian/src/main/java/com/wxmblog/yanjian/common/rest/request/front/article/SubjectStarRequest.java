package com.wxmblog.yanjian.common.rest.request.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SubjectStarRequest {

    @ApiModelProperty(value = "id")
    @NotBlank
    private String id;
}
