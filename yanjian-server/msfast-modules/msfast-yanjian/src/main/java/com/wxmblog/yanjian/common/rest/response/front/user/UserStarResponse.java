package com.wxmblog.yanjian.common.rest.response.front.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserStarResponse {

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @JsonFormat(pattern = "yyyy年MM月dd日")
    private Date birthDate;

    @ApiModelProperty(value = "用户身高")
    private Integer height;

    @ApiModelProperty(value = "用户身高格式化")
    private String heightFormat;

    @ApiModelProperty(value = "用户学历")
    private String education;

    @ApiModelProperty(value = "是否已读 1-已读 0-未读")
    private String readStatus;

    @ApiModelProperty(value = "申请状态 0-未申请 1-申请中 2-通过 3-已拒绝")
    private String applyStatus;

    private String eduAuth;

    @ApiModelProperty(value = "是否新人")
    private Boolean isNew;

    //活跃状态
    @ApiModelProperty(value = "活跃状态 1-在线 2-刚刚活跃 3-最近活跃 4-不活跃")
    private String activeStatus;
    /**
     * 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "身份认证")
    private String idAuth;

    @ApiModelProperty(value = "距离")
    private BigDecimal distance;

    @ApiModelProperty(value = "距离")
    private String distanceFormat;

    @ApiModelProperty(value = "最后登录时间")
    private Date latelyTime;

    //年龄
    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "是否是好友")
    private Boolean isFriend;

    @ApiModelProperty(value = "收藏id",hidden = true)
    private String starId;
}
