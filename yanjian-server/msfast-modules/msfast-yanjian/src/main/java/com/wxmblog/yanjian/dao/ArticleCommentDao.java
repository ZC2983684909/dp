package com.wxmblog.yanjian.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxmblog.yanjian.entity.ArticleCommentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态评论
 * 
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2024-01-31 15:23:10
 */
@Mapper
public interface ArticleCommentDao extends BaseMapper<ArticleCommentEntity> {
	
}
