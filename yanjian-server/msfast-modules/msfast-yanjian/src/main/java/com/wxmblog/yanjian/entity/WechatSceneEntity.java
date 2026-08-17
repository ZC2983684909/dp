package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 微信小程序码
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-25 10:35:40
 */
@Data
@TableName(value = "wechat_scene", autoResultMap = true)
public class WechatSceneEntity extends BaseEntity {


                                                                                                            /**
             * 内容
             */
            @TableField("content")
            private String content;
            
}
