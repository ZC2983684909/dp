package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.base.common.handler.BaseStringListTypeHandler;
import lombok.Data;

import java.util.List;


/**
 * 用户动态
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2024-01-31 15:23:10
 */
@Data
@TableName(value = "article", autoResultMap = true)
public class ArticleEntity extends BaseEntity {


    /**
     * 内容
     */
    @TableField("content")
    private String content;
    /**
     * 图片
     */
    @TableField(value = "img", typeHandler = BaseStringListTypeHandler.class)
    private List<String> img;
    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 是否公开 1-公开 0-私密
     */
    @TableField("open")
    private Integer open;

    @TableField("recommend")
    private String recommend;
    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 排序，数字越大越靠前
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 类型 类型 image  video
     */
    @TableField("type")
    private String type;
}
