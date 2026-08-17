package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 弹窗记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-03 15:53:38
 */
@Data
@TableName(value = "popup_records", autoResultMap = true)
public class PopupRecordsEntity extends BaseEntity {


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;


    @TableField("popup_id")
    private String popupId;

}
