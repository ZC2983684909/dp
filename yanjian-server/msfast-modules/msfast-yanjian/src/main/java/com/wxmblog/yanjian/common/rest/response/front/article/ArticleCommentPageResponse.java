package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ArticleCommentPageResponse {

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "头像 用户头像")
    private String headPortrait;

    @ApiModelProperty(value = "性别 MALE(\"男\"),\n" + "    FEMALE(\"女\")")
    private String gender;

    @ApiModelProperty(value = "昵称 用户名")
    private String nickName;

    @ApiModelProperty(value = "简介资料信息")
    private String information;

    /**
     * 评论内容
     */
    @ApiModelProperty(value = "评论内容 用户评论内容")
    private String content;


    /**
     * 点赞数
     */
    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "是否点赞过 用户是否点赞")
    private Boolean isLike;

    @ApiModelProperty(value = "发布时间 用户评论时间")
    private String time;

    @ApiModelProperty(value = "是否可以删除 是否是自己评论")
    private Boolean deleteBtn;

    @ApiModelProperty(hidden = true)
    private Date createTime;


    @ApiModelProperty(value = "匿名 1-匿名 0-非匿名")
    private String anonymous;

    @ApiModelProperty(value = "回复用户id")
    private String replyUserId;

    @ApiModelProperty(value = "回复用户昵称")
    private String replyNickName;

    @ApiModelProperty(value = "回复评论id")
    private String commentId;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "是否是楼主")
    private Boolean isOwner;

}
