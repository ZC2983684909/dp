package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.front.article.MessagePageRequest;
import com.wxmblog.yanjian.common.rest.response.front.article.MessagePageResponse;
import com.wxmblog.yanjian.entity.MessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 消息 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论
 * 
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-14 16:20:16
 */
@Mapper
public interface MessageDao extends BaseMapper<MessageEntity> {

    List<MessagePageResponse> getPage(MessagePageRequest request);
}
