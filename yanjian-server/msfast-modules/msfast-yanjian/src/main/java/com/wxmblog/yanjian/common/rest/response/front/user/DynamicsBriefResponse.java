package com.wxmblog.yanjian.common.rest.response.front.user;


import com.wxmblog.yanjian.common.rest.response.front.article.ArticlePreVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DynamicsBriefResponse {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "封面图")
    private ArticlePreVo img;

    @ApiModelProperty(value = "日期-年")
    private String year;

    @ApiModelProperty(value = "日期-月")
    private String month;

    @ApiModelProperty(value = "日期-日")
    private String day;
}
