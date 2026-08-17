package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.entity.ArticleSubjectEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Set;


/**
 * 话题
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-13 17:54:41
 */
public interface ArticleSubjectService extends IService<ArticleSubjectEntity> {

    @Async
    void addVisit(List<String> articleIds);

    @Async
    void addDiscuss(List<String> articleIds);

    @Async
    void addVisitById(List<String> ids);

    @Async
    void addDiscussById(Set<String> ids);
}

