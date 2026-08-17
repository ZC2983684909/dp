package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 用户推广人
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:19:38
 */
@Data
@TableName(value = "distribution_person", autoResultMap = true)
public class DistributionPersonEntity extends BaseEntity {


    /**
     * 用户姓名
     */
    @TableField("name")
    private String name;
    /**
     * 推广码
     */
    @TableField("distribution_code")
    private String distributionCode;

}
