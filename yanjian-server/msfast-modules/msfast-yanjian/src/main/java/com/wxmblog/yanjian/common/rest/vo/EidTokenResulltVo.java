package com.wxmblog.yanjian.common.rest.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EidTokenResulltVo {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "最佳帧")
    private String bestFrame;

    /*

    本次流程活体一比一的分数
- 取值范围 [0.00, 100.00]。
- 相似度大于等于70时才判断为同一人，也可根据具体场景自行调整阈值。
- 阈值70的误通过率为千分之一，阈值80的误通过率是万分之一
     */
    @ApiModelProperty(value = "相似度")
    private String compareSim;
}
