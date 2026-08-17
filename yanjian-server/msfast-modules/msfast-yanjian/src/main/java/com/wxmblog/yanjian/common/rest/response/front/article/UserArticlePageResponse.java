package com.wxmblog.yanjian.common.rest.response.front.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserArticlePageResponse {

    @ApiModelProperty(value = "主键")
    private String id;

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
     * 点赞数
     */
    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "是否点赞过")
    private Boolean isLike;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "是否可以删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "年份 可能为空 不展示")
    private Integer year;

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "日期")
    private String day;

    @ApiModelProperty(value = "是否公开 1-公开 0-私密")
    private Integer open;

    @ApiModelProperty(value = "话题列表")
    private List<SubjectPageResponse> subjectList;

    @ApiModelProperty(value = "动态类型 类型 image  video")
    private String type;

    @ApiModelProperty(value = "城市")
    private String city;

}
