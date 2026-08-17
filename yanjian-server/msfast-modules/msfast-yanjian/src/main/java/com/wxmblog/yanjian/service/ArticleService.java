package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.yanjian.common.rest.request.front.article.*;
import com.wxmblog.yanjian.common.rest.response.front.article.*;
import com.wxmblog.yanjian.entity.ArticleEntity;

import java.util.List;


/**
 * 用户动态
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2024-01-31 15:23:10
 */
public interface ArticleService extends IService<ArticleEntity> {

    void addArticle(ArticleAddRequest request);

     Long praise(PraiseRequest request);

    Long cancelPraise(PraiseRequest request);


    void deleteArticle(String id);

    void openStatus(OpenStatusRequest request);

    PageResult<ArticlePageResponse> articlePage(ArticlePageRequest request, Integer pageIndex, Integer pageSize);

    void addComment(CommentAddRequest request);

    void deleteComment(String id);

    PageResult<SubjectPageResponse> subjectPage(Integer pageIndex, Integer pageSize);

    List<SubjectPageResponse> homeSubject();

    SubjectDetailResponse subjectDetail(String id);

    void subjectStar(SubjectStarRequest request);

    void cancelsubjectStar(SubjectStarRequest request);

    /**
     * 清除文章权重缓存
     */
    void clearArticleWeightCache();

    /**
     * 获取动态权重排序的文章列表
     */
    List<ArticlePageResponse> getWeightedArticleList(ArticlePageRequest request);
}

