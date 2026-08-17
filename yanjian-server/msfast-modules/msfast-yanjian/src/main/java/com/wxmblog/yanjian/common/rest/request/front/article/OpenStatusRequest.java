package com.wxmblog.yanjian.common.rest.request.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OpenStatusRequest {

    @ApiModelProperty(value = "动态主键")
    @NotBlank(message = "动态不能为空")
    private String id;

    @ApiModelProperty(value = "是否公开 1-公开 0-私密")
    @NotNull(message = "是否公开不能为空")
    private Integer open;
}
