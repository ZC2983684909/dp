package com.wxmblog.yanjian.common.rest.request.front.user;

import com.wxmblog.base.file.annotation.FileListSave;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class EduApplyRequest {

    @ApiModelProperty(value = "学校名称")
    @NotBlank(message = "学校名称不可为空")
    private String school;

    @ApiModelProperty(value = "学历")
    @NotBlank(message = "学历不可为空")
    private String education;

    @ApiModelProperty(value = "证明图片")
    @NotEmpty(message = "证明图片不可为空")
    @FileListSave
    private List<String> evidence;
}
