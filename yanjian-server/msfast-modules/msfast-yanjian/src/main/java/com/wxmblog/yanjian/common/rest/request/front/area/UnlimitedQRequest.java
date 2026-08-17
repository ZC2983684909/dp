package com.wxmblog.yanjian.common.rest.request.front.area;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UnlimitedQRequest {

    @ApiModelProperty(value = "参数 a=1")
    @NotBlank
    private String scene;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Object lineColor;

    private Boolean isHyaline;

}
