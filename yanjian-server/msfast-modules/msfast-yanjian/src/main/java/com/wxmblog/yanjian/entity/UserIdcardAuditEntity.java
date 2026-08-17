package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 00:32:20
 */
@Data
@TableName(value = "user_idcard_audit", autoResultMap = true)
public class UserIdcardAuditEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 状态 身份证认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("status")
    private String status;
    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;
    /**
     * 姓名
     */
    @TableField("name")
    private String name;

}
