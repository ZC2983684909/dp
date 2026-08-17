package com.wxmblog.yanjian.common.rest.request.front.article;

import com.wxmblog.yanjian.common.enums.article.PraiseTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PraiseRequest {

    @ApiModelProperty(value = "点赞类型 ARTICLE(\"动态\"),\n" +
            "    COMMENT(\"评论\"),\n" +
            "    REPLY(\"回复\")")
    @NotNull
    private PraiseTypeEnum praiseType;

    @ApiModelProperty(value = "id")
    @NotBlank
    private String id;
}
