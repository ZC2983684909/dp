package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.base.common.handler.BaseStringListTypeHandler;
import lombok.Data;

import java.util.List;


/**
 * 投诉
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-12-09 17:22:36
 */
@Data
@TableName(value = "complaint", autoResultMap = true)
public class ComplaintEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 投诉用户id
     */
    @TableField("complaint_id")
    private String complaintId;


    //type
    @TableField("type")
    private String type;
    /**
     * 投诉内容
     */
    @TableField("content")
    private String content;
    /**
     * 图片
     */
    @TableField(value = "img", typeHandler = BaseStringListTypeHandler.class)
    private List<String> img;

    //1-待处理 2-已处理
    @TableField("status")
    private String status;



}
