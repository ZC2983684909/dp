package com.wxmblog.yanjian.common.rest.request.front.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wxmblog.base.common.annotation.NotEmptyOrNull;
import com.wxmblog.base.file.annotation.FileListSave;
import com.wxmblog.base.file.annotation.FileSave;
import com.wxmblog.yanjian.common.rest.vo.LabelVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
public class UserInfoEditRequest {

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Length(max = 8, message = "昵称字数过多")
    @NotEmptyOrNull(message = "昵称不可为空")
    private String nickName;

    /**
     * 出生日期
     */
    @Past(message = "出生日期必须小于当前时间")
    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer height;
    //体重

    @ApiModelProperty(value = "体重")
    private Integer weight;

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

    /**
     * 相片
     */
    @ApiModelProperty(value = "原相机视频")
    @FileSave
    private String cameraImg;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    @NotEmptyOrNull(message = "职业不可为空")
    private String jobMes;

    /**
     * 年薪
     */
    @ApiModelProperty(value = "年薪")
    private String salarys;

    /**
     * 微信号 可以传null 但是不能传空字符
     */

    @ApiModelProperty(value = "微信号")
    @NotEmptyOrNull(message = "微信号不可为空")
    private String wechat;

    @ApiModelProperty(value = "微信是否公开")
    private Boolean wechatOpen;

    @ApiModelProperty(value = "手机号")
    @NotEmptyOrNull(message = "手机号不可为空")
    private String phone;

    /**
     * 自我描述
     */
    //@NotBlank(message = "自我描述不可为空")
    @ApiModelProperty(value = "个人签名")
    @Length(max = 2000, message = "个人签名最多{max}字")
    private String selfDescription;

    /**
     * 个人照片
     */
    @ApiModelProperty(value = "个人照片")
    @Size(min = 1, message = "个人照片至少上传{min}张")
    @Size(max = 20, message = "个人照片最多上传{max}张")
    @FileListSave
    private List<String> personalPhoto;

    /**
     * 理想对象
     */
    @ApiModelProperty(value = "理想对象")
    @Length(min = 20, message = "理想对象至少{min}位")
    @Length(max = 2000, message = "理想对象最多{max}位")
    private String idealFriend;

}
