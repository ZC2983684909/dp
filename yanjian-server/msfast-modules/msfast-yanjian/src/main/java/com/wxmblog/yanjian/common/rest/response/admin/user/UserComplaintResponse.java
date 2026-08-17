package com.wxmblog.yanjian.common.rest.response.admin.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserComplaintResponse {


    private String id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /**
     * 用户id
     */
    private String userId;

    private String userName;

    private String phone;

    /**
     * 投诉用户id
     */
    private String complaintId;

    private String complaintName;

    private String complaintPhone;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 图片
     */
    private List<String> img;

    //1-待处理 2-已处理
    private String status;

    private String type;

    private String articleContent;

    private List<String> articleImg;

    private String comment;


}
