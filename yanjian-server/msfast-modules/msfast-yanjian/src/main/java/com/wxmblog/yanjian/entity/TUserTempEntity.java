package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.handler.BaseStringListTypeHandler;
import com.wxmblog.yanjian.common.handle.LabelTypeHandler;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-13 23:37:01
 */
@Data
@TableName(value = "t_user_temp", autoResultMap = true)
public class TUserTempEntity extends BaseEntity {

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;
    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;
    /**
     * 性别
     */
    @TableField("sex")
    private String sex;
    /**
     * 出生日期
     */
    @TableField("birth_date")
    private Date birthDate;
    /**
     * 身高
     */
    @TableField("height")
    private Integer height;
    /**
     * 家乡信息
     */
    @TableField("home_town")
    private String homeTown;
    /**
     * 居住城市
     */
    @TableField("residential_city")
    private String residentialCity;
    /**
     * 其他倾向居住城市
     */
    @TableField("tend_live_city")
    private String tendLiveCity;
    /**
     * 兴趣标签
     */
    @TableField(value = "fond_tags", typeHandler = LabelTypeHandler.class)
    private LabelVo fondTags;
    /**
     * 职业
     */
    @TableField("job_mes")
    private String jobMes;
    /**
     * 年薪
     */
    @TableField("salarys")
    private String salarys;
    /**
     * 微信号
     */
    @TableField("wechat")
    private String wechat;
    /**
     * 自我描述
     */
    @TableField("self_description")
    private String selfDescription;
    /**
     * 个人照片
     */
    @TableField(value = "personal_photo", typeHandler = BaseStringListTypeHandler.class)
    private List<String> personalPhoto;
    /**
     * 理想对象
     */
    @TableField("ideal_friend")
    private String idealFriend;
    /**
     * open_id
     */
    @TableField("open_id")
    private String openId;
    /**
     * union_id
     */
    @TableField("union_id")
    private String unionId;
    /**
     * 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("id_auth")
    private String idAuth;
    /**
     * 学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("edu_auth")
    private String eduAuth;

    /**
     * 用户状态 启用/停用/注销
     */
    @TableField("status")
    private FrUserStatusEnum status;

    //id_card
    @TableField("id_card")
    private String idCard;

    @TableField("name")
    private String name;

    @TableField("school")
    private String school;

    @TableField("education")
    private String education;

    @TableField("lately_time")
    private Date latelyTime;

    /*
    1-隐身 2-不隐身
     */
    @TableField("invisible")
    private String invisible;

    @TableField("invisible_date")
    private Date invisibleDate;

    @TableField("apply_count")
    private Integer applyCount;

    @TableField("user_type")
    private String userType;

    //用户相册认证
    @TableField("photo_auth")
    private String photoAuth;

    //用户实名相册截图
    @TableField(value = "real_photo", typeHandler = BaseStringListTypeHandler.class)
    private List<String> realPhoto;

    @TableField("phone")
    private String phone;

    @TableField("public_open_id")
    private String publicOpenId;

    /**
     * 人脸识别最佳帧的Base64编码
     */
    @TableField("img_base64")
    private String imgBase64; // 使用 String 类型，兼容 MEDIUMTEXT

    /**
     * 最佳帧与相册首页相似度分数
     */
    @TableField("similarity")
    private Float similarty;

    @TableField("fake_wechat")
    private String fakeWechat;

    //用户是否填写了无效信息
    @TableField("has_invalid_info")
    private Boolean hasInvalidInfo;

    @TableField("distribution_code")
    private String distributionCode;
}
