package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 动态话题关联
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-13 17:54:41
 */
@Data
@TableName(value = "article_to_subject", autoResultMap = true)
public class ArticleToSubjectEntity extends BaseEntity {


                                                                                                            /**
             * 动态id
             */
            @TableField("article_id")
            private String articleId;
                                /**
             * 话题id
             */
            @TableField("subject_id")
            private String subjectId;
            
}
