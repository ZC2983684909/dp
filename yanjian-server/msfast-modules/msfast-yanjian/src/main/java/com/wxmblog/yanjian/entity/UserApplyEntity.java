package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
@Data
@TableName(value = "user_apply", autoResultMap = true)
public class UserApplyEntity extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 申请用户id
     */
    @TableField("apply_user_id")
    private String applyUserId;
    /**
     * 申请状态 1-申请中 2-通过 3-已拒绝 4-已过期
     */
    @TableField("status")
    private String status;

    //申请我的阅读状态 0-未处理 1-已处理
    @TableField("apply_read_status")
    private String applyReadStatus;

    //我的申请状态变更  0-未读 1-已读
    @TableField("apply_wait_status")
    private String applyWaitStatus;

    @TableField("apply_desc")
    private String applyDesc;
    //1-颜币解锁 2-vip解锁
    @TableField("apply_way")
    private String applyWay;
}
