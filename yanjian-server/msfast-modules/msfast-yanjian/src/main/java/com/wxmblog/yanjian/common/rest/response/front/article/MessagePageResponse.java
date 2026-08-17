package com.wxmblog.yanjian.common.rest.response.front.article;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MessagePageResponse {

    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "发送用户id")
    private String sendUserId;

    @ApiModelProperty(value = "头像 用户头像")
    private String headPortrait;

    @ApiModelProperty(value = "性别 MALE(\"男\"),\n" + "    FEMALE(\"女\")")
    private String gender;

    @ApiModelProperty(value = "昵称 用户名")
    private String nickName;

    @ApiModelProperty(value = "匿名")
    private String anonymous;

    /**
     * 动态id
     */
    @ApiModelProperty(value = "动态id")
    private String articleId;

    @ApiModelProperty(value = "动态图片")
    private String articleImg;

    @ApiModelProperty(value = "动态类型")
    private String articleType;


    /**
     * 评论id
     */
    @ApiModelProperty(value = "评论id")
    private String commentId;

    @ApiModelProperty(value = "评论内容")
    private String commentContent;

    /**
     * 回复类型
     */
    @ApiModelProperty(value = "回复类型")
    private String type;

    @ApiModelProperty(value = "内容 评论下面的内容 有则显示 没有则不显示，需要根据类型显示不同的文案 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论")
    private String content;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeStr;

    @ApiModelProperty(value = "读取状态 1-已读 0-未读")
    private String status;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isLike;
}
