package com.wxmblog.yanjian.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxmblog.yanjian.common.rest.request.front.article.*;
import com.wxmblog.yanjian.common.rest.response.front.article.*;
import com.wxmblog.yanjian.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户动态
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2024-01-31 15:23:10
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleEntity> {

    ArticleInfoResponse articleInfo(ArticleInfoRequest request);

    List<UserArticlePageResponse> getUserArticle(UserArticleRequest request);

    List<ArticlePageResponse> getArticlePage(ArticlePageRequest request);

    List<ArticleCommentPageResponse> getCommentPage(ArticleCommentPageRequest request);

    List<SubjectPageResponse> subjectPage();
}
