package com.wxmblog.yanjian.common.rest.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DetectFaceResultVo {

    @ApiModelProperty(value = "是否检测到人脸")
    private Boolean isExistFace;

    @ApiModelProperty(value = "人脸数量")
    private Integer faceCount;

    @ApiModelProperty(value = "性别")
    private String gender;

    //推荐照片质量70 以上
    @ApiModelProperty(value = "照片质量")
    private Long quality;

}
