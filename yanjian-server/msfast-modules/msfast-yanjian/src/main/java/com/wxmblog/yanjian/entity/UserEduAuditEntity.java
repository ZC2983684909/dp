package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.base.common.handler.BaseStringListTypeHandler;
import lombok.Data;

import java.util.List;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-16 21:00:20
 */
@Data
@TableName(value = "user_edu_audit", autoResultMap = true)
public class UserEduAuditEntity extends BaseEntity {

    /**
     * 学校
     */
    @TableField("school")
    private String school;
    /**
     * 学历
     */
    @TableField("education")
    private String education;
    /**
     * 证明材料
     */
    @TableField(value = "evidence", typeHandler = BaseStringListTypeHandler.class)
    private List<String> evidence;
    /**
     * 状态 学历认证  2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("status")
    private String status;

    @TableField("user_id")
    private String  userId;
}
