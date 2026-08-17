package com.wxmblog.yanjian.common.rest.response.front.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserInfoResponse {

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy年MM月dd日")
    private Date birthFormatDate;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer height;

    @ApiModelProperty(value = "身高")
    private String heightFormat;

    @ApiModelProperty(value = "体重")
    private Integer weight;

    @ApiModelProperty(value = "体重")
    private String weightFormat;

    /**
     * 相片
     */
    @ApiModelProperty(value = "原相机视频")
    private String cameraImg;

    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 家乡信息
     */
    @ApiModelProperty(value = "家乡信息")
    private String homeTown;

    /**
     * 居住城市
     */
    @ApiModelProperty(value = "居住城市")
    private String residentialCity;

    /**
     * 其他倾向居住城市
     */
    @ApiModelProperty(value = "其他倾向居住城市")
    private String tendLiveCity;

    /**
     * 兴趣标签
     */
    @ApiModelProperty(value = "兴趣标签")
    private LabelVo fondTags;

    @ApiModelProperty(value = "兴趣标签,列表的")
    private List<LabelInfoResponse> fondTagsList;

    @ApiModelProperty(value = "兴趣标签,分割的")
    private String fondTagsFormat;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    private String jobMes;

    /**
     * 年薪
     */
    @ApiModelProperty(value = "年薪")
    private String salarys;

    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "微信是否公开")
    private Boolean wechatOpen;

    /**
     * 自我描述
     */
    @ApiModelProperty(value = "自我描述")
    private String selfDescription;

    /**
     * 个人照片
     */
    @ApiModelProperty(value = "个人照片")
    private List<PhotoResultVo> personalPhoto;

    /**
     * 理想对象
     */
    @ApiModelProperty(value = "理想对象")
    private String idealFriend;

    /**
     * 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "身份认证")
    private String idAuth;

    /**
     * 学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "学历认证")
    private String eduAuth;

    //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
    @ApiModelProperty(value = "照片认证")
    private String photoAuth;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "真实姓名")
    private String name;

    @ApiModelProperty(value = "毕业院校")
    private String school;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "推广码")
    private String distributionCode;

    @ApiModelProperty(value = "警告")
    private String warning;
}
