package com.wxmblog.yanjian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.ArticleCommentDao;
import com.wxmblog.yanjian.entity.ArticleCommentEntity;
import com.wxmblog.yanjian.service.ArticleCommentService;
import org.springframework.stereotype.Service;


@Service("articleCommentService")
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentDao, ArticleCommentEntity> implements ArticleCommentService {

}
