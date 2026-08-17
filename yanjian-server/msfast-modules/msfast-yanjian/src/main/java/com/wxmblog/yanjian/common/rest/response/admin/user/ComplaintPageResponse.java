package com.wxmblog.yanjian.common.rest.response.admin.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2023-02-13 16:16
 **/

@Data
public class ComplaintPageResponse {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /**
     * 用户状态 启用/停用/注销
     */
    @ApiModelProperty(value = "处理状态")
    private String status;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "举报人昵称")
    private String nickName;

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "被举报人昵称")
    private String complaintNickName;

    @ApiModelProperty(value = "被举报人手机")
    private String complaintPhone;

    @ApiModelProperty(value = "内容")
    private String content;

    private String type;


}
