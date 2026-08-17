package com.wxmblog.yanjian.common.rest.response.front.user;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserInfoPageResponse {

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "是否新人")
    private Boolean isNew;

    //活跃状态
    @ApiModelProperty(value = "活跃状态 1-在线 2-刚刚活跃 3-最近活跃 4-不活跃")
    private String activeStatus;

    //年龄
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private String county;

    @ApiModelProperty(value = "是否主城")
    private Boolean mainCity;

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

    @ApiModelProperty(value = "体重")
    private Integer weight;

    @ApiModelProperty(value = "身高")
    private String heightFormat;

    @ApiModelProperty(value = "体重")
    private String weightFormat;

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
     * 个人照片
     */
    @ApiModelProperty(value = "个人照片")
    private List<PhotoResultVo> personalPhoto;

    @ApiModelProperty(value = "相册")
    private String photo;

    @ApiModelProperty(value = "相似度")
    private Float similarity;

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

    @ApiModelProperty(value = "毕业院校")
    private String school;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "浏览次数")
    private Long browseCount;

    private String userType;

    @ApiModelProperty(value = "最后登录时间")
    private Date latelyTime;

    @ApiModelProperty(value = "距离")
    private BigDecimal distance;

    @ApiModelProperty(value = "距离")
    private String distanceFormat;

    //是否活跃
    private Boolean active;

    @ApiModelProperty(value = "读取状态 1-已读 0-未读")
    private String visitStatus;

    @ApiModelProperty(hidden = true)
    private String visitId;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "是否隐藏 为true时模糊处理")
    private Boolean hide;

}
