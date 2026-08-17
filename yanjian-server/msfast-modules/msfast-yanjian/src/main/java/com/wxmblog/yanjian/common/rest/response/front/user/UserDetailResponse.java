package com.wxmblog.yanjian.common.rest.response.front.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.yanjian.common.rest.response.front.article.UserArticleViewResponse;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserDetailResponse {


    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "ip属地")
    private String city;

    @ApiModelProperty(value = "区")
    private String county;

    @ApiModelProperty(value = "是否主城")
    private Boolean mainCity;

    @ApiModelProperty(value = "是否新人")
    private Boolean isNew;

    //活跃状态
    @ApiModelProperty(value = "活跃状态 1-在线 2-刚刚活跃 3-最近活跃 4-不活跃")
    private String activeStatus;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy年MM月dd日")
    private Date birthFormatDate;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer height;

    @ApiModelProperty(value = "身高")
    private String heightFormat;

    @ApiModelProperty(value = "体重")
    private Integer weight;

    @ApiModelProperty(value = "体重")
    private String weightFormat;

    @ApiModelProperty(value = "年龄")
    private Integer age;


    @ApiModelProperty(value = "距离")
    private BigDecimal distance;

    @ApiModelProperty(value = "距离")
    private String distanceFormat;

    @ApiModelProperty(value = "信息")
    private String informationList;

    @ApiModelProperty(value = "信息")
    private String informationBaseList;

    /**
     * 家乡信息
     */
    @ApiModelProperty(value = "家乡信息")
    private String homeTown;

    /**
     * 居住城市
     */
    @ApiModelProperty(value = "居住城市")
    private String residentialCity;

    /**
     * 其他倾向居住城市
     */
    @ApiModelProperty(value = "其他倾向居住城市")
    private String tendLiveCity;

    /**
     * 兴趣标签
     */
    @ApiModelProperty(value = "兴趣标签")
    private LabelVo fondTags;

    @ApiModelProperty(value = "兴趣标签,分割的")
    private String fondTagsFormat;

    @ApiModelProperty(value = "兴趣标签,列表的")
    private List<LabelInfoResponse> fondTagsList;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    private String jobMes;

    /**
     * 年薪
     */
    @ApiModelProperty(value = "年薪")
    private String salarys;


    @ApiModelProperty(value = "相机图片")
    private String cameraImg;
    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "是否微信公开 是否可以直接解锁")
    private Boolean wechatOpen;

    @ApiModelProperty(value = "是否关注过")
    private Boolean isStar;

    @ApiModelProperty(value = "关注数")
    private long starNum;

    @ApiModelProperty(value = "被关注数 粉丝数")
    private long starByNum;

    @ApiModelProperty(value = "动态数")
    private long articleNum;

    @ApiModelProperty(value = "申请状态 0-未申请 1-申请中 2-通过 3-已拒绝 4-已过期")
    private String applyStatus;

    /**
     * 自我描述
     */
    @ApiModelProperty(value = "自我描述")
    private String selfDescription;

    /**
     * 个人照片
     */
    @ApiModelProperty(value = "个人照片")
    private List<PhotoResultVo> personalPhoto;

    /**
     * 理想对象
     */
    @ApiModelProperty(value = "理想对象")
    private String idealFriend;

    /**
     * 身份认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "身份认证")
    private String idAuth;

    /**
     * 学历认证  1-未认证 2-认证中 3-通过 4-拒绝 5-失败
     */
    @ApiModelProperty(value = "学历认证")
    private String eduAuth;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
    @ApiModelProperty(value = "照片认证")
    private String photoAuth;

    @ApiModelProperty(value = "毕业院校")
    private String school;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "朋友圈概览信息")
    private UserArticleViewResponse userArticleViewResponse;

    @ApiModelProperty(value = "我的学历认证")
    private String myEduAuth;

    @ApiModelProperty(value = "我的身份认证")
    private String myIdAuth;

    //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
    @ApiModelProperty(value = "我的照片认证")
    private String myPhotoAuth;

    @ApiModelProperty(value = "是否可以聊天")
    private Boolean isChat;

    /**
     * 最佳帧与相册首页相似度分数
     */
    @ApiModelProperty(value = "最佳帧与相册首页相似度分数")
    private Float similarity;

    @ApiModelProperty(value = "是否可以查看原相机")
    private Boolean seeCameraImg;

}
