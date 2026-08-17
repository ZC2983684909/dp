package com.wxmblog.yanjian.tasks;

import com.wxmblog.yanjian.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文章权重缓存定时任务
 * 
 * @author peiwei
 * @email jlu.hpw@foxmail.com
 * @date 2025-06-25
 */
@Slf4j
@Component
public class ArticleWeightCacheTask {

    @Autowired
    private ArticleService articleService;

    /**
     * 每天凌晨2点清除权重缓存
     * 确保权重计算基于最新的数据
     */
    //@Scheduled(cron = "0 0 2 * * ?")
    public void clearArticleWeightCache() {
        try {
            log.info("开始执行文章权重缓存清理任务");
            articleService.clearArticleWeightCache();
            log.info("文章权重缓存清理任务执行完成");
        } catch (Exception e) {
            log.error("文章权重缓存清理任务执行失败", e);
        }
    }
} 