package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArticleInfoResponse {

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "头像")
    private String avatar;

    private String sex;

    @ApiModelProperty(value = "昵称")
    private String nickName;

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

    @ApiModelProperty(value = "是否公开 1-公开 0-私密")
    private Integer open;

    @ApiModelProperty(value = "话题列表")
    private List<SubjectPageResponse> subjectList;

    @ApiModelProperty(value = "动态类型 类型 image  video")
    private String type;

    @ApiModelProperty(value = "城市")
    private String city;

    private String jobMes;

    private Date birthDate;

    @ApiModelProperty(value = "是否可以删除")
    private Boolean isDelete;
}
