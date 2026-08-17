package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.yanjian.common.rest.response.front.article.ArticlePageResponse;
import com.wxmblog.yanjian.dao.TUserDao;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.service.ArticleWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文章权重计算服务实现
 * 
 * @author peiwei
 * @email jlu.hpw@foxmail.com
 * @date 2025-06-25
 */
@Slf4j
@Service("articleWeightService")
public class ArticleWeightServiceImpl implements ArticleWeightService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private TUserDao tUserDao;

    // 缓存过期时间：1小时
    private static final long CACHE_EXPIRE_HOURS = 1;
    
    // 最新动态天数阈值
    private static final int RECENT_ARTICLE_DAYS = 7;
    
    // 新用户天数阈值
    private static final int NEW_USER_DAYS = 14;

    @Override
    public List<ArticlePageResponse> calculateAndSortByWeight(List<ArticlePageResponse> articles) {
        if (CollectionUtil.isEmpty(articles)) {
            return articles;
        }

        // 获取所有用户信息用于计算新用户权重
        Set<String> userIds = articles.stream()
                .map(ArticlePageResponse::getUserId)
                .collect(Collectors.toSet());
        Map<String, TUserEntity> userMap = getUserMap(userIds);

        // 计算权重
        List<ArticleWithWeight> articlesWithWeight = articles.stream()
                .map(article -> {
                    double weight = calculateWeight(article, userMap);
                    return new ArticleWithWeight(article, weight);
                })
                .collect(Collectors.toList());

        // 按权重排序
        articlesWithWeight.sort((a, b) -> Double.compare(b.weight, a.weight));

        // 返回排序后的文章列表
        return articlesWithWeight.stream()
                .map(ArticleWithWeight::getArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticlePageResponse> applyRandomization(List<ArticlePageResponse> articles) {
        if (CollectionUtil.isEmpty(articles)) {
            return articles;
        }

        // 可配置的前N%比例，用于随机化处理
        double topPercentage = 0.15; // 可以修改这个值来调整前N%的比例
        
        List<ArticlePageResponse> randomizedArticles = new ArrayList<>(articles);
        
        // 计算前N%的位置
        int topIndex = (int) Math.ceil(articles.size() * topPercentage);
        
        if (topIndex > 0 && topIndex < articles.size()) {
            // 前N%的内容
            List<ArticlePageResponse> topArticles = new ArrayList<>(randomizedArticles.subList(0, topIndex));
            // 后(100-N)%的内容
            List<ArticlePageResponse> bottomArticles = new ArrayList<>(randomizedArticles.subList(topIndex, articles.size()));
            
            // 随机从前N%中抽出20%的内容（即全部动态数量的8%）
            int exchangeCount = Math.max(1, (int) Math.ceil(articles.size() * 0.08));
            exchangeCount = Math.min(exchangeCount, topArticles.size());
            
            if (exchangeCount > 0 && !bottomArticles.isEmpty()) {
                // 随机选择要交换的前N%内容
                List<Integer> topIndices = new ArrayList<>();
                for (int i = 0; i < topArticles.size(); i++) {
                    topIndices.add(i);
                }
                Collections.shuffle(topIndices);
                List<Integer> selectedTopIndices = topIndices.subList(0, exchangeCount);
                
                // 随机选择要交换的后(100-N)%内容
                List<Integer> bottomIndices = new ArrayList<>();
                for (int i = 0; i < bottomArticles.size(); i++) {
                    bottomIndices.add(i);
                }
                Collections.shuffle(bottomIndices);
                List<Integer> selectedBottomIndices = bottomIndices.subList(0, Math.min(exchangeCount, bottomArticles.size()));
                
                // 执行交换
                for (int i = 0; i < selectedTopIndices.size() && i < selectedBottomIndices.size(); i++) {
                    int topIndex1 = selectedTopIndices.get(i);
                    int bottomIndex = selectedBottomIndices.get(i);
                    
                    ArticlePageResponse temp = topArticles.get(topIndex1);
                    topArticles.set(topIndex1, bottomArticles.get(bottomIndex));
                    bottomArticles.set(bottomIndex, temp);
                }
            }
            
            // 对前N%和后(100-N)%分别进行shuffle
            Collections.shuffle(topArticles);
            Collections.shuffle(bottomArticles);
            
            // 重新组合列表
            randomizedArticles.clear();
            randomizedArticles.addAll(topArticles);
            randomizedArticles.addAll(bottomArticles);
        } else {
            // 如果列表太小，直接shuffle整个列表
            Collections.shuffle(randomizedArticles);
        }
        
        return randomizedArticles;
    }

    @Override
    public List<ArticlePageResponse> getCachedWeightedArticles(String cacheKey) {
        return redisService.getCacheObject(cacheKey);
    }

    @Override
    public void cacheWeightedArticles(String cacheKey, List<ArticlePageResponse> articles) {
        redisService.setCacheObject(cacheKey, articles, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void clearWeightCache(String cacheKey) {
        redisService.deleteObject(cacheKey);
    }

    /**
     * 计算单个文章的权重
     * 权重 = 最新动态分数 + 热门动态分数 + 新用户动态分数
     */
    private double calculateWeight(ArticlePageResponse article, Map<String, TUserEntity> userMap) {
        double weight = 0.0;
        
        // 1. 最新动态权重（7日内算最新动态，距离今天n日则加8-n分）
        weight += calculateRecentWeight(article);
        
        // 2. 热门动态权重（使用点赞数+评论数*3）
        weight += calculatePopularWeight(article);
        
        // 3. 新用户动态权重（14日内算新用户，注册时间距离今天p日则加15-p分）
        weight += calculateNewUserWeight(article, userMap);
        
        return weight;
    }

    /**
     * 计算最新动态权重
     */
    private double calculateRecentWeight(ArticlePageResponse article) {
        if (article.getCreateTime() == null) {
            return 0.0;
        }
        
        Date now = new Date();
        long daysDiff = DateUtil.betweenDay(article.getCreateTime(), now, true);
        
        if (daysDiff <= RECENT_ARTICLE_DAYS) {
            return 8 - daysDiff + 8;
        }
        
        return 0.0;
    }

    /**
     * 计算热门动态权重
     */
    private double calculatePopularWeight(ArticlePageResponse article) {
        int likeCount = article.getLikeCount() != null ? article.getLikeCount() : 0;
        int commentCount = article.getCommentCount() != null ? article.getCommentCount() : 0;
        
        return likeCount + commentCount * 3;
    }

    /**
     * 计算新用户动态权重
     */
    private double calculateNewUserWeight(ArticlePageResponse article, Map<String, TUserEntity> userMap) {
        TUserEntity user = userMap.get(article.getUserId());
        if (user == null || user.getCreateTime() == null) {
            return 0.0;
        }
        
        Date now = new Date();
        long daysDiff = DateUtil.betweenDay(user.getCreateTime(), now, true);
        
        if (daysDiff <= NEW_USER_DAYS) {
            return 15 - daysDiff;
        }
        
        return 0.0;
    }

    /**
     * 获取用户信息映射
     */
    private Map<String, TUserEntity> getUserMap(Set<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        
        try {
            List<TUserEntity> users = tUserDao.selectBatchIds(userIds);
            return users.stream()
                    .collect(Collectors.toMap(TUserEntity::getId, user -> user));
        } catch (Exception e) {
            log.error("Failed to get user map for userIds: {}", userIds, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 文章权重包装类
     */
    private static class ArticleWithWeight {
        private final ArticlePageResponse article;
        private final double weight;

        public ArticleWithWeight(ArticlePageResponse article, double weight) {
            this.article = article;
            this.weight = weight;
        }

        public ArticlePageResponse getArticle() {
            return article;
        }

        public double getWeight() {
            return weight;
        }
    }
} 