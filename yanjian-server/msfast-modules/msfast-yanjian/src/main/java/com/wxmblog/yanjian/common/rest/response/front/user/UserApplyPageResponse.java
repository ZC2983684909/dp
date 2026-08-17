package com.wxmblog.yanjian.common.rest.response.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserApplyPageResponse extends UserStarResponse {

    @ApiModelProperty(value = "申请状态 1-申请中 2-通过 3-已拒绝")
    private String status;

    @ApiModelProperty(value = "审核id 同意或是拒绝时传这个id")
    private String auditId;

    @ApiModelProperty(value = "申请描述")
    private String applyDesc;

    @ApiModelProperty(value = "对方是否已读 1-已读 0-未读")
    private String otherReadStatus;

}
