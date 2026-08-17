package com.wxmblog.yanjian.common.rest.response.front.home;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class PopUpResponse {

    /**
     * 弹窗背景图
     */
    @ApiModelProperty(value = "弹窗背景图")
    private String image;

    /**
     * 跳转链接
     */
    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "属性")
    private Map<String, Object> attr;
}
