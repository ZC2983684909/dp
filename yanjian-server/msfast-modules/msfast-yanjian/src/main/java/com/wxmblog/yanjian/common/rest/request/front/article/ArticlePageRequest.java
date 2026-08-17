package com.wxmblog.yanjian.common.rest.request.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ArticlePageRequest {

    @ApiModelProperty(hidden = true)
    private String ownerId;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "话题")
    private String subjectId;

    @ApiModelProperty(value = "排序方式 comp-综合 nowtime-最新")
    private String sortType;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "周", hidden = true)
    private Date week;

    @ApiModelProperty(value = "文章id", hidden = true)
    private String articleId;
}
