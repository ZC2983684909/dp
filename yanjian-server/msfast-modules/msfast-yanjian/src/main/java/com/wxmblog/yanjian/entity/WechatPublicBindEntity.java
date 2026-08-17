package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import lombok.Data;


/**
 * 微信公众号绑定
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-09 17:27:47
 */
@Data
@TableName(value = "wechat_public_bind", autoResultMap = true)
public class WechatPublicBindEntity extends BaseEntity {


    /**
     *
     */
    @TableField("open_id")
    private String openId;
    /**
     *
     */
    @TableField("union_id")
    private String unionId;

}
