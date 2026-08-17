package com.wxmblog.yanjian.common.rest.request.front.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserInfoPageRequest {


    @ApiModelProperty(value = "现居地")
    private String residentialCity;

    @ApiModelProperty(value = "最小年龄 传出生年份 入1996 1998 2000")
    private Integer minAge;

    @ApiModelProperty(value = "最大年龄 传出生年份 入1996 1998 2000")
    private Integer maxAge;

    @ApiModelProperty(value = "最小身高")
    private Integer minHeight;

    @ApiModelProperty(value = "最大身高")
    private Integer maxHeight;

    @ApiModelProperty(value = "倾向地")
    private String tendLiveCity;

    @ApiModelProperty(value = "兴趣")
    private String otherLabel;

    @ApiModelProperty(value = "是否在读")
    private Boolean isStudy;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "家乡")
    private String homeTown;

    @ApiModelProperty(value = "排序类型 recommend-推荐 last-最新 distance-距离")
    private String sortType;

    @ApiModelProperty(value = "用户id", hidden = true)
    private String ownerId;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "定位城市")
    private String city;

    @ApiModelProperty(value = "定位区")
    private String county;

    //经度 纬度
    @ApiModelProperty(value = "经度")
    private String lon;

    @ApiModelProperty(value = "纬度")
    private String lat;

    //相似度
    @ApiModelProperty(value = "相似度")
    private Double similarity;

    @ApiModelProperty(value = "标签集合", hidden = true)
    private List<String> labelList;

    @ApiModelProperty(value = "id集合", hidden = true)
    private List<String> idList;

    @ApiModelProperty(value = "id集合", hidden = true)
    private List<String> noIdList;

    @ApiModelProperty(hidden = true)
    private Date week;

    @ApiModelProperty(value = "实名认证")
    private Boolean isIdAuth;

    @ApiModelProperty(value = "原相机")
    private Boolean isOriginalCamera;

    @ApiModelProperty(value = "是否主城", hidden = true)
    private Boolean mainCity;

}
