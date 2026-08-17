package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.yanjian.common.handle.LabelTypeHandler;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import lombok.Data;

import java.util.Date;


/**
 * 弹窗记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-21 16:40:54
 */
@Data
@TableName(value = "user_profile", autoResultMap = true)
public class UserProfileEntity extends BaseEntity {


    /**
     * 自我描述
     */
    @TableField("self_description")
    private String selfDescription;

    /**
     * 理想对象
     */
    @TableField("ideal_friend")
    private String idealFriend;
    /**
     * union_id
     */
    @TableField("union_id")
    private String unionId;
    /**
     * 身份证信息
     */
    @TableField("id_card")
    private String idCard;
    /**
     * 姓名
     */
    @TableField("name")
    private String name;
    /**
     * 隐身时间
     */
    @TableField("invisible_date")
    private Date invisibleDate;
    /**
     * 用户实名相册截图
     */
    @TableField("real_photo")
    private String realPhoto;
    /**
     * 微信公众号的openid
     */
    @TableField("public_open_id")
    private String publicOpenId;
    /**
     * Base64 编码的图片
     */
    @TableField("img_base64")
    private String imgBase64;

    /**
     * 伪造微信号
     */
    @TableField("fake_wechat")
    private String fakeWechat;
    /**
     * 用户是否填写了无效信息
     */
    @TableField("has_invalid_info")
    private Boolean hasInvalidInfo;
    /**
     * 我的邀请码
     */
    @TableField("distribution_code")
    private String distributionCode;
    /**
     * 其他倾向居住城市
     */
    @TableField("tend_live_city")
    private String tendLiveCity;

    /**
     * 微信号
     */
    @TableField("wechat")
    private String wechat;
    /**
     * 剩余申请次数
     */
    @TableField("apply_count")
    private Integer applyCount;
    /**
     * 地址信息
     */
    @TableField("address")
    private String address;

    @TableField("wechat_open")
    private Boolean wechatOpen;

    @TableField("violation_msg")
    private String violationMsg;

    //warning_msg
    @TableField("warning_msg")
    private String warningMsg;
}
