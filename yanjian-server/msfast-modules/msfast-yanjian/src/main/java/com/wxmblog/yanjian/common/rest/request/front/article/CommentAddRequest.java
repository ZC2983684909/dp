package com.wxmblog.yanjian.common.rest.request.front.article;

import com.wxmblog.base.common.annotation.ForeignTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class CommentAddRequest {


    @ApiModelProperty(value = "动态id")
    @NotBlank
    @ForeignTable(value = "article", message = "动态不存在")
    private String articleId;

    @Length(max = 200, message = "内容不能超过200字")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @ApiModelProperty(value = "回复评论id")
    private String commentId;

    @ApiModelProperty(value = "匿名 1-匿名 0-非匿名")
    private String anonymous;

}
