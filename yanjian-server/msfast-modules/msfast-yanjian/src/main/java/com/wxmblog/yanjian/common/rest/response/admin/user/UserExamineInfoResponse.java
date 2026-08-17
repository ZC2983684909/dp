package com.wxmblog.yanjian.common.rest.response.admin.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-02-14 16:16
 **/

@Data
public class UserExamineInfoResponse extends BaseUserInfoResponse {

    @ApiModelProperty(value = "待审核资料认证状态 EXAMINE(\"审核中\"),\n" +
            "    PASS(\"通过\"),\n" +
            "    REFUSE(\"拒绝\")")
    private String waitApprovedStatus;

    @ApiModelProperty(value = "审核理由")
    private String remarks;

    @ApiModelProperty(value = "待审核相册")
    private List<String> waitApprovedImg;

    /**
     * 相册
     */
    @ApiModelProperty(value = "相册")
    private List<String> imgList;

    /**
     * 用户资料状态
     */
    @ApiModelProperty(value = "用户资料状态")
    private String authStatus;

    @ApiModelProperty(value = "用户资料状态")
    private String idAuth;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "相册认证状态")
    private String photoAuth;

    //real_photo
    @ApiModelProperty(value = "用户实名相册截图")
    private List<String> realPhoto;
}
