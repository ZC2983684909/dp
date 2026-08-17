package com.wxmblog.yanjian.common.rest.vo;

import lombok.Data;

@Data
public class AddNatureVisitVo {

    private String userId;

    private String visitUserId;

    //1-浏览 2-关注
    private String type;
}
