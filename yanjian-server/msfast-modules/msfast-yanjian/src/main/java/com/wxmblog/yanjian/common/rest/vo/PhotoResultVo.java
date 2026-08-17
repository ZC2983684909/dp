package com.wxmblog.yanjian.common.rest.vo;

import lombok.Data;

@Data
public class PhotoResultVo {

    private Float similarity;

    private String url;

    private Boolean isMatch;
}
