package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EduRequest {

    @ApiModelProperty(value = "学校名称")
    private String school;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "证明图片")
    private List<String> schoolPhoto;
}
