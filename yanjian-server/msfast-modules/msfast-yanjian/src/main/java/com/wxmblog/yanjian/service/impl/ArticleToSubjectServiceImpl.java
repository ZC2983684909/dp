package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.ArticleToSubjectDao;
import com.wxmblog.yanjian.entity.ArticleToSubjectEntity;
import com.wxmblog.yanjian.service.ArticleToSubjectService;


@Service("articleToSubjectService")
public class ArticleToSubjectServiceImpl extends ServiceImpl<ArticleToSubjectDao, ArticleToSubjectEntity> implements ArticleToSubjectService {

}
