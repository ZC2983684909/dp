package com.wxmblog.yanjian.common.rest.response.front.user;

import lombok.Data;

import java.util.List;

@Data
public class LabelInfoResponse {

    private String name;

    private String imgUrl;

    private List<LabelInfoResponse> childs;
}
