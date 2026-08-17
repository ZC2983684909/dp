package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ArticlePageResponse {

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "头像")
    private String headPortrait;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    private String jobMes;

    private Date birthDate;

    @ApiModelProperty(value = "简介资料信息")
    private String information;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 图片
     */
    @ApiModelProperty(value = "图片")
    private List<String> img;

    /**
     * 评论数
     */
    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    /**
     * 点赞数
     */
    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "是否点赞过")
    private Boolean isLike;

    @ApiModelProperty(value = "发布时间")
    private String time;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "是否可以删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "排序用于置顶")
    private Integer sort;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "动态类型 类型 image  video")
    private String type;

    @ApiModelProperty(value = "话题列表")
    private List<SubjectPageResponse> subjectList;

}
