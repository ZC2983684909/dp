package com.wxmblog.yanjian.common.rest.response.front.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserArticleViewResponse {

    @ApiModelProperty(value = "朋友圈图片")
    private List<ArticlePreVo> articleImg=new ArrayList<>();

    @ApiModelProperty(value = "朋友圈文字")
    private String articleContent;
}
