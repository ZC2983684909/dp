package com.wxmblog.yanjian.service;

import com.wxmblog.yanjian.common.rest.response.front.article.ArticlePageResponse;

import java.util.List;

/**
 * 文章权重计算服务
 * 
 * @author peiwei
 * @email jlu.hpw@foxmail.com
 * @date 2025-06-25
 */
public interface ArticleWeightService {

    /**
     * 计算文章权重并返回排序后的列表
     * 
     * @param articles 原始文章列表
     * @return 按权重排序后的文章列表
     */
    List<ArticlePageResponse> calculateAndSortByWeight(List<ArticlePageResponse> articles);

    /**
     * 应用随机化策略
     * 
     * @param articles 已排序的文章列表
     * @return 随机化后的文章列表
     */
    List<ArticlePageResponse> applyRandomization(List<ArticlePageResponse> articles);

    /**
     * 获取缓存的权重排序列表
     * 
     * @param cacheKey 缓存键
     * @return 缓存的文章列表
     */
    List<ArticlePageResponse> getCachedWeightedArticles(String cacheKey);

    /**
     * 缓存权重排序列表
     * 
     * @param cacheKey 缓存键
     * @param articles 文章列表
     */
    void cacheWeightedArticles(String cacheKey, List<ArticlePageResponse> articles);

    /**
     * 清除权重缓存
     * 
     * @param cacheKey 缓存键
     */
    void clearWeightCache(String cacheKey);
} 