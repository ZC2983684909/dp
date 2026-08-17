package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 话题收藏
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-15 22:18:54
 */
@Data
@TableName(value = "subject_star", autoResultMap = true)
public class SubjectStarEntity extends BaseEntity {


                                                                                                            /**
             * 用户id
             */
            @TableField("member_id")
            private String memberId;
                                /**
             * 话题id
             */
            @TableField("subject_id")
            private String subjectId;
            
}
