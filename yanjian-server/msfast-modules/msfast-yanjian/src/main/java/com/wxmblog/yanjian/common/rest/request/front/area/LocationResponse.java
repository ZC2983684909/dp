package com.wxmblog.yanjian.common.rest.request.front.area;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LocationResponse {

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 区/县
     */
    @ApiModelProperty(value = "区/县")
    private String county;

    /**
     * 乡镇
     */
    @ApiModelProperty(value = "街道")
    private String township;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String lon;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String lat;

    /**
     * 是否移动
     */
    private Boolean isMove;
}
