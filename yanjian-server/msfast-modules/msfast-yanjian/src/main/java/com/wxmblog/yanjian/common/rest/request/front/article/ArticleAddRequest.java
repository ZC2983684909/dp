package com.wxmblog.yanjian.common.rest.request.front.article;

import com.wxmblog.base.file.annotation.FileListSave;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
public class ArticleAddRequest {

    @Length(max = 500, message = "内容不能超过500字")
    private String content;

    @Size(max = 9, message = "最多9张图片")
    @FileListSave
    private List<String> img;

    private String code;

    @ApiModelProperty(value = "话题列表")
    private Set<String> subjectIdList;

    @ApiModelProperty(value = "动态类型 类型 image  video")
    private String type;

}
