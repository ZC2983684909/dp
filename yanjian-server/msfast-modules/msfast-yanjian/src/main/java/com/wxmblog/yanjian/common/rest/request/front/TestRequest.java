package com.wxmblog.yanjian.common.rest.request.front;

import com.wxmblog.base.auth.common.rest.request.wx.h5.MenuButtonVo;
import com.wxmblog.base.file.annotation.FileListSave;
import com.wxmblog.base.file.annotation.FileSave;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import lombok.Data;

import java.util.List;

@Data
public class TestRequest {

    private String imageA;

    private String imageB;

    private String urlA;

    private String urlB;

    @FileListSave
    private List<String> personalPhoto;

    private List<MenuButtonVo> menuButtonVo;

    private SendUserMessageVo sendUserMessageVo;

    private String type;

    private Integer offset;

    private Integer count;
}
