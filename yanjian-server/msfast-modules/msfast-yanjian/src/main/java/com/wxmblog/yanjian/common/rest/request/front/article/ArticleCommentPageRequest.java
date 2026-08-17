package com.wxmblog.yanjian.common.rest.request.front.article;

import lombok.Data;

@Data
public class ArticleCommentPageRequest {

    private String userId;

    private String articleId;
}
