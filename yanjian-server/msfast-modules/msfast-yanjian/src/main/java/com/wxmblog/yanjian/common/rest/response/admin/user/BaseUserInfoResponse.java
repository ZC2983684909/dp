package com.wxmblog.yanjian.common.rest.response.admin.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-02-22 16:20
 **/

@Data
public class BaseUserInfoResponse {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 用户状态 启用/停用/注销
     */
    @ApiModelProperty(value = "用户状态 ENABLE(\"启用\"),\n" +
            "    DISABLE(\"禁用\"),\n" +
            "    LOGOFF(\"注销\")")
    private FrUserStatusEnum status;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别 MALE(\"男\"),\n" +
            "    FEMALE(\"女\")")
    private String sex;


    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String residentialCity;

    private String salarys;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型 Dummy(\"虚拟\"),\n" +
            "    Normal(\"正常\")")
    private String userType;


    @ApiModelProperty(value = "最近活跃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date latelyTime;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer height;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    private String jobMes;

    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    private String wechat;


    /**
     * 毕业学校
     */
    @ApiModelProperty(value = "毕业学校")
    private String school;

    /**
     * 最高学历
     */
    @ApiModelProperty(value = "最高学历 Specialty(\"专科\"),\n" +
            "    Undergraduate(\"本科\"),\n" +
            "    Master(\"硕士\"),\n" +
            "    Doctor(\"博士\")")
    private String education;


    private String selfDescription;

    /**
     * 兴趣爱好
     */
    @ApiModelProperty(value = "兴趣爱好")
    private String fondTags;

    /**
     * 择偶要求
     */
    @ApiModelProperty(value = "心议的他")
    private String idealFriend;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
     * 金币余额
     */
    @ApiModelProperty(value = "金币余额")
    private Integer applyCount;

    @ApiModelProperty(value = "用户资料状态")
    private String idAuth;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "姓名")
    private String phone;
}
