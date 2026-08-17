package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;


/**
 * 首页弹窗
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-12 14:37:29
 */
@Data
@TableName(value = "popup", autoResultMap = true)
public class PopupEntity extends BaseEntity {


    /**
     * 位置 home-首页
     */
    @TableField("location")
    private String location;
    /**
     * 标题
     */
    @TableField("title")
    private String title;
    /**
     * general-通用 subscribe-关注
     */
    @TableField("type")
    private String type;
    /**
     * 弹窗背景图
     */
    @TableField("image")
    private String image;
    /**
     * 跳转链接
     */
    @TableField("link")
    private String link;
    /**
     * 1.1天/次 2.周/次 3.月/次 4.一次
     */
    @TableField("popup_num")
    private Integer popupNum;
    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

    @TableField("attr")
    private String attr;

}
