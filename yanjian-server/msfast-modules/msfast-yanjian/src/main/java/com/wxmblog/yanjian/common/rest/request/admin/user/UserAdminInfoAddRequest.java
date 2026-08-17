package com.wxmblog.yanjian.common.rest.request.admin.user;

import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.file.annotation.FileListSave;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-03-03 14:26
 **/
@Data
public class UserAdminInfoAddRequest {

    @ApiModelProperty(value = "id 新增时为空")
    private String id;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Length(min = 1, max = 8, message = "昵称为{min}-{max}位")
    @NotBlank
    private String nickName;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @Past(message = "出生日期必须是过去")
    @NotNull(message = "出生日期不可为空")
    private Date birthDate;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    @NotNull
    private Integer height;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    @NotBlank
    private String jobMes;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别 MALE(\"男\"),\n" +
            "    FEMALE(\"女\")")
    @NotNull
    private String sex;

    /**
     * 毕业学校
     */
    @ApiModelProperty(value = "毕业学校")
    @NotBlank
    private String school;

    /**
     * 最高学历
     */
    @ApiModelProperty(value = "最高学历 Specialty(\"专科\"),\n" +
            "    Undergraduate(\"本科\"),\n" +
            "    Master(\"硕士\"),\n" +
            "    Doctor(\"博士\")")
    @NotNull
    private String education;

    /**
     * 关于我
     */
    @ApiModelProperty(value = "关于我")
    @NotBlank
    @Size(min = 30,max = 3000,message = "关于我为{min}-{max}位")
    private String selfDescription;

    /**
     * 兴趣爱好
     */
    @ApiModelProperty(value = "兴趣爱好")
    @NotBlank
    private String fondTags;

    /**
     * 择偶要求
     */
    @ApiModelProperty(value = "择偶要求")
    @NotBlank
    @Size(min = 30,max = 3000, message = "择偶要求为{min}-{max}位")
    private String idealFriend;



    /**
     * 年薪
     */
    @ApiModelProperty(value = "年薪 Less_Ten(\"小于10w\"),\n" +
            "    Ten_Two(\"10w到20w\"),\n" +
            "    Two_Three(\"20w到30w\"),\n" +
            "    Three_Five(\"30w到50w\"),\n" +
            "    Five_Hundred(\"50w到100w\"),\n" +
            "    Greater_Hundred(\"大于100w\"),\n" +
            "    Secrecy(\"保密\")")
    private String salarys;

    /**
     * 相册
     */
    @ApiModelProperty(value = "相册")
    @FileListSave
    @NotEmpty
    private List<String> personalPhoto;

    /**
     * 用户状态 启用/停用/注销
     */
    @ApiModelProperty(value = "用户状态 ENABLE(\"启用\"),\n" +
            "    DISABLE(\"禁用\")")
    private FrUserStatusEnum status;
}
