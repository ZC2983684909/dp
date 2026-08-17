package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.ArticleToSubjectService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.ArticleSubjectDao;
import com.wxmblog.yanjian.service.ArticleSubjectService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("articleSubjectService")
public class ArticleSubjectServiceImpl extends ServiceImpl<ArticleSubjectDao, ArticleSubjectEntity> implements ArticleSubjectService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private ArticleToSubjectService articleToSubjectService;

    @Transactional
    @Override
    public void addVisit(List<String> articleIds) {

        if (CollectionUtil.isEmpty(articleIds)) {
            return;
        }
        Wrapper<ArticleToSubjectEntity> wrapper = new QueryWrapper<ArticleToSubjectEntity>().lambda()
                .in(ArticleToSubjectEntity::getArticleId, articleIds);
        List<ArticleToSubjectEntity> subjectEntityList = articleToSubjectService.list(wrapper);
        if (CollectionUtil.isNotEmpty(subjectEntityList)) {
            List<String> ids = subjectEntityList.stream().map(ArticleToSubjectEntity::getSubjectId).collect(Collectors.toList());
            for (String id : ids) {
                RLock lock = redissonClient.getLock(id);
                try {
                    lock.lock(10, TimeUnit.SECONDS);
                    ArticleSubjectEntity articleSubjectEntity = this.getById(id);
                    if (articleSubjectEntity != null) {
                        articleSubjectEntity.setVisitCount(articleSubjectEntity.getVisitCount() + 1);
                        updateById(articleSubjectEntity);
                    }

                } finally {
                    lock.unlock();
                }
            }
        }

    }

    @Transactional
    @Override
    public void addDiscuss(List<String> articleIds) {

        if (CollectionUtil.isEmpty(articleIds)) {
            return;
        }
        Wrapper<ArticleToSubjectEntity> wrapper = new QueryWrapper<ArticleToSubjectEntity>().lambda()
                .in(ArticleToSubjectEntity::getArticleId, articleIds);
        List<ArticleToSubjectEntity> subjectEntityList = articleToSubjectService.list(wrapper);

        if (CollectionUtil.isNotEmpty(subjectEntityList)) {
            List<String> ids = subjectEntityList.stream().map(ArticleToSubjectEntity::getSubjectId).collect(Collectors.toList());
            for (String id : ids) {
                RLock lock = redissonClient.getLock(id);
                try {
                    lock.lock(10, TimeUnit.SECONDS);
                    ArticleSubjectEntity articleSubjectEntity = this.getById(id);
                    if (articleSubjectEntity != null) {
                        articleSubjectEntity.setDiscussCount(articleSubjectEntity.getDiscussCount() + 1);
                        updateById(articleSubjectEntity);
                    }

                } finally {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public void addVisitById(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        for (String id : ids) {
            RLock lock = redissonClient.getLock(id);
            try {
                lock.lock(10, TimeUnit.SECONDS);
                ArticleSubjectEntity articleSubjectEntity = this.getById(id);
                if (articleSubjectEntity != null) {
                    articleSubjectEntity.setVisitCount(articleSubjectEntity.getVisitCount() + 1);
                    updateById(articleSubjectEntity);
                }

            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void addDiscussById(Set<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        for (String id : ids) {
            RLock lock = redissonClient.getLock(id);
            try {
                lock.lock(10, TimeUnit.SECONDS);
                ArticleSubjectEntity articleSubjectEntity = this.getById(id);
                if (articleSubjectEntity != null) {
                    articleSubjectEntity.setDiscussCount(articleSubjectEntity.getDiscussCount() + 1);
                    updateById(articleSubjectEntity);
                }

            } finally {
                lock.unlock();
            }
        }
    }
}
