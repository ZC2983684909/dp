package com.wxmblog.yanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wxmblog.base.common.entity.BaseEntity;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.yanjian.common.handle.LabelTypeHandler;
import com.wxmblog.yanjian.common.handle.PhotoListHandler;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-21 16:41:55
 */
@Data
@TableName(value = "t_user", autoResultMap = true)
public class TUserEntity extends BaseEntity {

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
     * 体重
     */
    @TableField("weight")
    private Integer weight;

    /**
     * 家乡信息
     */
    @TableField("home_town")
    private String homeTown;
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
     * open_id
     */
    @TableField("open_id")
    private String openId;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    /**
     * 身份认证 1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("id_auth")
    private String idAuth;
    /**
     * 学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @TableField("edu_auth")
    private String eduAuth;
    /**
     * 用户相册认证 1-未认证 2-有人脸 3-通过 4-拒绝 5-失败 6-警告
     */
    @TableField("photo_auth")
    private String photoAuth;
    /**
     * 用户状态
     */
    @TableField("status")
    private FrUserStatusEnum status;
    /**
     * 毕业学校
     */
    @TableField("school")
    private String school;
    /**
     * 学历
     */
    @TableField("education")
    private String education;
    /**
     * 最近登陆时间
     */
    @TableField("lately_time")
    private Date latelyTime;
    /**
     * 1-隐身 2-不隐身
     */
    @TableField("invisible")
    private String invisible;
    /**
     * 用户类型 1-注册用户 2-内置用户
     */
    @TableField("user_type")
    private String userType;
    /**
     * 用户排序
     */
    @TableField("sort")
    private Integer sort;

    @TableField("nature_sort")
    private Integer natureSort;
    /**
     * 省份
     */
    @TableField("province")
    private String province;
    /**
     * 城市
     */
    @TableField("city")
    private String city;
    /**
     * 区/县
     */
    @TableField("county")
    private String county;

    @TableField("main_city")
    private Boolean mainCity;
    /**
     * 经度
     */
    @TableField("lon")
    private String lon;
    /**
     * 纬度
     */
    @TableField("lat")
    private String lat;

    /**
     * 居住城市
     */
    @TableField("residential_city")
    private String residentialCity;

    /**
     * 个人照片
     */
    @TableField(value = "personal_photo", typeHandler = PhotoListHandler.class)
    private List<PhotoResultVo> personalPhoto;

    /**
     * 最佳帧与相册首页相似度分数
     */
    @TableField("similarity")
    private Float similarity;

    @TableField("camera_img")
    private String cameraImg;

    /**
     * 兴趣标签
     */
    @TableField(value = "fond_tags", typeHandler = LabelTypeHandler.class)
    private LabelVo fondTags;

    @TableField("profile_id")
    private String profileId;

    @TableField("recommend")
    private String recommend;
}
