package com.wxmblog.yanjian.common.rest.response.admin.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-02-27 16:57
 **/

@Data
public class EducationExamineInfoResponse extends BaseUserInfoResponse {

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private String authStatus;

    @ApiModelProperty(value = "相册")
    private List<String> imgList;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "版本")
    private Integer version;
}
