package com.wxmblog.yanjian.common.rest.request.front.user;

import com.wxmblog.base.file.annotation.FileListSave;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ComplaintRequest {

    @NotBlank(message = "请输入投诉对象")
    private String complaintId;

    @NotBlank(message = "请输入投诉内容")
    private String content;

    @NotBlank(message = "请选择投诉类型")
    @ApiModelProperty(value = "投诉类型 1-用户 2-动态 3-评论")
    private String type;

    @FileListSave
    private List<String> img;
}
