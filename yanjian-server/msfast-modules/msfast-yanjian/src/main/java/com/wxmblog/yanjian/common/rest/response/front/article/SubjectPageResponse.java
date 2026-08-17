package com.wxmblog.yanjian.common.rest.response.front.article;

import lombok.Data;

@Data
public class SubjectPageResponse {

    private String id;

    private String title;

    private String img;

    private String descriptionInfo;

    private Integer visitCount;

    private String visitCountDesc;

    private Integer discussCount;

    private String discussCountDesc;

}
