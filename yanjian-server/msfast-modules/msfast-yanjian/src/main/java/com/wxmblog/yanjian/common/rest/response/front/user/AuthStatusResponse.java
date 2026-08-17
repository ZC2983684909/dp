package com.wxmblog.yanjian.common.rest.response.front.user;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthStatusResponse {

    /**
     * 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "身份认证 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败")
    private String idAuth;
    /**
     * 学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败")
    private String eduAuth;

    @ApiModelProperty(value = "照片认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败")
    private String photoAuth;
}
