package com.wxmblog.yanjian.common.rest.request.admin.user;

import com.wxmblog.base.common.enums.FrUserStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-03-03 14:26
 **/
@Data
public class UserAdminCheckRequest {

    @ApiModelProperty(value = "id 新增时为空")
    @NotBlank
    private String id;

    /**
     * 用户状态 启用/停用/注销
     */
    @ApiModelProperty(value = "用户状态 ENABLE(\"启用\"),\n" +
            "    DISABLE(\"禁用\")")
    @NotNull
    private FrUserStatusEnum status;
}
