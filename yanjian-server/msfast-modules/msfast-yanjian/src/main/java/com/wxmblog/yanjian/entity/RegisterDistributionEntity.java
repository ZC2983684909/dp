package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户推广
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:08:10
 */
@Data
@TableName(value = "register_distribution", autoResultMap = true)
public class RegisterDistributionEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 推广码
     */
    @TableField("distribution_person_id")
    private String distributionPersonId;

    /**
     * 1-推广码 2-会员分享
     */
    @TableField("type")
    private String type;

    @TableField("pay_status")
    private String payStatus;

    @TableField("higher_pay_status")
    private String higherPayStatus;

    @TableField("reward_status")
    private String rewardStatus;

    @TableField("higher_reward_status")
    private String higherRewardStatus;
}
