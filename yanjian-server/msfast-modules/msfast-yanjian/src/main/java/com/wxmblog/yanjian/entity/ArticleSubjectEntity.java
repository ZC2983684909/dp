package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 话题
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-13 17:54:41
 */
@Data
@TableName(value = "article_subject", autoResultMap = true)
public class ArticleSubjectEntity extends BaseEntity {


    /**
     * 话题标题
     */
    @TableField("title")
    private String title;
    /**
     * 图片
     */
    @TableField("img")
    private String img;
    /**
     * 话题描述
     */
    @TableField("description_info")
    private String descriptionInfo;
    /**
     * 访问量
     */
    @TableField("visit_count")
    private Integer visitCount;
    /**
     * 讨论量
     */
    @TableField("discuss_count")
    private Integer discussCount;
    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

}
