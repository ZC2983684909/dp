package com.wxmblog.yanjian.common.rest.response.front.user;

import com.wxmblog.base.common.enums.FrUserStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PersonalCenterResponse {

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "是否会员")
    private Boolean isVip;

    @ApiModelProperty(value = "收藏我的")
    private long starMe;

    @ApiModelProperty(value = "申请我的")
    private long applyMe;

    @ApiModelProperty(value = "我的申请")
    private long myApply;

    @ApiModelProperty(value = "我的收藏")
    private long myStar;


    @ApiModelProperty(value = "收藏我的未读")
    private long unreadStarMe;

    @ApiModelProperty(value = "申请我的未读")
    private long unreadApplyMe;

    @ApiModelProperty(value = "我的申请未读")
    private long unreadMyApply;

    @ApiModelProperty(value = "访客未读消息未读")
    private long visitUnreadCount;

    //余额
    @ApiModelProperty(value = "钱包余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "动态")
    private DynamicsBriefResponse dynamics;

    @ApiModelProperty(value = "相册认证状态 1-未认证 2-存在人脸但未认证 3-成功 4-拒绝 5-失败")
    private String photoAuth;

    @ApiModelProperty(value = "身份认证状态 1-未认证 2-认证中 3-成功 4-拒绝 5-失败")
    private String idAuth;

    @ApiModelProperty(value = "警告")
    private String warning;

    @ApiModelProperty(value = "隐私")
    private String invisible;

}
