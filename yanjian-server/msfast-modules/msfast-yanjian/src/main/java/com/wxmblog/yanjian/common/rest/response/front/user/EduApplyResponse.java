package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EduApplyResponse {

    @ApiModelProperty(value = "学校名称")
    private String school;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "证明图片")
    private List<String> evidence;
}
