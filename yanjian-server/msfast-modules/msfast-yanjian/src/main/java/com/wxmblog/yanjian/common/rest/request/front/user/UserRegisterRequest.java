package com.wxmblog.yanjian.common.rest.request.front.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.base.auth.common.rest.request.RegisterRequest;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2022-09-22 18:07
 **/

@Data
public class UserRegisterRequest extends RegisterRequest {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不可为空")
    @ApiModelProperty(value = "昵称")
    @Length(max = 8, message = "昵称字数过多")
    private String nickName;

    /**
     * 性别
     */
    @NotBlank(message = "性别不可为空")
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不可为空")
    @Past(message = "出生日期必须小于当前时间")
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
     * 身高
     */
    //@NotNull(message = "身高不可为空")
    @ApiModelProperty(value = "身高")
    private Integer height;
    /**
     * 家乡信息
     */
    //@NotBlank(message = "家乡信息不可为空")
    @ApiModelProperty(value = "家乡信息")
    private String homeTown;

    /**
     * 居住城市
     */
    //@NotBlank(message = "居住城市不可为空")
    @ApiModelProperty(value = "居住城市")
    private String residentialCity;
    /**
     * 其他倾向居住城市
     */
    //@NotBlank(message = "其他倾向居住城市不可为空")
    @ApiModelProperty(value = "其他倾向居住城市")
    private String tendLiveCity;
    /**
     * 兴趣标签
     */
    @ApiModelProperty(value = "兴趣标签")
    private LabelVo fondTags;

    /**
     * 职业
     */
    //@NotBlank(message = "职业不可为空")
    @ApiModelProperty(value = "职业")
    private String jobMes;

    /**
     * 相片
     */
    @ApiModelProperty(value = "原相机视频")
    private String cameraImg;
    /**
     * 年薪
     */
    //@NotBlank(message = "年薪不可为空")
    @ApiModelProperty(value = "年薪")
    private String salarys;

    /**
     * 微信号
     */
    @NotBlank(message = "微信号不可为空")
    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "微信是否公开")
    @NotNull(message = "微信是否公开不可为空")
    private Boolean wechatOpen;

    /**
     * 手机号
     */
    //@NotBlank(message = "手机号不可为空")
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 自我描述
     */
    //@NotBlank(message = "自我描述不可为空")
    @ApiModelProperty(value = "个人签名")
    @Length(min = 5, message = "个人签名至少{min}字")
    @Length(max = 2000, message = "个人签名最多{max}字")
    private String selfDescription;

    /**
     * 个人照片
     */
    @ApiModelProperty(value = "个人照片")
    @NotEmpty(message = "个人照片不可为空")
    @Size(min = 1, message = "个人照片至少上传{min}张")
    @Size(max = 20, message = "个人照片最多上传{max}张")
    private List<String> personalPhoto;

    /**
     * 理想对象
     */
    //@NotBlank(message = "理想对象不可为空")
    @ApiModelProperty(value = "理想对象")
    @Length(min = 20, message = "理想对象至少{min}字")
    private String idealFriend;

   /* @NotNull(message = "身份证信息不可为空")
    @ApiModelProperty(value = "身份证信息")
    private IdCardRequest idCardRequest;*/

    @ApiModelProperty(value = "身份认证业务token")
    //@NotBlank(message = "请完成实名认证")
    private String eidToken;

    @ApiModelProperty(value = "学历信息 和eduCode 任选一个")
    private EduRequest eduRequest;

    @ApiModelProperty(value = "学历认证验证码 和eduRequest 只传其中一个")
    private String eduCode;

    @ApiModelProperty(value = "注册码")
    private String registrationNo;

    @ApiModelProperty(value = "邀请人id")
    private String inviterId;

    @ApiModelProperty(value = "省")
    private String province;
    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;
    /**
     * 区/县
     */
    @ApiModelProperty(value = "区/县")
    private String county;

    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String lon;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String lat;

}
